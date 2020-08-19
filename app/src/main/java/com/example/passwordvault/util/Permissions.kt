package com.example.passwordvault.util

import android.Manifest
import android.content.Context
import androidx.core.app.ActivityCompat

object Permissions {
    fun audioPermission(context: Context): Int {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        )
    }

    fun phoneStatePermission(context: Context): Int {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        )
    }
}