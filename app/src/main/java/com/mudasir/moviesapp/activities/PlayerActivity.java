package com.mudasir.moviesapp.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.mudasir.moviesapp.R;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends YouTubeBaseActivity{

    YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    TextView tvtitle,tvDescription;
    String loadVideo="";
    Toolbar toolbar;
    long duration;

    YouTubePlayer player;
    CountDownTimer mCountDownTimer;

    private long START_TIME_IN_MILLIS;

    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    private boolean isLoaded=false;


    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        tvtitle=findViewById(R.id.player_activity_movie_title);
        tvDescription=findViewById(R.id.player_activity_movie_description);
        toolbar=findViewById(R.id.app_bar_individual_user_players);

        //
        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();
        //

        toolbar.setTitle(getIntent().getExtras().get("title").toString());
        toolbar.setSubtitle("Watching Movie...");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);

        toolbar.setNavigationOnClickListener(v -> {

            AlertDialog.Builder builder=new AlertDialog.Builder(PlayerActivity.this)
                    .setTitle("Are You Sure You want to Exit")
                    .setPositiveButton("Yes", (dialog, which) -> finish())
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.create();
            builder.show();

        });

        youTubePlayerView=findViewById(R.id.youtube_player);
        onInitializedListener=new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {

                youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
                youTubePlayer.setPlaybackEventListener(playbackEventListener);

                player=youTubePlayer;

                if (!wasRestored){
                    youTubePlayer.loadVideo(loadVideo);

                }



            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        youTubePlayerView.initialize("AIzaSyDxCaPL1UIJmJvNaGjveyC6pxgW9e281wM",onInitializedListener);





      /*
*/


    }


    @Override
    protected void onStart() {
        super.onStart();

       loadVideo= String.valueOf(getIntent().getStringExtra("video_url"));
       tvtitle.setText(getIntent().getStringExtra("title"));
       tvDescription.setText(getIntent().getStringExtra("description"));
       tvDescription.setMovementMethod(new ScrollingMovementMethod());

    }


    // Working for all API levels
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder dailog=new AlertDialog.Builder(this);
            dailog.setTitle("Confirmation Message");
            dailog.setMessage("Are You Sure Want to Exit the Movie");
            dailog.setPositiveButton("Yes", (dialog, which) -> {
                dialog.dismiss();
                finish();

            });
            dailog.setNegativeButton("No", (dialog, which) -> dialog.cancel());

            dailog.create();
            dailog.show();

        }
        return super.onKeyDown(keyCode, event);
    }


    private void showMessage(String message) {
      //  Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            // Called when playback starts, either due to user action or call to play().
           // showMessage("Playing");
            mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    mTimeLeftInMillis = millisUntilFinished;

                    UpdateTime();
                }

                @Override
                public void onFinish() {

                }
            }.start();


        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
            showMessage("Paused");

            if (isLoaded){
                if (!mCountDownTimer.equals(null)){
                    mCountDownTimer.cancel();
                }
            }
            else{

            }


        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
          //  showMessage("Stopped");
        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
        }

        @Override
        public void onSeekTo(int i) {

            long changed= player.getDurationMillis()-i;
            mCountDownTimer.cancel();
            mTimeLeftInMillis=changed;
            UpdateTime();
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(changed),
                    TimeUnit.MILLISECONDS.toMinutes(changed) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(changed)),
                    TimeUnit.MILLISECONDS.toSeconds(changed) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(changed)));

           // Toast.makeText(PlayerActivity.this, "Remaining time is "+hms, Toast.LENGTH_SHORT).show();

            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
        }
    }

    private void UpdateTime() {

        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(mTimeLeftInMillis),
                TimeUnit.MILLISECONDS.toMinutes(mTimeLeftInMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mTimeLeftInMillis)),
                TimeUnit.MILLISECONDS.toSeconds(mTimeLeftInMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mTimeLeftInMillis)));
  // hms

    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        @Override
        public void onLoaded(String s) {

            isLoaded=true;

            START_TIME_IN_MILLIS = player.getDurationMillis();

            mTimeLeftInMillis=START_TIME_IN_MILLIS;
           // Toast.makeText(PlayerActivity.this, ""+mTimeLeftInMillis, Toast.LENGTH_SHORT).show();

            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
        }

    }


}