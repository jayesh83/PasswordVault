package com.example.passwordvault.util

import com.google.firebase.storage.FirebaseStorage

object FirebaseStorage {
    fun storage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }
}