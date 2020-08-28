package com.example.passwordvault.service

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import com.example.passwordvault.notification.ServiceNotificationManager
import com.example.passwordvault.receivers.CallReceiver
import com.example.passwordvault.util.ServiceStarter
import com.example.passwordvault.util.logger.log

private val tag = CallReceiverService::class.java.simpleName
const val restartBroadcastAction = "com.example.passwordvault.service.restartCallReceiverService"

class CallReceiverService : IntentService(CallReceiverService::class.java.simpleName) {
    private lateinit var callReceiverBroadcast: CallReceiver

    override fun onCreate() {
        log(tag, "Created")
        registerCallReceiver()
        initializeNotification()
    }

    private fun registerCallReceiver() {
        isReceiverRegistered = true
        callReceiverBroadcast = CallReceiver()
        val intentFilter = IntentFilter("android.intent.action.PHONE_STATE")
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL")
        registerReceiver(callReceiverBroadcast, intentFilter)
    }

    private fun initializeNotification() {
        val notification = ServiceNotificationManager(applicationContext).createNotification()
        startForeground(NOTIFICATION_ID_CALLRECEIVER_SERVICE, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(restartBroadcastAction))
            log(tag, "Restarted")
        if (!isReceiverRegistered)
            registerCallReceiver()
        return START_STICKY
    }

    override fun onHandleIntent(intent: Intent?) {
        log(tag, "Handling the incoming intent")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onTaskRemoved(rootIntent: Intent?) {
        unregisterCallReceiverAndRestartReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterCallReceiverAndRestartReceiver()
    }

    private fun unregisterCallReceiver() {
        if (isReceiverRegistered) {
            isReceiverRegistered = false
            unregisterReceiver(callReceiverBroadcast)
        }
    }

    private fun unregisterCallReceiverAndRestartReceiver() {
        unregisterCallReceiver()
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val interval = System.currentTimeMillis() + 5000L
        val intent = Intent(restartBroadcastAction)
        val pendingIntent = PendingIntent.getBroadcast(baseContext, 0, intent, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                interval,
                pendingIntent
            )
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, interval, pendingIntent)
        }
        log(tag, "Unregistering receiver\nRestarting Call receiver Service in 5sec")
    }

    companion object {
        private var isReceiverRegistered = false
        private const val NOTIFICATION_ID_CALLRECEIVER_SERVICE = 109
    }
}

class CallReceiverServiceRestarter : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if (!(intent?.action == restartBroadcastAction || intent?.action == "android.intent.action.BOOT_COMPLETED"))
            return

        context?.let {
            ServiceStarter.startCallReceiverService(it)
        }

    }
}