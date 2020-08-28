package com.example.passwordvault.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.widget.Toast

class CallReceiverRestarterOnInternetJob : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        Toast.makeText(applicationContext, "Internet connection", Toast.LENGTH_SHORT).show()
        sendBroadcast(Intent(restartBroadcastAction))
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}