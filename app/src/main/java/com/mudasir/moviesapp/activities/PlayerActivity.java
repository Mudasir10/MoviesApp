package com.mudasir.moviesapp.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.mudasir.moviesapp.R;

public class PlayerActivity extends YouTubeBaseActivity{

    YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListener;

    TextView tvtitle,tvDescription;


    String loadVideo="";
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        tvtitle=findViewById(R.id.player_activity_movie_title);
        tvDescription=findViewById(R.id.player_activity_movie_description);
        toolbar=findViewById(R.id.app_bar_individual_user_players);

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
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

                youTubePlayer.loadVideo(loadVideo);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        youTubePlayerView.initialize("AIzaSyDxCaPL1UIJmJvNaGjveyC6pxgW9e281wM",onInitializedListener);

    }




    @Override
    protected void onStart() {
        super.onStart();

        loadVideo= String.valueOf(getIntent().getExtras().get("video_url"));
        tvtitle.setText(getIntent().getExtras().get("title").toString());
        tvDescription.setText(getIntent().getExtras().get("des").toString());
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


}