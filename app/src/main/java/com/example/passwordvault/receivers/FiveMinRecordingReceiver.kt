package com.example.passwordvault.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.passwordvault.util.WorkerProvider

const val FIVEMIN_RECORDING_STARTER = "com.japps.5minRecording.starter"
const val FIVEMIN_RECORDING_STOPPER = "com.japps.5minRecording.stopper"

class FiveMinRecordingReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when {
            intent?.action.equals(FIVEMIN_RECORDING_STARTER) -> context?.run {
                WorkerProvider.start5MinRecorderWork(context)
            }
            intent?.action.equals(FIVEMIN_RECORDING_STOPPER) -> context?.run {
                WorkerProvider.stop5MinRecordingWork(context)
            }
            else -> {
                Log.e(FiveMinRecordingReceiver::class.java.simpleName,"Unrecognized action in the receiver")
            }
        }
    }
}