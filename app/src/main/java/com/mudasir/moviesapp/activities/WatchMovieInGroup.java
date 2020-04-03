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
import com.google.firebase.database.ValueEventListener;
import com.mudasir.moviesapp.R;
import com.mudasir.moviesapp.adapters.MessagesAdapter;
import com.mudasir.moviesapp.models.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class WatchMovieInGroup extends YouTubeBaseActivity {

    YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListenerGroup;


    String loadVideoUrl="";
    String title="";
    String status="";
    String admin="";


    String GroupName="";
    EditText editTextMessage;
    Button btnSendMessage;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabase;

    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;

    Toolbar mToolbar;
    private String uid="";
    String code = "";
    String UsersName;

    List<messages> messagesList;

    private RecyclerView rvMessages;

    TextView textViewShareCode;

    String SeekTO;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_movie_in_group);


        mToolbar=findViewById(R.id.app_bar_watch_movie_inGroup);
        editTextMessage=findViewById(R.id.et_message);
        btnSendMessage=findViewById(R.id.btnSendMessage);
        textViewShareCode=findViewById(R.id.tv_Share_Code);


        // setting up Recycler View Messages
        rvMessages=findViewById(R.id.rv_messages);
        rvMessages.setHasFixedSize(true);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));

        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();
        uid=mCurrentUser.getUid();

        if (mAuth.getCurrentUser()!=null){
            FetchUserName();
        }

        mDatabaseRef=FirebaseDatabase.getInstance().getReference("groups");

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_left);


        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(WatchMovieInGroup.this)
                        .setTitle("Are You Sure You want to Exit The Group")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create();
                builder.show();

            }
        });


        LoadVideoInYoutubePlayerView();


        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String message= editTextMessage.getText().toString();

                if (!message.isEmpty()){

                    String key=mDatabaseRef.push().getKey();

                    messages messages=new messages(UsersName,message);
                    Map<String ,Object> postValues =  messages.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(key, postValues);

                    mDatabaseRef.child(code).child("Messages").updateChildren(childUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                editTextMessage.setText("");
                            }

                        }
                    });



                }
                else{
                    Toast.makeText(WatchMovieInGroup.this, "Type a Comment", Toast.LENGTH_SHORT).show();
                }
            }
        });



        textViewShareCode.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                SendDataToOtherApps();

            }
        });


    }

    private void LoadVideoInYoutubePlayerView() {

            youTubePlayerView=findViewById(R.id.youtube_player_group);

            onInitializedListenerGroup=new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

                    if (mCurrentUser.getUid().equals(admin)){

                        youTubePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
                            @Override
                            public void onPlaying() {

                                mDatabaseRef.child(code).child("status").setValue("isPlaying");
                            }

                            @Override
                            public void onPaused() {
                                mDatabaseRef.child(code).child("status").setValue("isPaused");
                            }

                            @Override
                            public void onStopped() {

                                mDatabaseRef.child(code).child("status").setValue("isStoped");
                            }

                            @Override
                            public void onBuffering(boolean b) {
                                mDatabaseRef.child(code).child("status").setValue("isBuffering");
                            }

                            @Override
                            public void onSeekTo(int i) {

                                mDatabaseRef.child(code).child("status").setValue(""+i);

                            }
                        });
                        youTubePlayer.loadVideo(loadVideoUrl);
                        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);




                    }
                    else{

                            youTubePlayer.loadVideo(loadVideoUrl);
                            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);

                    }


                    /*youTubePlayer.setShowFullscreenButton(false);*/
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            };



            youTubePlayerView.initialize("AIzaSyDxCaPL1UIJmJvNaGjveyC6pxgW9e281wM",onInitializedListenerGroup);


    }


    @Override
    protected void onStart() {
        super.onStart();

        mCurrentUser=mAuth.getCurrentUser();
        uid=mCurrentUser.getUid();

        messagesList=new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("groups");

        if (getIntent() != null) {
            code = String.valueOf(getIntent().getExtras().get("group_code"));

        }

        mDatabaseRef.child(code).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    loadVideoUrl = dataSnapshot.child("movieUrl").getValue().toString();
                    GroupName=dataSnapshot.child("groupName").getValue().toString();
                    title = dataSnapshot.child("title").getValue().toString();

                    admin=dataSnapshot.child("admin").getValue().toString();
                    status=dataSnapshot.child("status").getValue().toString();

                    mToolbar.setTitle("Movie Name:"+title);
                    mToolbar.setSubtitle("Joined: "+GroupName);

                    RetrieveMessages();

                }
                else {
                    Toast.makeText(WatchMovieInGroup.this, "Group Not Found", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    private void JoinedGroup(){

        String key=mDatabaseRef.push().getKey();

        String message=UsersName+" Has Joined The Group";

        messages messages=new messages(UsersName,message);
        Map<String ,Object> postValues =  messages.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, postValues);

        mDatabaseRef.child(code).child("Messages").updateChildren(childUpdates);

    }


    private void FetchUserName(){

        mDatabase=FirebaseDatabase.getInstance().getReference("Users").child(uid);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UsersName= String.valueOf(dataSnapshot.child("UserName").getValue());
                JoinedGroup();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }




    private void RetrieveMessages(){


        mDatabaseRef.child(code).child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()){

                    messagesList.clear();
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                        messages messages=snapshot.getValue(messages.class);
                        messagesList.add(messages);
                    }

                    MessagesAdapter adapter=new MessagesAdapter(messagesList,WatchMovieInGroup.this);
                    rvMessages.setAdapter(adapter);

                   int lastpos= rvMessages.getAdapter().getItemCount();
                   rvMessages.smoothScrollToPosition(lastpos);
                   adapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(WatchMovieInGroup.this, "No Messages", Toast.LENGTH_SHORT).show();
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

            AlertDialog.Builder dailog=new AlertDialog.Builder(this);
            dailog.setTitle("Confirmation Message");
            dailog.setMessage("Are You Sure Want to Exit The Group");
            dailog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    finish();

                }
            });
            dailog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();

                }
            });

            dailog.create();
            dailog.show();

        }
        return super.onKeyDown(keyCode, event);
    }



    private void SendDataToOtherApps() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Movies App Group Code : "+code);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);

    }

}
