package com.mudasir.moviesapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mudasir.moviesapp.R;
import com.mudasir.moviesapp.adapters.ShowAllMoviesAdapter;
import com.mudasir.moviesapp.listeners.MovieItemClickListener;
import com.mudasir.moviesapp.models.Movies;

import java.util.ArrayList;
import java.util.List;

public class ShowAllLoveMovies extends AppCompatActivity implements MovieItemClickListener {

     Toolbar mToolbar;
     RecyclerView recyclerView;
     List<Movies> moviesListLove;
     DatabaseReference mDatabaseRefLove;
     ShowAllMoviesAdapter mAdapter;
     private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_love_movies);

        mProgress=findViewById(R.id.progressBarLoveMovie);

        mToolbar=findViewById(R.id.app_bar_showAllLoveMovies);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ALL Love Movies");

        mToolbar.setNavigationOnClickListener(v -> finish());

        mDatabaseRefLove= FirebaseDatabase.getInstance().getReference("Movies").child("Love");

        recyclerView=findViewById(R.id.rv_show_all_loveMovies);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,1));

        mProgress.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            populateLoveCategoryMovies();
            mProgress.setVisibility(View.INVISIBLE);
        },1000);


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
            options = ActivityOptions.makeSceneTransitionAnimation(ShowAllLoveMovies.this,
                    movieImageView,"sharedName");
            startActivity(intent,options.toBundle());
        }

        // i l make a simple test to see if the click works
        Toast.makeText(this,"item clicked : " + movie.getTitle(),Toast.LENGTH_LONG).show();
        // it works great

    }



    private void populateLoveCategoryMovies() {

        moviesListLove =new ArrayList<>();
        mDatabaseRefLove.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()){

                    moviesListLove.clear();
                    List<String> ActionMoviekeys=new ArrayList<>();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        ActionMoviekeys.add(snapshot.getKey());

                        Movies movies=snapshot.getValue(Movies.class);
                        moviesListLove.add(movies);

                    }

                    mAdapter=new ShowAllMoviesAdapter(moviesListLove,ShowAllLoveMovies.this,ShowAllLoveMovies.this);
                    recyclerView.setAdapter(mAdapter);

                }
                else{
                    Toast.makeText(ShowAllLoveMovies.this, "No Movies", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }




}
