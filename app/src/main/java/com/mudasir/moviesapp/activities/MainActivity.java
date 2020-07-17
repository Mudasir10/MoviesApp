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
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mudasir.moviesapp.R;
import com.mudasir.moviesapp.adapters.MoviesAdapter;
import com.mudasir.moviesapp.adapters.SlidesPagerAdapter;
import com.mudasir.moviesapp.listeners.MovieItemClickListener;
import com.mudasir.moviesapp.models.Movies;
import com.mudasir.moviesapp.models.Slide;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements MovieItemClickListener {

    Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseRefLove, mDatabaseRefAction, mDatabaseRefAdventure, mDatabaseRefComedy, mDatabaseFav;
    private DatabaseReference mRefGroups;
    private DatabaseReference mRefTrailor;


    String uid;
    List<Slide> slideList;

    private ViewPager sliderPager;
    private TabLayout indicator;
    private BottomNavigationView bottomNavigationView;
    NestedScrollView nestedScrollView;

    ProgressDialog mProgress;
    private ProgressBar mProgressBar;
    private List<Movies> moviesListLove, moviesListAction, moviesListAdventure, moviesListComedy;
    private List<Movies> moviesListFavourite;
    private RecyclerView movieRvLove, movieRvAction, movieRvAdventure, movieRvComedy, movieRvFav;
    TextView tvLove, tvAction, tvAdventure, tvComedy, tvFavourite;

    Button btnShowAllLoveMovies, btnShowAllAdventureMovies, btnShowAllComedyMovies, btnShowAllActionMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            mCurrentUser = mAuth.getCurrentUser();
            uid = mCurrentUser.getUid();

            mDatabaseFav = FirebaseDatabase.getInstance().getReference("Favourites").child(uid);
            mDatabaseRefLove = FirebaseDatabase.getInstance().getReference("Movies").child("Love");
            mDatabaseRefAction = FirebaseDatabase.getInstance().getReference("Movies").child("Action");
            mDatabaseRefAdventure = FirebaseDatabase.getInstance().getReference("Movies").child("Adventure");
            mDatabaseRefComedy = FirebaseDatabase.getInstance().getReference("Movies").child("Comedy");
            mRefTrailor=FirebaseDatabase.getInstance().getReference("MovieTrailors");



            if (isConnectedToInternet()) {
                mProgressBar.setVisibility(View.VISIBLE);
                setUpSlider();
                loadallData();

            } else {
                HideAllData();

            }

        }


    }

    private void HideAllData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Hiding Recycler Views
                movieRvAction.setVisibility(View.INVISIBLE);
                movieRvAdventure.setVisibility(View.INVISIBLE);
                movieRvComedy.setVisibility(View.INVISIBLE);
                movieRvLove.setVisibility(View.INVISIBLE);
                movieRvFav.setVisibility(View.INVISIBLE);

                // hiding TextView

                tvAction.setVisibility(View.INVISIBLE);
                tvAdventure.setVisibility(View.INVISIBLE);
                tvComedy.setVisibility(View.INVISIBLE);
                tvFavourite.setVisibility(View.INVISIBLE);

                mProgressBar.setVisibility(View.INVISIBLE);
                tvLove.setVisibility(View.VISIBLE);
                tvLove.setText("No Internet Connection!!!");

                btnShowAllActionMovies.setVisibility(View.INVISIBLE);
                btnShowAllAdventureMovies.setVisibility(View.INVISIBLE);
                btnShowAllComedyMovies.setVisibility(View.INVISIBLE);
                btnShowAllLoveMovies.setVisibility(View.INVISIBLE);


            }
        }, 1000);
    }

    private void loadallData() {
        new Handler().postDelayed(() -> {

            movieRvAction.setVisibility(View.VISIBLE);
            movieRvAdventure.setVisibility(View.VISIBLE);
            movieRvComedy.setVisibility(View.VISIBLE);
            movieRvLove.setVisibility(View.VISIBLE);

            //retrieving Favourite Movies
            populateFavouriteMovies();

            // retriving Love Category Movies
            populateLoveCategoryMovies();


            // retriving Action Category Movies
            populateActionCategoryMovies();


            // retriving Adventure Category Movies
            populateAdventureCategoryMovies();


            // retriving Comedy Category Movies
            populateComedyCategoryMovies();

            mProgressBar.setVisibility(View.INVISIBLE);


        }, 1000);
    }

    private void populateFavouriteMovies() {
        moviesListFavourite = new ArrayList<>();

        Query query = mDatabaseFav.limitToLast(20);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.hasChildren()) {

                    tvFavourite.setVisibility(View.VISIBLE);
                    moviesListFavourite.clear();
                    List<String> FavMoviekeys = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FavMoviekeys.add(snapshot.getKey());

                        Movies movies = snapshot.getValue(Movies.class);
                        moviesListFavourite.add(movies);
                    }


                    MoviesAdapter moviesAdapter = new MoviesAdapter(MainActivity.this, moviesListFavourite, MainActivity.this);
                    movieRvFav.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    movieRvFav.setAdapter(moviesAdapter);

                } else {
                    movieRvFav.setVisibility(View.GONE);
                    tvFavourite.setVisibility(View.GONE);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private Boolean isConnectedToInternet() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else
            return false;
    }


    private void setUpSlider() {

        slideList = new ArrayList<>();

        Query query=mRefTrailor.limitToLast(10);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        slideList.add(snapshot.getValue(Slide.class));
                    }
                    SlidesPagerAdapter slidesPagerAdapter = new SlidesPagerAdapter(MainActivity.this, slideList);
                    sliderPager.setAdapter(slidesPagerAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //setup timer for Slides
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);

        indicator.setupWithViewPager(sliderPager, true);
    }

    private void init() {

        mProgressBar = findViewById(R.id.progress_bar);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setSubtitle("Movies Room");

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            if (Math.abs(verticalOffset) == appBarLayout1.getTotalScrollRange()) {
                // Collapsed
            } else if (verticalOffset == 0) {
                // Expanded
            } else {
                // Somewhere in between
            }
        });

        tvLove = findViewById(R.id.tv_love_movies);
        tvAction = findViewById(R.id.tv_action_movies);
        tvAdventure = findViewById(R.id.tv_adventure_movies);
        tvComedy = findViewById(R.id.tv_comedy_movies);
        tvFavourite = findViewById(R.id.tv_fav_movies);

        mAuth = FirebaseAuth.getInstance();

        nestedScrollView = findViewById(R.id.nestedScrollView);

        sliderPager = findViewById(R.id.slide_pager);
        indicator = findViewById(R.id.indicator);
        movieRvLove = findViewById(R.id.rv_movies_love);
        movieRvAction = findViewById(R.id.rv_movies_Action);
        movieRvAdventure = findViewById(R.id.rv_moviesAdventure);
        movieRvComedy = findViewById(R.id.rv_movies_comedy);
        movieRvFav = findViewById(R.id.rv_movies_favourite);

        bottomNavigationView = findViewById(R.id.navigation);

        btnShowAllActionMovies = findViewById(R.id.showAllActionMovies);
        btnShowAllAdventureMovies = findViewById(R.id.showAllAdventureMovies);
        btnShowAllComedyMovies = findViewById(R.id.showAllComedyMovies);
        btnShowAllLoveMovies = findViewById(R.id.showAllLoveMovies);

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY > 0 || scrollY < 0) {
                    bottomNavigationView.setVisibility(View.INVISIBLE);
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }

            }
        });


        btnShowAllLoveMovies.setOnClickListener(v -> {
            //Listener Of All Love Movies
            startActivity(new Intent(MainActivity.this, ShowAllLoveMovies.class));

        });

        btnShowAllComedyMovies.setOnClickListener(v -> {
            //Listener Of All Comedy Movies

            startActivity(new Intent(MainActivity.this, ShowAllComedyMovies.class));
        });

        btnShowAllAdventureMovies.setOnClickListener(v -> {

            //Listener Of All Adventure Movies

            startActivity(new Intent(MainActivity.this, ShowAllAdventureMovies.class));

        });


        btnShowAllActionMovies.setOnClickListener(v -> {
            //Listener Of All Action Movies

            startActivity(new Intent(MainActivity.this, ShowAllActionMovies.class));

        });



    }



    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {

                switch (item.getItemId()) {
                    case R.id.home:

                        if (isConnectedToInternet()) {
                         //   startActivity(new Intent(MainActivity.this,MainActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "Please Connect To Internet And Try Again!", Toast.LENGTH_SHORT).show();

                        }

                        break;
                    case R.id.groups:
                        if (isConnectedToInternet()) {
                            startActivity(new Intent(MainActivity.this, GroupsActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "Please Connect To Internet And Try Again!", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.join:

                        if (isConnectedToInternet()) {
                            JoinIntoGroup();
                        } else {
                            Toast.makeText(MainActivity.this, "Please Connect To Internet And Try Again!", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case R.id.settings:
                        if (isConnectedToInternet()){
                            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Please Connect To Internet And Try Again!", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case R.id.logout:
                        if (isConnectedToInternet()) {
                            MethodLogOutUser();
                        } else {
                            Toast.makeText(MainActivity.this, "Please Connect To Internet And Try Again!", Toast.LENGTH_SHORT).show();
                        }

                        break;
                }


                return true;
            };


    private void populateComedyCategoryMovies() {

        moviesListComedy = new ArrayList<>();
        Query query = mDatabaseRefComedy.limitToLast(20);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {

                    if (dataSnapshot.getChildrenCount()>=5){
                        btnShowAllComedyMovies.setVisibility(View.VISIBLE);
                    }

                    tvComedy.setVisibility(View.VISIBLE);

                    moviesListComedy.clear();
                    List<String> ComedyMoviekeys = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ComedyMoviekeys.add(snapshot.getKey());

                        Movies movies = snapshot.getValue(Movies.class);
                        moviesListComedy.add(movies);

                    }

                    MoviesAdapter moviesAdapter = new MoviesAdapter(MainActivity.this, moviesListComedy, MainActivity.this);
                    movieRvComedy.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    movieRvComedy.setAdapter(moviesAdapter);

                } else {
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
        moviesListAdventure = new ArrayList<>();

        Query query = mDatabaseRefAdventure.limitToLast(20);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {

                    if (dataSnapshot.getChildrenCount()>=5){
                        btnShowAllAdventureMovies.setVisibility(View.VISIBLE);
                    }
                    tvAdventure.setVisibility(View.VISIBLE);

                    moviesListAdventure.clear();
                    List<String> AdventureMoviekeys = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AdventureMoviekeys.add(snapshot.getKey());

                        Movies movies = snapshot.getValue(Movies.class);
                        moviesListAdventure.add(movies);

                    }


                    MoviesAdapter moviesAdapter = new MoviesAdapter(MainActivity.this, moviesListAdventure, MainActivity.this);
                    movieRvAdventure.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    movieRvAdventure.setAdapter(moviesAdapter);

                } else {
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
        moviesListAction = new ArrayList<>();

        Query query = mDatabaseRefAction.limitToLast(20);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {
                    if (dataSnapshot.getChildrenCount()>=5){
                        btnShowAllActionMovies.setVisibility(View.VISIBLE);
                    }

                    tvAction.setVisibility(View.VISIBLE);

                    moviesListAction.clear();
                    List<String> ActionMoviekeys = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ActionMoviekeys.add(snapshot.getKey());

                        Movies movies = snapshot.getValue(Movies.class);
                        moviesListAction.add(movies);

                    }


                    MoviesAdapter moviesAdapter = new MoviesAdapter(MainActivity.this, moviesListAction, MainActivity.this);
                    movieRvAction.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    movieRvAction.setAdapter(moviesAdapter);


                } else {

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
        moviesListLove = new ArrayList<>();

        Query query = mDatabaseRefLove.limitToLast(20);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {

                    if (dataSnapshot.getChildrenCount()>=5){
                        btnShowAllLoveMovies.setVisibility(View.VISIBLE);
                    }
                    tvLove.setVisibility(View.VISIBLE);

                    moviesListLove.clear();
                    List<String> LoveMoviekeys = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        LoveMoviekeys.add(snapshot.getKey());

                        Movies movies = snapshot.getValue(Movies.class);
                        moviesListLove.add(movies);

                    }

                    MoviesAdapter moviesAdapter = new MoviesAdapter(MainActivity.this, moviesListLove, MainActivity.this);
                    movieRvLove.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    movieRvLove.setAdapter(moviesAdapter);

                } else {

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

        mAuth = FirebaseAuth.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }

    }


    private void MethodLogOutUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setIcon(R.drawable.confirm);
        builder.setMessage("Are You Sure You Want to Log Out");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {

            mProgress = new ProgressDialog(MainActivity.this);
            mProgress.setTitle("Logging Off...");
            mProgress.setMessage("Please Wait");
            mProgress.show();

            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            mProgress.dismiss();

        });
        builder.setNegativeButton("cancel", (dialog, which) -> dialog.dismiss());
        builder.create();
        builder.show();
    }

    private void JoinIntoGroup() {

        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.create_join_ui, null);

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Joining Group...");
        alertDialog.setIcon(R.drawable.link);
        alertDialog.setCancelable(false);
        final EditText etGroupCode = (EditText) view.findViewById(R.id.et_group_code);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {

            String code = etGroupCode.getText().toString();


            mRefGroups = FirebaseDatabase.getInstance().getReference("groups").child(code);
            mRefGroups.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if (dataSnapshot.exists()) {

                        Intent sendTOWatchMoviesInGroupActivity = new Intent(MainActivity.this,
                                WatchMovieInGroup.class);
                        sendTOWatchMoviesInGroupActivity.putExtra("group_code", code);
                        startActivity(sendTOWatchMoviesInGroupActivity);

                    } else {
                        Toast.makeText(MainActivity.this, "Group Not Found", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "cancel", (dialog, which) -> dialog.dismiss());

        alertDialog.setView(view);
        alertDialog.show();


    }

    @Override
    public void onMovieClick(Movies movie, ImageView movieImageView) {
        // here we send movie information to detail activity
        // also we ll create the transition animation between the two activity

        Intent intent = new Intent(MainActivity.this, MoviesDetailsActivity.class);
        // send movie information to deatilActivity
        intent.putExtra("title", movie.getTitle());
        intent.putExtra("imgURL", movie.getThumbnail());
        intent.putExtra("imgCover", movie.getThumbnail());
        intent.putExtra("des", movie.getDescription());
        intent.putExtra("video_url", movie.getStreamingLink());
        intent.putExtra("category", movie.getCategory());
        intent.putExtra("key", movie.getKey());

        // lets crezte the animation
        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,
                    movieImageView, "sharedName");
            startActivity(intent, options.toBundle());
        }

        // i l make a simple test to see if the click works
        // it works great
    }


    public class SliderTimer extends TimerTask {
        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (sliderPager.getCurrentItem() < slideList.size() - 1) {
                        sliderPager.setCurrentItem(sliderPager.getCurrentItem() + 1);
                    } else {
                        sliderPager.setCurrentItem(0);
                    }
                }
            });
        }
    }


}
