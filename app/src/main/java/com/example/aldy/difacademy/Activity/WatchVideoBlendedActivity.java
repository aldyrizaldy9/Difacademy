package com.example.aldy.difacademy.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.aldy.difacademy.Model.BlendedVideoModel;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.TextView;

import com.example.aldy.difacademy.R;

public class WatchVideoBlendedActivity extends AppCompatActivity {
    private static final String TAG = "WatchVideoBlendedActivi";

    PlayerView playerView;
    TextView tvJudul, tvDeskripsi;

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

    private void initView(){
        playerView = findViewById(R.id.pv_watch_video_blended);
        tvJudul = findViewById(R.id.tv_watch_video_blended_judul);
        tvDeskripsi = findViewById(R.id.tv_watch_video_blended_deskripsi);
    }

    private void checkIntent(){
        Intent intent = getIntent();
        blendedVideoModel = intent.getParcelableExtra("blended_video_model");
        if (blendedVideoModel != null){
            videoUrl = blendedVideoModel.getVideoUrl();
            tvJudul.setText(blendedVideoModel.getTitle());
            tvDeskripsi.setText(blendedVideoModel.getDescription());
        }
    }

    private void videoPlayer(){
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(simpleExoPlayer);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Difacademy"));
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(videoUrl));
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.setPlayWhenReady(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        simpleExoPlayer.release();
    }
}
