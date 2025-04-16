// MusicServiceAIDL.aidl
package com.example.musicplayerservice;

interface MusicServiceAIDL {
    void play();
    void pause();
    void playNext();
    int getCurrentPosition();
    int getDuration();
    void seekTo(int position);
    String getTrackTitle();
    String getTrackImageName();
}