package com.example.passwordvault.util

import android.content.Context
import android.content.SharedPreferences

const val LATEST_RECORD: String = "LatestRecord"

object PreferenceUtil {

    fun writeToLatestRecord(outputFile: String, context: Context) {
        val editor = sharedPreference(context).edit()
        editor.putString(LATEST_RECORD, outputFile).apply()
    }

    fun latestRecord(context: Context): String? {
        return sharedPreference(context).getString(LATEST_RECORD, "error")
    }

    private fun sharedPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            "com.passwordvault.newRecords",
            Context.MODE_PRIVATE
        )
    }
}