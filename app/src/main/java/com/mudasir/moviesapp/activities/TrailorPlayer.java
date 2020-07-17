package com.mudasir.moviesapp.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.mudasir.moviesapp.R;

import java.util.concurrent.TimeUnit;

public class TrailorPlayer extends YouTubeBaseActivity {

    YouTubePlayerView youTubePlayertrailor;
    YouTubePlayer.OnInitializedListener onInitializedListener;

    YouTubePlayer player;

    TrailorPlaybackEventListener trailorPlaybackEventListener;
    TrailorPlayerStateChangeListener trailorPlayerStateChangeListener;
    String loadVideo="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailor_player);


        youTubePlayertrailor=findViewById(R.id.yt_player_trailor);


        trailorPlayerStateChangeListener = new TrailorPlayerStateChangeListener();
        trailorPlaybackEventListener = new TrailorPlaybackEventListener();

        onInitializedListener=new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {

                youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                youTubePlayer.setPlayerStateChangeListener(trailorPlayerStateChangeListener);
                youTubePlayer.setPlaybackEventListener(trailorPlaybackEventListener);

                player=youTubePlayer;

                if (!wasRestored){
                    youTubePlayer.loadVideo(loadVideo);
                }

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        youTubePlayertrailor.initialize("AIzaSyDxCaPL1UIJmJvNaGjveyC6pxgW9e281wM",onInitializedListener);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent()!=null){
            loadVideo=getIntent().getStringExtra("video_url");
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder dailog = new AlertDialog.Builder(this);
            dailog.setTitle("Confirmation Message");
            dailog.setMessage("Are You Sure Want to Exit Trailor");
            dailog.setPositiveButton("Yes", (dialog, which) -> {

                dialog.dismiss();
                finish();

            });
            dailog.setNegativeButton("No", (dialog, which) -> dialog.cancel());

            dailog.create();
            dailog.show();

        }
       // return super.onKeyDown(keyCode, event);
        return true;
    }

    private final class TrailorPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {

        }

        @Override
        public void onPaused() {
        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }
    }

    private final class TrailorPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {

        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {

        }

        @Override
        public void onVideoEnded() {

            finish();

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

        }

    }


}