package com.example.musicplayerservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat


class MusicPlayerService : Service(){
    companion object{
        private val channelId = "MusicPlayerServiceChannel"
    }
    private var mediaPlayer:MediaPlayer?=null
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Music Player")
            .setContentText("Music is playing...")
            .setSmallIcon(R.drawable.baseline_music_video_24)
            .build()
        startForeground(1,notification)
        Log.d("MusicPlayerService", "Service created")
    }

    private val binder= object : MusicServiceAIDL.Stub() {
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
//        mediaPlayer?.release()
//        mediaPlayer=null
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        stopForeground(true)
        Log.d("MusicPlayerService", "MediaPlayer released")
    }

    private fun createNotificationChannel() {
        val name = "Music Player Service"
        val descriptionText = "Notification for Music Player Service"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        Log.d("MusicPlayerService", "Notification channel created")
    }
}