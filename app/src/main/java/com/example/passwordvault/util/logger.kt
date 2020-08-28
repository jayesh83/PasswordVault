package com.example.passwordvault.util

import android.util.Log

object logger {
    fun log(tag: String, value: String) {
        Log.e(tag, value)
    }
}