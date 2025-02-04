package com.k.shakhriyor.contactbook.other

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

fun callLogReadPermission(context: Context):Boolean{
    return ContextCompat.checkSelfPermission(context,Manifest.permission.READ_CALL_LOG)==PackageManager.PERMISSION_GRANTED
}

fun contactReadPermission(context: Context):Boolean{
    return ContextCompat.checkSelfPermission(context,Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_GRANTED
}

fun callPhonePermission(context: Context):Boolean{
    return ContextCompat.checkSelfPermission(context,Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED
}

fun sendSmsPermission(context: Context):Boolean{
    return ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)==PackageManager.PERMISSION_GRANTED
}




