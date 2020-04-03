package com.mudasir.moviesapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mudasir.moviesapp.R;
import com.mudasir.moviesapp.adapters.MoviesAdapter;
import com.mudasir.moviesapp.adapters.SlidesPagerAdapter;
import com.mudasir.moviesapp.listeners.MovieItemClickListener;
import com.mudasir.moviesapp.models.Group;
import com.mudasir.moviesapp.models.Movies;
import com.mudasir.moviesapp.models.Slide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity  implements MovieItemClickListener {

    Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseRefLove,mDatabaseRefAction,mDatabaseRefAdventure,mDatabaseRefComedy;

    private DatabaseReference mRefGroups;


   String uid;

   List<Slide> slideList;

   private ViewPager sliderPager;
   private TabLayout indicator;
   private BottomNavigationView bottomNavigationView;
   NestedScrollView nestedScrollView;

   ProgressDialog mProgress;
   private ProgressBar mProgressBar;
   private List<Movies> moviesListLove ,moviesListAction,moviesListAdventure,moviesListComedy;
   private RecyclerView movieRvLove,movieRvAction,movieRvAdventure,movieRvComedy;
   TextView tvLove,tvAction,tvAdventure,tvComedy;

   Button btnShowAllLoveMovies,btnShowAllAdventureMovies,btnShowAllComedyMovies,btnShowAllActionMovies;

    private static final String TAG = "MainActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();


        if (FirebaseAuth.getInstance().getCurrentUser()!=null){

            mDatabaseRefLove=FirebaseDatabase.getInstance().getReference("Movies").child("Love");
            mDatabaseRefAction=FirebaseDatabase.getInstance().getReference("Movies").child("Action");
            mDatabaseRefAdventure=FirebaseDatabase.getInstance().getReference("Movies").child("Adventure");
            mDatabaseRefComedy=FirebaseDatabase.getInstance().getReference("Movies").child("Comedy");

            mCurrentUser=mAuth.getCurrentUser();
            uid= mCurrentUser.getUid();


            setUpSlider();


            if (isConnectedToInternet()){


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        movieRvAction.setVisibility(View.VISIBLE);
                        movieRvAdventure.setVisibility(View.VISIBLE);
                        movieRvComedy.setVisibility(View.VISIBLE);
                        movieRvLove.setVisibility(View.VISIBLE);


                        // retriving Love Category Movies
                        populateLoveCategoryMovies();


                        // retriving Action Category Movies
                        populateActionCategoryMovies();


                        // retriving Adventure Category Movies
                        populateAdventureCategoryMovies();


                        // retriving Comedy Category Movies
                        populateComedyCategoryMovies();

                        mProgressBar.setVisibility(View.INVISIBLE);




                    }
                },1000);



            }
            else{

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Hiding Recycler Views
                        movieRvAction.setVisibility(View.INVISIBLE);
                        movieRvAdventure.setVisibility(View.INVISIBLE);
                        movieRvComedy.setVisibility(View.INVISIBLE);
                        movieRvLove.setVisibility(View.INVISIBLE);

                        // hiding TextView

                        tvAction.setVisibility(View.INVISIBLE);
                        tvAdventure.setVisibility(View.INVISIBLE);
                        tvComedy.setVisibility(View.INVISIBLE);

                        mProgressBar.setVisibility(View.INVISIBLE);
                        tvLove.setVisibility(View.VISIBLE);
                        tvLove.setText("No Internet Connection!!!");

                        btnShowAllActionMovies.setVisibility(View.INVISIBLE);
                        btnShowAllAdventureMovies.setVisibility(View.INVISIBLE);
                        btnShowAllComedyMovies.setVisibility(View.INVISIBLE);
                        btnShowAllLoveMovies.setVisibility(View.INVISIBLE);



                    }
                },1000);



            }

        }

    }

   private Boolean isConnectedToInternet(){

       ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
       if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
               connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
           //we are connected to a network
           return true;
       }
       else
           return false;
   }


    private void setUpSlider() {

        slideList=new ArrayList<>();
        slideList.add(new Slide(R.drawable.download,"Radhe"));
        slideList.add(new Slide(R.drawable.download1,"Movie"));
        slideList.add(new Slide(R.drawable.download2,"Bazzar"));
        slideList.add(new Slide(R.drawable.download3,"Tiger Zinda Hy"));


        SlidesPagerAdapter slidesPagerAdapter=new SlidesPagerAdapter(MainActivity.this,slideList);
        sliderPager.setAdapter(slidesPagerAdapter);

        //setup timer for Slides
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);

        indicator.setupWithViewPager(sliderPager,true);
    }

    private void init() {

        mProgressBar=findViewById(R.id.progress_bar);
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Movies App");
        setSupportActionBar(mToolbar);

        tvLove = findViewById(R.id.tv_love_movies);
        tvAction = findViewById(R.id.tv_action_movies);
        tvAdventure = findViewById(R.id.tv_adventure_movies);
        tvComedy = findViewById(R.id.tv_comedy_movies);

        nestedScrollView = findViewById(R.id.nestedScrollView);
        mAuth = FirebaseAuth.getInstance();
        sliderPager = findViewById(R.id.slide_pager);
        indicator = findViewById(R.id.indicator);
        movieRvLove = findViewById(R.id.rv_movies_love);
        movieRvAction = findViewById(R.id.rv_movies_Action);
        movieRvAdventure = findViewById(R.id.rv_moviesAdventure);
        movieRvComedy = findViewById(R.id.rv_movies_comedy);
        bottomNavigationView = findViewById(R.id.navigation);

        btnShowAllActionMovies=findViewById(R.id.showAllActionMovies);
        btnShowAllAdventureMovies=findViewById(R.id.showAllAdventureMovies);
        btnShowAllComedyMovies=findViewById(R.id.showAllComedyMovies);
        btnShowAllLoveMovies=findViewById(R.id.showAllLoveMovies);

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY>0 || scrollY<0 ){
                    bottomNavigationView.setVisibility(View.INVISIBLE);
                }
                else{
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }

            }
        });


        btnShowAllLoveMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Listener Of All Love Movies
                startActivity(new Intent(MainActivity.this,ShowAllLoveMovies.class));

            }
        });

        btnShowAllComedyMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Listener Of All Comedy Movies

                startActivity(new Intent(MainActivity.this,ShowAllComedyMovies.class));
            }
        });

        btnShowAllAdventureMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Listener Of All Adventure Movies

                startActivity(new Intent(MainActivity.this,ShowAllAdventureMovies.class));

            }
        });


        btnShowAllActionMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Listener Of All Action Movies

                startActivity(new Intent(MainActivity.this,ShowAllActionMovies.class));

            }
        });


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {

                switch (item.getItemId()) {
                    case R.id.home:

                        if (isConnectedToInternet()){

                            //startActivity(new Intent(MainActivity.this,MainActivity.class));
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Please Connect To Internet And Try Again!", Toast.LENGTH_SHORT).show();

                        }

                        break;
                    case R.id.groups:
                        if (isConnectedToInternet()){
                            startActivity(new Intent(MainActivity.this,GroupsActivity.class));
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Please Connect To Internet And Try Again!", Toast.LENGTH_SHORT).show();

                        }
                        break;
                    case R.id.join:

                        if (isConnectedToInternet()){
                            JoinIntoGroup();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Please Connect To Internet And Try Again!", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case R.id.settings:

                        startActivity(new Intent(MainActivity.this,SettingsActivity.class));

                        break;
                    case R.id.logout:
                        if (isConnectedToInternet()){
                            MethodLogOutUser();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Please Connect To Internet And Try Again!", Toast.LENGTH_SHORT).show();
                        }

                        break;
                }


                return true;
            };





    private void populateComedyCategoryMovies() {
        moviesListComedy=new ArrayList<>();
        mDatabaseRefComedy.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()){

                    btnShowAllComedyMovies.setVisibility(View.VISIBLE);
                    tvComedy.setVisibility(View.VISIBLE);

                    moviesListComedy.clear();
                    List<String> ComedyMoviekeys=new ArrayList<>();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        ComedyMoviekeys.add(snapshot.getKey());

                        Movies movies=snapshot.getValue(Movies.class);
                        moviesListComedy.add(movies);

                    }

                    MoviesAdapter moviesAdapter=new MoviesAdapter(MainActivity.this,moviesListComedy,MainActivity.this);
                    movieRvComedy.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL,false));
                    movieRvComedy.setAdapter(moviesAdapter);

                }
                else{
                    movieRvComedy.setVisibility(View.GONE);
                    tvComedy.setVisibility(View.GONE);
                    btnShowAllComedyMovies.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void populateAdventureCategoryMovies() {
        moviesListAdventure=new ArrayList<>();
        mDatabaseRefAdventure.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()){

                    btnShowAllAdventureMovies.setVisibility(View.VISIBLE);
                    tvAdventure.setVisibility(View.VISIBLE);

                    moviesListAdventure.clear();
                    List<String> AdventureMoviekeys=new ArrayList<>();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        AdventureMoviekeys.add(snapshot.getKey());

                        Movies movies=snapshot.getValue(Movies.class);
                        moviesListAdventure.add(movies);

                    }



                    MoviesAdapter moviesAdapter=new MoviesAdapter(MainActivity.this,moviesListAdventure,MainActivity.this);
                    movieRvAdventure.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL,false));
                    movieRvAdventure.setAdapter(moviesAdapter);

                }
                else{
                    movieRvAdventure.setVisibility(View.GONE);
                    tvAdventure.setVisibility(View.GONE);
                    btnShowAllAdventureMovies.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populateActionCategoryMovies() {
        moviesListAction=new ArrayList<>();
        mDatabaseRefAction.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()){

                    btnShowAllActionMovies.setVisibility(View.VISIBLE);
                    tvAction.setVisibility(View.VISIBLE);

                    moviesListAction.clear();
                    List<String> ActionMoviekeys=new ArrayList<>();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        ActionMoviekeys.add(snapshot.getKey());

                        Movies movies=snapshot.getValue(Movies.class);
                        moviesListAction.add(movies);

                    }


                    MoviesAdapter moviesAdapter=new MoviesAdapter(MainActivity.this,moviesListAction,MainActivity.this);
                    movieRvAction.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL,false));
                    movieRvAction.setAdapter(moviesAdapter);





                }
                else{

                    movieRvAction.setVisibility(View.GONE);
                    tvAction.setVisibility(View.GONE);
                    btnShowAllActionMovies.setVisibility(View.GONE);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populateLoveCategoryMovies() {
        moviesListLove=new ArrayList<>();
        mDatabaseRefLove.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.hasChildren()){

                    btnShowAllLoveMovies.setVisibility(View.VISIBLE);
                    tvLove.setVisibility(View.VISIBLE);

                    moviesListLove.clear();
                    List<String> LoveMoviekeys=new ArrayList<>();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        LoveMoviekeys.add(snapshot.getKey());

                        Movies movies=snapshot.getValue(Movies.class);
                        moviesListLove.add(movies);

                    }


                    MoviesAdapter moviesAdapter=new MoviesAdapter(MainActivity.this,moviesListLove,MainActivity.this);
                    movieRvLove.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL,false));
                    movieRvLove.setAdapter(moviesAdapter);

                }
                else{

                    movieRvLove.setVisibility(View.GONE);
                    tvLove.setVisibility(View.GONE);
                    btnShowAllLoveMovies.setVisibility(View.GONE);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mCurrentUser = mAuth.getCurrentUser();

        if (mCurrentUser == null) {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else {





        }

    }



    private void MethodLogOutUser() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setIcon(R.drawable.confirm);
        builder.setMessage("Are You Sure You Want to Log Out");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mProgress=new ProgressDialog(MainActivity.this);
                mProgress.setTitle("Logging Off...");
                mProgress.setMessage("Please Wait");
                mProgress.show();

                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                mProgress.dismiss();

            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    private void JoinIntoGroup() {

        View view=LayoutInflater.from(MainActivity.this).inflate(R.layout.create_join_ui,null);

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Joining Group...");
        alertDialog.setIcon(R.drawable.link);
        alertDialog.setCancelable(false);
        final EditText etGroupCode = (EditText) view.findViewById(R.id.et_group_code);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

             String code=etGroupCode.getText().toString();


                mRefGroups=FirebaseDatabase.getInstance().getReference("groups").child(code);
                mRefGroups.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        if (dataSnapshot.exists()){

                            Intent sendTOWatchMoviesInGroupActivity=new Intent(MainActivity.this,
                                    WatchMovieInGroup.class);
                            sendTOWatchMoviesInGroupActivity.putExtra("group_code",code);
                            startActivity(sendTOWatchMoviesInGroupActivity);

                        }
                        else{

                            Toast.makeText(MainActivity.this, "Group Not Found", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });

        alertDialog.setView(view);
        alertDialog.show();


    }

    @Override
    public void onMovieClick(Movies movie, ImageView movieImageView) {
        // here we send movie information to detail activity
        // also we ll create the transition animation between the two activity

        Intent intent = new Intent(this, MoviesDetailsActivity.class);
        // send movie information to deatilActivity
        intent.putExtra("title",movie.getTitle());
        intent.putExtra("imgURL",movie.getThumbnail());
        intent.putExtra("imgCover",movie.getThumbnail());
        intent.putExtra("des",movie.getDescription());
        intent.putExtra("video_url",movie.getStreamingLink());
        // lets crezte the animation
        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,
                    movieImageView,"sharedName");
            startActivity(intent,options.toBundle());
        }

        // i l make a simple test to see if the click works

        Toast.makeText(this,"item clicked : " + movie.getTitle(),Toast.LENGTH_LONG).show();
        // it works great
    }


    public class SliderTimer extends TimerTask{

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (sliderPager.getCurrentItem()<slideList.size()-1){
                        sliderPager.setCurrentItem(sliderPager.getCurrentItem()+1);
                    }
                    else{
                        sliderPager.setCurrentItem(0);
                    }
                }
            });
        }
    }





}
