package com.example.myschool.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SchoolReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action.equals("android.intent.action.BOOT_COMPLETED", ignoreCase = true)) {
            context.startService(Intent(context, SchoolService::class.java))
        }
    }
}