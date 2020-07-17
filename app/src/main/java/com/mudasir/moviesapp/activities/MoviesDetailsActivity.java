package com.mudasir.moviesapp.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Movie;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mudasir.moviesapp.R;
import com.mudasir.moviesapp.models.Group;
import com.mudasir.moviesapp.models.Movies;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.mudasir.moviesapp.R.color.colorAccent;

public class MoviesDetailsActivity extends AppCompatActivity {


    private static final String TAG = "MoviesDetailsActivity";

    private ImageView MovieThumbnailImg,MovieCoverImg ,ImagebtnFav;
    private TextView tv_title,tv_description;
    private FloatingActionButton play_fab;

    Toolbar mToolbar;
    private Button btnGroup;

    private ProgressDialog mprogress;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabaseFav;

    private FirebaseUser mCurrentuser;
    private FirebaseAuth mAuth;

    private String uid="";
    private String key="";

    private DatabaseReference mDataBase;
    private String category;


    //String Favkey;

    private Boolean showingFirst=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mToolbar=findViewById(R.id.app_bar_movie_details);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // ini views
        iniViews();



    }

    void iniViews() {

        mAuth=FirebaseAuth.getInstance();
        mCurrentuser=mAuth.getCurrentUser();
        uid=mCurrentuser.getUid();

        mDatabaseRef= FirebaseDatabase.getInstance().getReference("groups");
        mDataBase=FirebaseDatabase.getInstance().getReference("MyGroups");

        mDatabaseFav=FirebaseDatabase.getInstance().getReference("Favourites").child(uid);


        btnGroup=findViewById(R.id.btn_CreateGroup);
        play_fab = findViewById(R.id.play_movie);

        String movieTitle = getIntent().getExtras().getString("title");
        String movieDes = getIntent().getExtras().getString("des");
        String imageResourceId = getIntent().getExtras().getString("imgURL");
        String imagecover = getIntent().getExtras().getString("imgCover");
        final String video_url = getIntent().getExtras().getString("video_url");


      /*  if (!getIntent().getExtras().get("key").equals(null)){
            Favkey=getIntent().getExtras().getString("key");
        }
*/


        mDatabaseFav.orderByChild("title").equalTo(movieTitle).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    showingFirst=false;
                    ImagebtnFav.setImageResource(R.drawable.clicked_fav);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        MovieThumbnailImg = findViewById(R.id.detail_movie_img);
        Glide.with(this).load(imageResourceId).into(MovieThumbnailImg);

        MovieCoverImg = findViewById(R.id.detail_movie_cover);
        Glide.with(this).load(imagecover).into(MovieCoverImg);

        tv_title = findViewById(R.id.detail_movie_title);
        tv_title.setText(movieTitle);
        getSupportActionBar().setTitle(movieTitle);
        tv_description = findViewById(R.id.tv_description);
        tv_description.setText(movieDes);
        tv_description.setMovementMethod(new ScrollingMovementMethod());
        // setup animation
        MovieCoverImg.setAnimation(AnimationUtils.loadAnimation(this,R.anim.scale_animation));
        play_fab.setAnimation(AnimationUtils.loadAnimation(this,R.anim.scale_animation));
        btnGroup.setAnimation(AnimationUtils.loadAnimation(this,R.anim.scale_animation));


        ImagebtnFav=findViewById(R.id.favImageBtn);


        ImagebtnFav.setOnClickListener(v -> {

            if (showingFirst)
            {

                String key=  mDatabaseFav.push().getKey();
                Movies movies=new Movies(movieTitle,movieDes,imageResourceId,category,video_url,key);
                Map<String,Object> postvalues=movies.MoviestoMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put (key, postvalues);

                mDatabaseFav.updateChildren(childUpdates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){

                        showingFirst=false;
                        ImagebtnFav.setImageResource(R.drawable.clicked_fav);
                        Toast.makeText(MoviesDetailsActivity.this, "Added to Fav List", Toast.LENGTH_SHORT).show();

                    }
                });


            }
            else
            {
                mDatabaseFav.child(key).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){

                        showingFirst=true;
                        ImagebtnFav.setImageResource(R.drawable.fav);
                        Toast.makeText(MoviesDetailsActivity.this, movieTitle+" Removed From Favourite", Toast.LENGTH_SHORT).show();

                    }
                    else{

                        Toast.makeText(MoviesDetailsActivity.this, "Some Thing Went Wrong!!", Toast.LENGTH_SHORT).show();

                    }
                });

            }



        });


        play_fab.setOnClickListener(v -> {

            Intent SendToPlayerActivity=new Intent(MoviesDetailsActivity.this,PlayerActivity.class);
            SendToPlayerActivity.putExtra("video_url",video_url);
            SendToPlayerActivity.putExtra("title",movieTitle);
            SendToPlayerActivity.putExtra("description",movieDes);

            startActivity(SendToPlayerActivity);

        });

        btnGroup.setOnClickListener(v -> {

            View view=LayoutInflater.from(MoviesDetailsActivity.this).inflate(R.layout.create_group_ui,null);

            AlertDialog alertDialog = new AlertDialog.Builder(MoviesDetailsActivity.this).create();
            alertDialog.setTitle("Creating Group...");
            alertDialog.setIcon(R.drawable.team);
            alertDialog.setCancelable(false);

             final EditText etGroupName = view.findViewById(R.id.et_group_name);
             TextView tvGroupCode = view.findViewById(R.id.tv_group_code);
             String random=GenerateRandomNumber(8);
             tvGroupCode.setText(random);

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {

                String GroupName= etGroupName.getText().toString();
                String GroupCode=tvGroupCode.getText().toString();

                if (GroupName.isEmpty()){
                    Toast.makeText(MoviesDetailsActivity.this, "You Must Give a Group Name", Toast.LENGTH_SHORT).show();
                }
                else{


                    mprogress=new ProgressDialog(MoviesDetailsActivity.this);
                    mprogress.setTitle("Creating Group");
                    mprogress.setMessage("Please Wait We Are Creating A Group For You.");
                    mprogress.show();

                    new Handler().postDelayed(() -> {

                        String status="isPlaying";

                        Group group=new Group(GroupName,GroupCode,movieTitle,video_url,status,uid);
                        Map<String, Object> postValues = group.toMap();

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(GroupCode, postValues);

                        Map<String, Object> obj= new HashMap<>();
                        obj.put(GroupCode, postValues);

                        mDataBase.child(uid).updateChildren(obj);
                        mDatabaseRef.updateChildren(childUpdates);

                        Intent sendTOWatchMoviesInGroupActivity=new Intent(MoviesDetailsActivity.this,
                                WatchMovieInGroup.class);
                        sendTOWatchMoviesInGroupActivity.putExtra("group_code",GroupCode);
                        startActivity(sendTOWatchMoviesInGroupActivity);


                        mprogress.dismiss();
                    },5000);


                }



            });

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> alertDialog.dismiss());

            alertDialog.setView(view);
            alertDialog.show();


        });







    }

    @Override
    protected void onStart() {
        super.onStart();
        key=getIntent().getExtras().getString("key");
        category=getIntent().getExtras().getString("category");


    }

    String GenerateRandomNumber(int charLength) {
        return String.valueOf(charLength < 1 ? 0 : new Random()
                .nextInt((9 * (int) Math.pow(10, charLength - 1)) - 1)
                + (int) Math.pow(10, charLength - 1));
    }



}
