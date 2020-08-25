package com.example.passwordvault.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.passwordvault.receivers.FIVEMIN_RECORDING_STARTER
import com.example.passwordvault.receivers.FiveMinRecordingReceiver

class Scheduler {
    companion object {
        fun schedule5minRecorder(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val interval = 7000L
            val intent = Intent(context, FiveMinRecordingReceiver::class.java)
            intent.action = FIVEMIN_RECORDING_STARTER
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, interval, 60000, pendingIntent)
        }

        fun cancel5minRecorder(context: Context){
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, FiveMinRecordingReceiver::class.java)
            intent.action = FIVEMIN_RECORDING_STARTER
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.cancel(pendingIntent)
        }
    }
}