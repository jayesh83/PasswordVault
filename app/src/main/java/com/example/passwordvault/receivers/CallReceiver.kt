package com.example.passwordvault.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager.*
import android.util.Log
import com.example.passwordvault.util.WorkerProvider

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

        logger("State", "-> $rang, $offhook, $idle")

        when (intent?.getStringExtra(EXTRA_STATE)) {
            EXTRA_STATE_RINGING -> {
                rang = true
                if (ongoingCall)
                    anotherCall = true
                logger("Ringing", "Yes")
            }

            EXTRA_STATE_OFFHOOK -> {
                offhook = true

                if (anotherCall) {
                    anotherCall = false
                    return
                }

                if (rang && offhook) {
                    logger("incoming", "Talking")
                    ongoingCall = true
                    startRecorder(context)
                }

                if (!rang && offhook) {
                    ongoingCall = true
                    startRecorder(context)
                    logger("Outgoing", "Outgoing yes")
                }

            }

            EXTRA_STATE_IDLE -> {
                idle = true
                logger("Idle", "Yes")
                if (rang && offhook) {
                    logger("Cut", "Talked and cut at last")
                    ongoingCall = false
                    stopRecorder(context)
                }

                if (rang && !offhook)
                    logger("Cut", "Cut call or didn't pickup or caller cut the call")

                if (!rang && offhook && idle) {
                    logger(
                        "Cut",
                        "Outgoing and talked at last cut or recipient cut the call, no talks"
                    )
                    ongoingCall = false
                    stopRecorder(context)
                }

                if (!rang && !offhook && idle)
                    logger("Cut", "outgoing and didn't talked at last cut")

                rang = false
                offhook = false
                idle = false
            }
        }
    }

    private fun logger(tag: String, msg: String) {
        Log.e(tag, msg)
    }

    private fun startRecorder(context: Context?) {
        context?.run {
            Log.e("WorkerReceiver", "Enqueued")
            WorkerProvider.startRecorderWork(context)
        }
    }

    private fun stopRecorder(context: Context?) {
        context?.run {
            WorkerProvider.stopRecordingWork(context)
        }
    }
}