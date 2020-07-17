package com.mudasir.moviesapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mudasir.moviesapp.R;
import com.mudasir.moviesapp.adapters.MessagesAdapter;
import com.mudasir.moviesapp.models.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class WatchMovieInGroup extends YouTubeBaseActivity {

    YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListenerGroup;

    String loadVideoUrl = "";
    String title = "";
    String status = "";
    String admin = "";
    Integer time;


    String GroupName = "";
    EditText editTextMessage;
    Button btnSendMessage;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabase;

    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;

    Toolbar mToolbar;
    private String uid = "";
    String code = "";
    String UsersName;

    List<messages> messagesList;

    private RecyclerView rvMessages;

    TextView textViewShareCode;
    TextView textViewGroupCode;

    String SeekTO;

    YouTubePlayer player;
    YouTubePlayer userPlayer;

    CountDownTimer mCountDownTimer;

    private long START_TIME_IN_MILLIS;

    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;


    // listeners for Admin
     private MyPlayerStateChangeListener playerStateChangeListener;
     private MyPlaybackEventListener playbackEventListener;

     // listeners for the User who joined stream
    private UserPlaybackEventListener userPlaybackEventListener;
    private UserPlayerStateChangeListener userPlayerStateChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_movie_in_group);


        mToolbar = findViewById(R.id.app_bar_watch_movie_inGroup);
        editTextMessage = findViewById(R.id.et_message);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        textViewShareCode = findViewById(R.id.tv_Share_Code);
        textViewGroupCode=findViewById(R.id.tvGroupCode);

           // Listeners for Admin creating objects
        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();
          // creating objects

        // listeners for User who joined
        userPlaybackEventListener=new UserPlaybackEventListener();
        userPlayerStateChangeListener=new UserPlayerStateChangeListener();


        // setting up Recycler View Messages
        rvMessages = findViewById(R.id.rv_messages);
        rvMessages.setHasFixedSize(true);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        uid = mCurrentUser.getUid();

        if (mAuth.getCurrentUser() != null) {
            FetchUserName();
        }

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("groups");

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_left);


        mToolbar.setNavigationOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(WatchMovieInGroup.this)
                    .setTitle("Are You Sure You want to Exit The Group")
                    .setPositiveButton("Yes", (dialog, which) -> finish())
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.create();
            builder.show();

        });


        LoadVideoInYoutubePlayerView();


        btnSendMessage.setOnClickListener(v -> {

            String message = editTextMessage.getText().toString();

            if (!message.isEmpty()) {

                String key = mDatabaseRef.push().getKey();

                messages messages = new messages(UsersName, message);
                Map<String, Object> postValues = messages.toMap();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(key, postValues);

                mDatabaseRef.child(code).child("Messages").updateChildren(childUpdates)
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {

                                editTextMessage.setText("");

                            }

                        });

            } else {
                Toast.makeText(WatchMovieInGroup.this, "Type a Comment", Toast.LENGTH_SHORT).show();
            }

        });


        textViewShareCode.setOnClickListener(
                v -> SendDataToOtherApps()
        );



    }

    private void LoadVideoInYoutubePlayerView() {

        youTubePlayerView = findViewById(R.id.youtube_player_group);

        onInitializedListenerGroup = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

                if (mCurrentUser.getUid().equals(admin)) {

                    player=youTubePlayer;

                    youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
                    youTubePlayer.setPlaybackEventListener(playbackEventListener);

                    youTubePlayer.loadVideo(loadVideoUrl);
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    youTubePlayer.setShowFullscreenButton(false);


                } else {

                    userPlayer=youTubePlayer;
                    youTubePlayer.setPlayerStateChangeListener(userPlayerStateChangeListener);
                    youTubePlayer.setPlaybackEventListener(userPlaybackEventListener);

                    youTubePlayer.loadVideo(loadVideoUrl);
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    youTubePlayer.setShowFullscreenButton(false);


                    // play movie according to admin




                }

            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        youTubePlayerView.initialize("AIzaSyDxCaPL1UIJmJvNaGjveyC6pxgW9e281wM", onInitializedListenerGroup);

    }


    @Override
    protected void onStart() {
        super.onStart();

        mCurrentUser = mAuth.getCurrentUser();
        uid = mCurrentUser.getUid();

        messagesList = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("groups");

        if (getIntent() != null) {
            code = String.valueOf(getIntent().getExtras().get("group_code"));
            textViewGroupCode.setText("Joining Code : "+code);
        }

        mDatabaseRef.child(code).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    loadVideoUrl = dataSnapshot.child("movieUrl").getValue().toString();
                    GroupName = dataSnapshot.child("groupName").getValue().toString();
                    title = dataSnapshot.child("title").getValue().toString();

                    admin = dataSnapshot.child("admin").getValue().toString();
                    status = dataSnapshot.child("status").getValue().toString();

                    mToolbar.setTitle("Movie Name:" + title);
                    mToolbar.setSubtitle("Group: " + GroupName);



                    RetrieveMessages();

                } else {

                    Toast.makeText(WatchMovieInGroup.this, "Group Not Found", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void JoinedGroup() {

        String key = mDatabaseRef.push().getKey();

        String message = UsersName + " Has Joined The Group";

        messages messages = new messages(UsersName, message);
        Map<String, Object> postValues = messages.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, postValues);

        mDatabaseRef.child(code).child("Messages").updateChildren(childUpdates);

    }


    private void FetchUserName() {

        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UsersName = String.valueOf(dataSnapshot.child("UserName").getValue());
                JoinedGroup();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void RetrieveMessages() {


        DatabaseReference mSMSRef=mDatabaseRef.child(code).child("Messages");
        Query query=mSMSRef.limitToLast(10);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    messagesList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        messages messages = snapshot.getValue(messages.class);
                        messagesList.add(messages);
                    }

                    MessagesAdapter adapter = new MessagesAdapter(messagesList, WatchMovieInGroup.this);
                    rvMessages.setAdapter(adapter);

                    int lastpos = rvMessages.getAdapter().getItemCount();
                    rvMessages.smoothScrollToPosition(lastpos);
                    adapter.notifyDataSetChanged();
                } else {
                   // Toast.makeText(WatchMovieInGroup.this, "No Messages", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    // Working for all API levels
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder dailog = new AlertDialog.Builder(this);
            dailog.setTitle("Confirmation Message");
            dailog.setMessage("Are You Sure Want to Exit The Group");
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


    private void SendDataToOtherApps() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Movies Room Joining Code : " + code);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);

    }


    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            mDatabaseRef.child(code).child("status").setValue("isPlaying");
            // Called when playback starts, either due to user action or call to play().
            //showMessage("Playing");
            mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                   // mTimeLeftInMillis = millisUntilFinished;
                         //   UpdateTime();

                }

                @Override
                public void onFinish() {

               /*

                */

                }
            }.start();


        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
            //showMessage("Paused");
            if (mCountDownTimer!=null){
                mCountDownTimer.cancel();
                mDatabaseRef.child(code).child("status").setValue("isPaused");
            }

        }

        @Override
        public void onStopped() {

          //  showMessage("Stopped");
            mDatabaseRef.child(code).child("status").setValue("isStoped");

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

           // Toast.makeText(WatchMovieInGroup.this, "Remaining time is "+hms, Toast.LENGTH_SHORT).show();

            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void UpdateTime() {

        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(mTimeLeftInMillis),
                TimeUnit.MILLISECONDS.toMinutes(mTimeLeftInMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mTimeLeftInMillis)),
                TimeUnit.MILLISECONDS.toSeconds(mTimeLeftInMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mTimeLeftInMillis)));

        mDatabaseRef.child(code).child("time").setValue(mTimeLeftInMillis);
        Log.d(TAG, "UpdateTime Method Time Left is :  "+hms);
    }


    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }
        @Override
        public void onLoaded(String s) {

            START_TIME_IN_MILLIS = player.getDurationMillis();
            mTimeLeftInMillis=START_TIME_IN_MILLIS;

           // Toast.makeText(WatchMovieInGroup.this, ""+mTimeLeftInMillis, Toast.LENGTH_SHORT).show();

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
            AlertDialog.Builder builder=new AlertDialog.Builder(WatchMovieInGroup.this)
                    .setTitle("Message")
                    .setMessage(" Thanks For Watching , Movie Has Been Finished. Now You can Destroy The Group")
                    .setCancelable(false);
            builder.setPositiveButton("Ok", (dialog, which) -> finish());
            builder.create();
            builder.show();
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCurrentUser.getUid().equals(admin)){
            try{
                player.release();
            }
            catch (Exception ex){
               // Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
        if (!mCurrentUser.getUid().equals(admin)){
            try{
                userPlayer.release();
            }
            catch (Exception ex){
               // Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        if (mCountDownTimer!=null){
            mCountDownTimer.cancel();
        }
    }

   private final class UserPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener{

       @Override
       public void onLoading() {

       }

       @Override
       public void onLoaded(String s) {

           START_TIME_IN_MILLIS = userPlayer.getDurationMillis();
           mTimeLeftInMillis=START_TIME_IN_MILLIS;


           mDatabaseRef.child(code).child("time").addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   if (dataSnapshot.exists()){
                       time=Integer.valueOf(String.valueOf(dataSnapshot.getValue()));
                       if (userPlayer!=null){
                           try {
                               userPlayer.seekToMillis((int) (mTimeLeftInMillis-time));
                           }
                           catch (Exception ex){
                             //  Toast.makeText(WatchMovieInGroup.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                           }

                       }
                       //
                   }
                  // Toast.makeText(WatchMovieInGroup.this, "Time Changed "+dataSnapshot.getValue(), Toast.LENGTH_SHORT).show();
               }
               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });


           // get Runtime status and play according to admin
           mDatabaseRef.child(code).child("status").addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   if (dataSnapshot.exists()){
                       status=String.valueOf(dataSnapshot.getValue());
                       if (userPlayer!=null){
                           if (status.equals("isPlaying")){
                               try{
                                   userPlayer.play();
                               }
                               catch (Exception ex){
                                   //Toast.makeText(WatchMovieInGroup.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                               }

                           }
                           else if (status.equals("isPaused")){
                               try{
                                   userPlayer.pause();
                               }catch (Exception ex){
                                  // Toast.makeText(WatchMovieInGroup.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                               }

                           }
                       }
                   }
               }
               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });








       }

       @Override
       public void onAdStarted() {

       }

       @Override
       public void onVideoStarted() {

       }

       @Override
       public void onVideoEnded() {

           AlertDialog.Builder builder=new AlertDialog.Builder(WatchMovieInGroup.this)
                   .setTitle("Message")
                   .setMessage(" Thanks For Watching , Movie Has Been Finished.")
                   .setCancelable(false);
           builder.setPositiveButton("Ok", (dialog, which) -> finish());
           builder.create();
           builder.show();
       }

       @Override
       public void onError(YouTubePlayer.ErrorReason errorReason) {

       }

   }

   private final class UserPlaybackEventListener implements YouTubePlayer.PlaybackEventListener{

       @Override
       public void onPlaying() {
           showMessage("Admin is Playing");
       }

       @Override
       public void onPaused() {
           showMessage("Admin has Paused");
       }

       @Override
       public void onStopped() {
           showMessage(" Admin has Stopped");
       }

       @Override
       public void onBuffering(boolean b) {
       }

       @Override
       public void onSeekTo(int i) {

           showMessage("Movie is Forwarded by Admin"+i);
           /*long changed = userPlayer.getDurationMillis()-i;
           mCountDownTimer.cancel();
           mTimeLeftInMillis=changed;*/

           // this code will be executed after 2 seconds

       }

   }
}
