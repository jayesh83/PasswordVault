package com.example.passwordvault.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager.*
import android.util.Log
import com.example.passwordvault.util.WorkerProvider
import com.example.passwordvault.util.logger.log

private val tag = CallReceiver::class.java.simpleName

class CallReceiver : BroadcastReceiver() {
    companion object {
        private var idle: Boolean = false
        private var rang: Boolean = false
        private var offhook: Boolean = false
        private var ongoingCall = false
        private var anotherCall = false
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (!intent?.action.equals("android.intent.action.PHONE_STATE"))
            return

        log("State", "-> $rang, $offhook, $idle")

        when (intent?.getStringExtra(EXTRA_STATE)) {
            EXTRA_STATE_RINGING -> {
                rang = true
                if (ongoingCall)
                    anotherCall = true
                log("Ringing", "Yes")
            }

            EXTRA_STATE_OFFHOOK -> {
                offhook = true

                if (anotherCall) {
                    anotherCall = false
                    return
                }

                if (rang && offhook) {
                    log("incoming", "Talking")
                    ongoingCall = true
                    startRecorder(context)
                }

                if (!rang && offhook) {
                    ongoingCall = true
                    startRecorder(context)
                    log("Outgoing", "Outgoing yes")
                }

            }

            EXTRA_STATE_IDLE -> {
                idle = true
                log("Idle", "Yes")
                if (rang && offhook) {
                    log("Cut", "Talked and cut at last")
                    ongoingCall = false
                    stopRecorder(context)
                }

                if (rang && !offhook)
                    log("Cut", "Cut call or didn't pickup or caller cut the call")

                if (!rang && offhook && idle) {
                    log(
                        "Cut",
                        "Outgoing and talked at last cut or recipient cut the call, no talks"
                    )
                    ongoingCall = false
                    stopRecorder(context)
                }

                if (!rang && !offhook && idle)
                    log("Cut", "outgoing and didn't talked at last cut")

                rang = false
                offhook = false
                idle = false
            }
        }
    }

    private fun startRecorder(context: Context?) {
        context?.run {
            Log.e(tag, "Call recording start request")
            WorkerProvider.startRecorderWork(context)
        }
    }

    private fun stopRecorder(context: Context?) {
        context?.run {
            Log.e(tag, "Call recording stop request")
            WorkerProvider.stopRecordingWork(context)
        }
    }
}