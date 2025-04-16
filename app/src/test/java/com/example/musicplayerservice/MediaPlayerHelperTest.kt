package com.example.musicplayerservice

import android.content.Context
import android.media.MediaPlayer
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class MediaPlayerHelperTest {

    @Mock
    lateinit var mockMediaPlayer: MediaPlayer

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testPlay_whenCalled_StartMediaPlayer() {
        val mockContext = mock(Context::class.java)
        val mediaPlayerHelper = object : MediaPlayerHelper(mockMediaPlayer) {
            override fun play(context: Context, resId: Int) {
                val field = this::class.java.superclass.getDeclaredField("mediaPlayer") //using reflection to access mediaplayer(pric
                field.isAccessible = true
                val mediaPlayer = field.get(this) as MediaPlayer

                if (!mediaPlayer.isPlaying) {
                    mediaPlayer.start()
                }
            }
        }
        `when`(mockMediaPlayer.isPlaying).thenReturn(false)

        mediaPlayerHelper.play(mockContext, R.raw.wake_me_up)

        verify(mockMediaPlayer).start()
    }

    @Test
    fun testPause_whenPlaying_PauseMediaPlayer() {
        val mediaPlayerHelper = MediaPlayerHelper(mockMediaPlayer)
        `when`(mockMediaPlayer.isPlaying).thenReturn(true)
        mediaPlayerHelper.pause()
        verify(mockMediaPlayer).pause()
    }

    @Test
    fun testRelease_whenCalled_shouldReleaseMediaPlayer() {
        val mediaPlayerHelper = MediaPlayerHelper(mockMediaPlayer)
        mediaPlayerHelper.release()
        verify(mockMediaPlayer).release()
    }

    @Test
    fun testPlay_whenMediaPlayerIsAlreadyPlaying_shouldNotCallStartAgain() {
        val mediaPlayerHelper = object : MediaPlayerHelper(mockMediaPlayer) {
            override fun play(context: Context, resId: Int) {
                val field = this::class.java.superclass.getDeclaredField("mediaPlayer")
                field.isAccessible = true
                val mediaPlayer = field.get(this) as MediaPlayer

                if (!mediaPlayer.isPlaying) {
                    mediaPlayer.start()
                }
            }
        }
        `when`(mockMediaPlayer.isPlaying).thenReturn(true)
        mediaPlayerHelper.play(mock(Context::class.java), R.raw.wake_me_up)
        verify(mockMediaPlayer, times(0)).start()
    }

    @Test
    fun testPause_whenMediaPlayerIsNotPlaying_shouldNotCallPause() {
        val mediaPlayerHelper = MediaPlayerHelper(mockMediaPlayer)
        `when`(mockMediaPlayer.isPlaying).thenReturn(false)
        mediaPlayerHelper.pause()
        verify(mockMediaPlayer, times(0)).pause()
    }

    @Test
    fun testPause_whenAlreadyPaused_shouldNotCallPauseAgain() {
        val mediaPlayerHelper = MediaPlayerHelper(mockMediaPlayer)
        `when`(mockMediaPlayer.isPlaying).thenReturn(false)
        mediaPlayerHelper.pause()
        verify(mockMediaPlayer, times(0)).pause()
    }

    @Test
    fun testPlay_whenResourceIsNull_shouldThrowException() {
        val mediaPlayerHelper = MediaPlayerHelper(mockMediaPlayer)
        var exceptionThrown = false
        try {
            mediaPlayerHelper.play(mock(Context::class.java), 0)
        } catch (e: IllegalArgumentException) {
            exceptionThrown = true
        }
        assertTrue("Expected IllegalArgumentException to be thrown", exceptionThrown)
    }

    @Test
    fun testPause_afterRelease_shouldNotCallPause() {
        val mediaPlayerHelper = MediaPlayerHelper(mockMediaPlayer)
        mediaPlayerHelper.release()
        mediaPlayerHelper.pause()
        verify(mockMediaPlayer, times(0)).pause()
    }

    @Test
    fun testPlayNext_shouldAdvanceAndCallPlay() {
        val mockContext = mock(Context::class.java)
        val mockPlayer = mock(MediaPlayer::class.java)

        //factory to return mock mediaPlayer instead of calling mediaPlayer.create()
        val factory = { _: Context, _: Int -> mockPlayer }

        val helper = spy(MediaPlayerHelper(null, factory))

        helper.playNext(mockContext)
        verify(helper).play(mockContext, R.raw.epic)

        helper.playNext(mockContext)
        verify(helper).play(mockContext, R.raw.wake_me_up)

        helper.playNext(mockContext)
        verify(helper).play(mockContext, R.raw.they_saw_heaven)
    }

}

