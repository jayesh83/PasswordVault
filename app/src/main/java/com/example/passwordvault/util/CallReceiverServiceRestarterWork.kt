package com.example.passwordvault.util

import android.content.Context
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters

class CallReceiverServiceRestarterWork(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Toast.makeText(applicationContext, "Internet", Toast.LENGTH_SHORT).show()
        ServiceStarter.startCallReceiverService(applicationContext)
        return Result.success()
    }

}
