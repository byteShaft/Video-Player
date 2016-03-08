package com.byteshaft.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        CustomVideoView.MediaPlayerStateChangedListener, MediaPlayer.OnCompletionListener {

    SlidingPaneLayout mSlidingPanel;
    ListView mMenuList;
    private CustomVideoView mCustomVideoView;
    private boolean isLandscape = true;
    private Helpers mHelpers;
    private Button mOverlayButton;
    private Button mRotationButton;
    private GestureDetectorCompat mDetector;
    private ScreenStateListener mScreenStateListener;

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPlaybackStateChanged(int state) {

    }

    @Override
    public void onVideoViewPrepared(MediaPlayer mp) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    private static class Screen {
        static class Brightness {
            static final float HIGH = 1f;
            static final float LOW = 0f;
        }
    }

    private static class Sound {
        static class Level {
            static final int MINIMUM = 0;
            static final int MAXIMUM = 15;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHelpers = new Helpers(getApplicationContext());
        mSlidingPanel = (SlidingPaneLayout) findViewById(R.id.SlidingPanel);
        mMenuList = (ListView) findViewById(R.id.MenuList);
        SlidingPaneLayout.PanelSlideListener panelListener = new SlidingPaneLayout.PanelSlideListener(){

            @Override
            public void onPanelClosed(View arg0) {
            }

            @Override
            public void onPanelOpened(View arg0) {
            }

            @Override
            public void onPanelSlide(View arg0, float arg1) {

            }
        };
        mSlidingPanel.setPanelSlideListener(panelListener);
        mSlidingPanel.setParallaxDistance(300);
        mScreenStateListener = new ScreenStateListener(mCustomVideoView);
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        mDetector = new GestureDetectorCompat(this, new GestureListener());
        mCustomVideoView = (CustomVideoView) findViewById(R.id.videoSurface);
        mCustomVideoView.setMediaPlayerStateChangedListener(this);
        mCustomVideoView.setOnCompletionListener(this);
        mHelpers.setScreenBrightness(getWindow(), Screen.Brightness.HIGH);
        CustomMediaController mediaController = new CustomMediaController(this);
        mediaController.setAnchorView(mCustomVideoView);
        mCustomVideoView.setMediaController(mediaController);
        registerReceiver(mScreenStateListener, filter);
        mCustomVideoView.setVideoPath("");
        mCustomVideoView.seekTo(0);
        mCustomVideoView.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class CustomMediaController extends MediaController {

        public CustomMediaController(Context context) {
            super(context);
        }

        @Override
        public void show() {
            super.show();
//            mOverlayButton.setVisibility(VISIBLE);
//            mRotationButton.setVisibility(VISIBLE);
        }

        @Override
        public void hide() {
            super.hide();
//            mOverlayButton.setVisibility(INVISIBLE);
//            mRotationButton.setVisibility(INVISIBLE);
        }
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        final double BRIGHTNESS_STEP = 0.066;
        final int VOLUME_STEP = 1;
        private float lastTrackedPosition;

        @Override
        public boolean onDown(MotionEvent e) {
            lastTrackedPosition = e.getY();
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            final int ACTIVITY_HEIGHT_FRAGMENT = getActivityHeight() / 50;
            float touchX = e2.getX();
            float touchY = e2.getY();
            if (touchX < getActivityWidth() / 2) {
                float brightness = mHelpers.getCurrentBrightness(getWindow());
                if (touchY >= lastTrackedPosition + ACTIVITY_HEIGHT_FRAGMENT &&
                        brightness - BRIGHTNESS_STEP > Screen.Brightness.LOW) {
                    brightness -= BRIGHTNESS_STEP;
                    mHelpers.setScreenBrightness(getWindow(), brightness);
                    lastTrackedPosition = touchY;
                } else if (touchY <= lastTrackedPosition - ACTIVITY_HEIGHT_FRAGMENT &&
                        brightness + BRIGHTNESS_STEP <= Screen.Brightness.HIGH) {
                    brightness += BRIGHTNESS_STEP;
                    mHelpers.setScreenBrightness(getWindow(), brightness);
                    lastTrackedPosition = touchY;
                }
            } else {
                int currentVolume = mHelpers.getCurrentVolume();
                if (touchY > lastTrackedPosition + ACTIVITY_HEIGHT_FRAGMENT &&
                        currentVolume - VOLUME_STEP >= Sound.Level.MINIMUM) {
                    currentVolume -= VOLUME_STEP;
                    mHelpers.setVolume(currentVolume);
                    lastTrackedPosition = touchY;
                } else if (touchY <= lastTrackedPosition - ACTIVITY_HEIGHT_FRAGMENT &&
                        currentVolume + VOLUME_STEP <= Sound.Level.MAXIMUM) {
                    currentVolume += VOLUME_STEP;
                    mHelpers.setVolume(currentVolume);
                    lastTrackedPosition = touchY;
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    private int getActivityHeight() {
        return getWindow().getDecorView().getHeight();
    }

    private int getActivityWidth() {
        return getWindow().getDecorView().getWidth();
    }

    private void setVideoOrientation() {
        if (mHelpers.isVideoPortrait(mCustomVideoView.getVideoHeight(),
                mCustomVideoView.getVideoWidth())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
}

