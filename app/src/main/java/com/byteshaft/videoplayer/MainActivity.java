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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        CustomVideoView.MediaPlayerStateChangedListener, MediaPlayer.OnCompletionListener {

    private ListView mMenuList;
    private CustomVideoView mCustomVideoView;
    private boolean isLandscape = true;
    private Helpers mHelpers;
    private Button mOverlayButton;
    private Button mRotationButton;
    private GestureDetectorCompat mDetector;
    private ScreenStateListener mScreenStateListener;
    private SlidingPaneLayout mSlidingPanel;
    private ListView mVideoList;
    private ArrayAdapter<String> adapter;
    private Toolbar toolbar;
    private Toolbar listToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHelpers = new Helpers(getApplicationContext());
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,
                mHelpers.getVideoTitles(mHelpers.getAllVideosUri()));
        mSlidingPanel = (SlidingPaneLayout) findViewById(R.id.SlidingPanel);
        mVideoList = (ListView) findViewById(R.id.video_list);
        mVideoList.setAdapter(adapter);
        listToolbar = (Toolbar) findViewById(R.id.titlebar);
        listToolbar.setTitle("Videos");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_menu);
        toolbar.setTitle("Prototype");
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
        mCustomVideoView = (CustomVideoView) findViewById(R.id.videoSurface);
        mCustomVideoView.setMediaPlayerStateChangedListener(this);
        mCustomVideoView.setOnCompletionListener(this);
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

    @Override
    public void onPlaybackStateChanged(int state) {

    }

    @Override
    public void onVideoViewPrepared(MediaPlayer mp) {
        setVideoOrientation();

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

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

    private void setVideoOrientation() {
        if (mHelpers.isVideoPortrait(mCustomVideoView.getVideoHeight(),
                mCustomVideoView.getVideoWidth())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
}

