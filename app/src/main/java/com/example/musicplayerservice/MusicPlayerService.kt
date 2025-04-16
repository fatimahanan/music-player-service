package com.example.musicplayerservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat


class MusicPlayerService(var mediaPlayer: MediaPlayerHelper? = null) : Service(){
    companion object{
        private const val channelId = "MusicPlayerServiceChannel"
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayerHelper()  //initialize media player helper
        createNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Music Player")
            .setContentText("Music is playing...")
            .setSmallIcon(R.drawable.baseline_music_video_24)
            .build()
        startForeground(1,notification)
        Log.d("MusicPlayerService", "Service created")
    }

    val binder : MusicServiceAIDL.Stub = object : MusicServiceAIDL.Stub(), MusicServiceBinder {
        override fun play() {
            mediaPlayer?.playCurrent(this@MusicPlayerService)
        }

        override fun pause() {
            mediaPlayer?.pause()
        }

        override fun playNext() {
            mediaPlayer?.playNext(this@MusicPlayerService)
        }

        override fun getCurrentPosition(): Int {
            return mediaPlayer?.getCurrentPosition() ?: 0
        }

        override fun getDuration(): Int {
            return mediaPlayer?.getDuration() ?: 0
        }

        override fun seekTo(position: Int) {
            mediaPlayer?.seekTo(position)
        }

        override fun getTrackTitle(): String {
            return mediaPlayer?.getCurrentTrackTitle() ?: "Unknown"
        }

        override fun getTrackImageName(): String {
            return mediaPlayer?.getCurrentTrackImage() ?: "whereisheaven"  // return resource ID as int
        }

    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MusicPlayerService", "onDestroy called")
        mediaPlayer?.release()
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
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        Log.d("MusicPlayerService", "Notification channel created")
    }
}

