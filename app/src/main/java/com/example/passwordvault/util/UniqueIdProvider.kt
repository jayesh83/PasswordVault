package com.example.passwordvault.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings.Secure
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat

object UniqueIdProvider {
    fun getUniqueID(context: Context): String{
        var imei = ""
        val phoneStatePermission =
            (Permissions.phoneStatePermission(context) == PackageManager.PERMISSION_GRANTED)
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (phoneStatePermission) {
                telephonyManager?.run {
                    imei = try {
                        telephonyManager.imei
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Secure.getString(context.contentResolver, Secure.ANDROID_ID)
                    }
                }
            } else {
                requestPhoneState(context)
            }
        } else if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (telephonyManager != null) {
                imei = telephonyManager.deviceId
            }
        } else {
            requestPhoneState(context)
        }
        return imei
    }

    private fun requestPhoneState(context: Context) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.READ_PHONE_STATE),
            1010
        )
    }
}