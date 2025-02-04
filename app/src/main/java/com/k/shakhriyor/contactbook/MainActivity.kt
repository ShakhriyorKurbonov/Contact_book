package com.k.shakhriyor.contactbook

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.k.shakhriyor.contactbook.fragments.ContactListFragment
import com.k.shakhriyor.contactbook.other.callLogReadPermission
import com.k.shakhriyor.contactbook.other.contactReadPermission

class MainActivity : AppCompatActivity() {
    private var readContactPermission=registerForActivityResult(ActivityResultContracts.RequestPermission()){}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        if (!contactReadPermission(this)){
            readContactPermission.launch(Manifest.permission.READ_CONTACTS)
        }





    }
}