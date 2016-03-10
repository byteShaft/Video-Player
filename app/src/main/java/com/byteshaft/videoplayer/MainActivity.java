package com.byteshaft.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        CustomVideoView.MediaPlayerStateChangedListener, MediaPlayer.OnCompletionListener, AdapterView.OnItemClickListener {

    private CustomVideoView mCustomVideoView;
    public Helpers mHelpers;
    private SlidingPaneLayout mSlidingPanel;
    private ListView mVideoList;
    private VideoListAdapter adapter;
    private Toolbar toolbar;
    private ArrayList<String> mVideosTitles;
    private ArrayList<String> videoPathList;
    private SeekBar mSeekBarVolume;
    private AudioManager audioManager;
    private Button mGmailButton;
    private Button mFaceBooKbutton;
    private Button mInstrgramButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSeekBarVolume = (SeekBar) findViewById(R.id.sound_seek_bar);
        mGmailButton = (Button) findViewById(R.id.google_button);
        mFaceBooKbutton = (Button) findViewById(R.id.facebook);
        mInstrgramButton = (Button) findViewById(R.id.instagram);
        mGmailButton.setOnClickListener(this);
        mFaceBooKbutton.setOnClickListener(this);
        mInstrgramButton.setOnClickListener(this);
        new BitmapCache();
        mHelpers = new Helpers(getApplicationContext());
        mVideosTitles = new ArrayList<>();
        videoPathList = new ArrayList<>();
        mCustomVideoView = (CustomVideoView) findViewById(R.id.videoSurface);
        final MediaController mediaController = new MediaController(mCustomVideoView.getContext());
        videoPathList = mHelpers.getAllVideosUri();
        mVideosTitles = mHelpers.getVideoTitles(videoPathList);
        adapter = new VideoListAdapter(getApplicationContext(), R.layout.row,
                mVideosTitles);
        mSlidingPanel = (SlidingPaneLayout) findViewById(R.id.SlidingPanel);
        mVideoList = (ListView) findViewById(R.id.video_list);
        mVideoList.setAdapter(adapter);
        mVideoList.setOnItemClickListener(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_menu);
        SlidingPaneLayout.PanelSlideListener panelListener = new SlidingPaneLayout.PanelSlideListener(){

            @Override
            public void onPanelClosed(View arg0) {
            }

            @Override
            public void onPanelOpened(View arg0) {
            }

            @Override
            public void onPanelSlide(View arg0, float arg1) {
                if (mediaController.isShowing()) {
                    mediaController.hide();
                }

            }
        };
        mSlidingPanel.setPanelSlideListener(panelListener);
        mSlidingPanel.setParallaxDistance(300);
        mCustomVideoView.setMediaPlayerStateChangedListener(this);
        mCustomVideoView.setOnCompletionListener(this);
        mediaController.setAnchorView(mCustomVideoView);
        mCustomVideoView.setMediaController(mediaController);
        mCustomVideoView.setVideoPath("https://cdn.scissorboy.com/episode/WellaApp/HLSPRO/WellaInnosense-Color_and_Style.1280x720_HLSPRO-2.m3u8");
        mCustomVideoView.seekTo(0);
        initControls();
    }

    private void initControls()
    {
        try
        {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mSeekBarVolume.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            mSeekBarVolume.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            mSeekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onStopTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mSlidingPanel.isOpen()) {
            mSlidingPanel.openPane();
        } else {
            super.onBackPressed();
        }
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
        mp.start();

    }

    @Override
    public void onClick(View v) {
        Intent browserIntent;
        switch (v.getId()) {
            case R.id.google_button:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/"));
                startActivity(browserIntent);
                break;
            case R.id.facebook:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"));
                startActivity(browserIntent);
                break;
            case R.id.instagram:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/"));
                startActivity(browserIntent);
                break;

        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    class VideoListAdapter extends ArrayAdapter<String> {

        public VideoListAdapter(Context context, int resource, ArrayList<String> videos) {
            super(context, resource, videos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.row, parent, false);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.FilePath);
                holder.time = (TextView) convertView.findViewById(R.id.tv);
                holder.thumbnail = (ImageView) convertView.findViewById(R.id.Thumbnail);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText(mVideosTitles.get(position));
            holder.time.setText(
                    mHelpers.getFormattedTime((mHelpers.getDurationForVideo(position))));
            holder.position = position;
            if (BitmapCache.getBitmapFromMemCache(String.valueOf(position)) == null) {
                holder.thumbnail.setImageURI(null);
                new ThumbnailCreationTask(getApplicationContext(),
                        holder, position).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                holder.thumbnail.setImageBitmap(BitmapCache.getBitmapFromMemCache
                        (String.valueOf(position)));
            }
            return convertView;
        }
    }

        static class ViewHolder {
            public TextView title;
            public TextView time;
            public ImageView thumbnail;
            public int position;
        }
}

