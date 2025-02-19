package com.example.musicplayerservice

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class MainActivity : Activity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val serviceIntent = Intent(this, MusicPlayerService::class.java)
        startService(serviceIntent)
    }

}