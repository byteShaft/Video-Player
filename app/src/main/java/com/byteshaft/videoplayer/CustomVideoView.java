package com.byteshaft.videoplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.VideoView;

import java.util.ArrayList;

public class CustomVideoView extends VideoView implements MediaPlayer.OnPreparedListener {

    private ArrayList<MediaPlayerStateChangedListener> mListeners = new ArrayList<>();

    public CustomVideoView(Context context) {
        super(context);
        setOnPreparedListener(this);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnPreparedListener(this);
    }

    public void setMediaPlayerStateChangedListener(MediaPlayerStateChangedListener listener) {
        mListeners.add(listener);
    }

    @Override
    public void start() {
        super.start();
        setKeepScreenOn(true);
        for (MediaPlayerStateChangedListener listener : mListeners) {
            listener.onPlaybackStateChanged(1);
        }
    }

    @Override
    public void pause() {
        super.pause();
        setKeepScreenOn(false);
        for (MediaPlayerStateChangedListener listener : mListeners) {
            listener.onPlaybackStateChanged(0);
        }
    }

    @Override
    public void stopPlayback() {
        super.stopPlayback();
    }

    @Override
    public void setVideoURI(Uri uri) {
        super.setVideoURI(uri);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        for (MediaPlayerStateChangedListener listener : mListeners) {
            listener.onVideoViewPrepared(mp);
        }

    }


    public interface MediaPlayerStateChangedListener {
        public void onPlaybackStateChanged(int state);
        public void onVideoViewPrepared(MediaPlayer mp);
    }
}
