package com.example.passwordvault.util

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseDB {
    fun database(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    fun getUsersDB(): DatabaseReference {
        return database().getReference("users")
    }
}