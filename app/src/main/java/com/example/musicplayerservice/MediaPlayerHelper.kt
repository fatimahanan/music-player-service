package com.example.musicplayerservice

import android.content.Context
import android.media.MediaPlayer

open class MediaPlayerHelper(private var mediaPlayer: MediaPlayer? = null,
                             private val factory: ((Context, Int) -> MediaPlayer)? = null) {

    data class Track(
        val title: String,
        val resId: Int,
        val imageName: String
    )

    private val playlist = listOf(
        Track("Where Is Heaven", R.raw.they_saw_heaven, "whereisheaven"),
        Track("Epic", R.raw.epic, "epic"),
        Track("Wake Me Up", R.raw.wake_me_up, "wakemeup")
    )

    private var currentResId: Int? = null
    private var currentIndex=0

    open fun play(context: Context, resId: Int) {
        if (resId == 0) throw IllegalArgumentException("Invalid resource ID")

        if (mediaPlayer == null || currentResId != resId) {
            //if media player doesn't exist or it's a new track, release old
            mediaPlayer?.release()
            mediaPlayer = factory?.invoke(context, resId) ?: MediaPlayer.create(context, resId)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
            currentResId = resId
        }
        else if(mediaPlayer?.isPlaying == false){
            mediaPlayer?.start()
        }
    }

    fun pause() {
        mediaPlayer?.takeIf { it.isPlaying }?.pause()
    }

    fun playNext(context: Context) {
        if (playlist.isNotEmpty()) {
            currentIndex = (currentIndex + 1) % playlist.size
            val track = playlist[currentIndex]
            play(context, track.resId)
        }
    }

    fun playCurrent(context: Context) {
        val track = playlist[currentIndex]
        play(context, track.resId)
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun getCurrentTrackTitle(): String {
        return playlist.getOrNull(currentIndex)?.title ?: "Unknown"
    }

    fun getCurrentTrackImage(): String {
        return playlist.getOrNull(currentIndex)?.imageName ?: "images"
    }

}
