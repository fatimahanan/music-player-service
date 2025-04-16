package com.example.musicplayerservice

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.google.common.truth.Truth.assertThat
import org.mockito.Mockito.spy

@Config(manifest=Config.NONE)
@RunWith(RobolectricTestRunner::class)
class MusicPlayerServiceTest {
    private lateinit var musicPlayerService: MusicPlayerService

    @Mock
    lateinit var mockMediaPlayerHelper: MediaPlayerHelper

    private lateinit var service: MusicPlayerService

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        service = MusicPlayerService(mockMediaPlayerHelper)

        //mock mediaPlayerHelper
        doNothing().`when`(mockMediaPlayerHelper).play(any(), any())
        doNothing().`when`(mockMediaPlayerHelper).pause()

        //initializing MusicPlayerService
        musicPlayerService = MusicPlayerService(mockMediaPlayerHelper)
        musicPlayerService.mediaPlayer = mockMediaPlayerHelper
    }

    @Test
    fun testOnBind_onCall_ReturnsBinder() {
        val musicPlayerService = MusicPlayerService()

        //call onBind
        val binder = musicPlayerService.onBind(mock(Intent::class.java))

        //verify binder is of the certain type
        assertThat(binder).isInstanceOf(MusicServiceAIDL.Stub::class.java) }


    @Test
    fun testPlay_onPlay_invokePlayOfMediaPlayerHelper() {
        musicPlayerService.binder.play()
        //verify MediaPlayerHelper's play is called
        verify(mockMediaPlayerHelper).playCurrent(any())
    }

    @Test
    fun testPause_onPause_invokePauseOfMediaPlayerHelper() {
        musicPlayerService.binder.pause()
        verify(mockMediaPlayerHelper).pause()
    }

    @Test
    fun testPlayNext_shouldPlayNextSongInPlaylist() {
        val mockMediaPlayer = mock(MediaPlayer::class.java)
        val factory = { _: Context, _: Int -> mockMediaPlayer }

        val helper = spy(MediaPlayerHelper(null, factory))
        val service = MusicPlayerService(helper)

        service.binder.play()      //plays first track
        service.binder.playNext()  //second track

        verify(helper).play(any(), eq(R.raw.epic))
    }

    @Test
    fun testPlayNext_wrapsAroundToFirstTrack() {
        val mockMediaPlayer = mock(MediaPlayer::class.java)
        val factory = { _: Context, _: Int -> mockMediaPlayer }
        val helper = spy(MediaPlayerHelper(null, factory))
        val service = MusicPlayerService(helper)

        //simulate going through the playlist
        service.binder.playNext() //index 1
        service.binder.playNext() //index 2
        service.binder.playNext() //back to index 0

        verify(helper).play(any(), eq(R.raw.epic))
    }

    @Test
    fun testOnDestroy_onDestroyCalled_releaseMediaPlayerHelper() {
        //injecting mockMediaPlayerHelper into musicPlayerService
        val mockMediaPlayerHelper: MediaPlayerHelper = mock()
        val musicPlayerService = MusicPlayerService(mockMediaPlayerHelper)
        musicPlayerService.onDestroy()
        //assert verify release called on mocked mediaPlayerHelper
        verify(mockMediaPlayerHelper).release()
        Log.d("MusicPlayerServiceTest", "onDestroy called and release() was invoked.")
    }


}
