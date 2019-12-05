package com.example.aldy.difacademy.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aldy.difacademy.Model.BlendedVideoModel;
import com.example.aldy.difacademy.R;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class WatchVideoBlendedActivity extends AppCompatActivity {
    private static final String TAG = "WatchVideoBlendedActivi";

    PlayerView playerView;
    TextView tvJudul, tvDeskripsi;
    ScrollView scrollView;

    SimpleExoPlayer simpleExoPlayer;
    BlendedVideoModel blendedVideoModel;

    String videoUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video_blended);

        initView();
        checkIntent();
        videoPlayer();
    }

    private void initView() {
        playerView = findViewById(R.id.pv_watch_video_blended);
        tvJudul = findViewById(R.id.tv_watch_video_blended_judul);
        tvDeskripsi = findViewById(R.id.tv_watch_video_blended_deskripsi);
        scrollView = findViewById(R.id.sv_watch_video_blended);
    }

    private void checkIntent() {
        Intent intent = getIntent();
        blendedVideoModel = intent.getParcelableExtra("blended_video_model");
        if (blendedVideoModel != null) {
            videoUrl = blendedVideoModel.getVideoUrl();
            tvJudul.setText(blendedVideoModel.getTitle());
            tvDeskripsi.setText(blendedVideoModel.getDescription());
        }
    }

    private void videoPlayer() {
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(simpleExoPlayer);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Difacademy"));
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(videoUrl));
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    ViewGroup.LayoutParams layoutParams = playerView.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    playerView.setLayoutParams(layoutParams);
                }
            }
        });
        simpleExoPlayer.setPlayWhenReady(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        simpleExoPlayer.release();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            scrollView.setVisibility(View.GONE);
        } else {
            scrollView.setVisibility(View.VISIBLE);
        }
    }
}
