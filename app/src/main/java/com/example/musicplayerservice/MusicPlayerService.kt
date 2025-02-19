package com.example.musicplayerservice

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.IBinder
import android.util.Log

class MusicPlayerService : Service(){
    private var mediaPlayer:MediaPlayer?=null
    override fun onCreate() {
        super.onCreate()
        Log.d("MusicPlayerService", "Service created")
    }

    private val binder= object : MusicServiceAIDL.Stub() {
//        override fun play() {
//            if(mediaPlayer==null){
//                val mediaPlayerTask = AsyncTask.execute {
//                    mediaPlayer = MediaPlayer.create(this@MusicPlayerService, R.raw.wake_me_up)
//                    mediaPlayer?.start()
//                    Log.d("MusicPlayerService", "Music started in background")
//                }
//            }
//            else{
//                mediaPlayer?.start()
//                Log.e("MusicPlayerService", "Failed to initialize media player")
//            }
//        }
    override fun play() {
    Log.d("MusicPlayerService", "Play Method Invoked")
    if (mediaPlayer == null) {
        try {
            mediaPlayer = MediaPlayer.create(this@MusicPlayerService, R.raw.wake_me_up).apply {
                isLooping=true
            }
            mediaPlayer?.start()
            Log.d("MusicPlayerService", "Music started in background")
        } catch (e: Exception) {
            Log.e("MusicPlayerService", "Error initializing MediaPlayer: ${e.message}")
        }
    } else {
        if (mediaPlayer?.isPlaying == true) {
            Log.d("MusicPlayerService", "Music is already playing.")
        } else {
            mediaPlayer?.start()
            Log.d("MusicPlayerService", "Resumed music playback.")
        }
    }
}

        override fun pause() {
            mediaPlayer?.pause()
        }
        
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer=null
        Log.d("MusicPlayerService", "MediaPlayer released")
    }
}