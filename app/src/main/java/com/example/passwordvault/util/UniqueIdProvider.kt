package com.example.passwordvault.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings.Secure
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat

object UniqueIdProvider {

    fun getThisUniquePhone(context: Context): String {
        return getDetails(context)
    }

    private fun getDetails(context: Context): String {
        val RELEASE = Build.VERSION.RELEASE
        val DEVICE = Build.DEVICE
        val MODEL = Build.MODEL
        val BRAND = Build.BRAND
        val MANUFACTURER = Build.MANUFACTURER

        return "$MANUFACTURER $BRAND $MODEL Android $RELEASE $DEVICE ${getUniqueID(context)}"
    }

    private fun getUniqueID(context: Context): String {
        Log.e("ID", "context -> $context")
        var imei = ""
        val phoneStatePermission =
            (Permissions.phoneStatePermission(context) == PackageManager.PERMISSION_GRANTED)
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (phoneStatePermission) {
                telephonyManager?.let {
                    imei = try {
                        Log.e("ID", "YesI - ${Secure.getString(context.contentResolver, Secure.ANDROID_ID)}")
                        telephonyManager.imei
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("ID", "YesA")
                        Secure.getString(context.contentResolver, Secure.ANDROID_ID)
                    }
                }
            } else {
                Log.e("ID", "req A")
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
            Log.e("ID", "req B")
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