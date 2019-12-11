package com.example.aldy.difacademy.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aldy.difacademy.Model.VideoFreeModel;
import com.example.aldy.difacademy.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import static com.example.aldy.difacademy.YoutubeApiKeyConfig.YOUTUBE_API_KEY;

public class WatchYoutubeVideoActivity extends YouTubeBaseActivity {

    VideoFreeModel videoFreeModel;

    YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    TextView tvJudul, tvDeskripsi;
    YouTubePlayer mYouTubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_youtube_video);

        initView();
        loadData();
    }

    private void initView() {
        tvJudul = findViewById(R.id.tv_watch_youtube_judul);
        tvDeskripsi = findViewById(R.id.tv_watch_youtube_deskripsi);
        youTubePlayerView = findViewById(R.id.yplayer);
    }

    private void loadData() {
        Intent intent = getIntent();
        videoFreeModel = intent.getParcelableExtra("video_free_model");
        tvJudul.setText(videoFreeModel.getTitle());
        tvDeskripsi.setText(videoFreeModel.getDescription());
        final String videoYoutubeId = videoFreeModel.getVideoYoutubeId();

        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                mYouTubePlayer = youTubePlayer;
                mYouTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE | YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
                youTubePlayer.loadVideo(videoYoutubeId);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        youTubePlayerView.initialize(YOUTUBE_API_KEY, onInitializedListener);
    }

}
