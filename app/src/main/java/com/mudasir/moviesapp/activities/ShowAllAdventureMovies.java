package com.mudasir.moviesapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mudasir.moviesapp.R;
import com.mudasir.moviesapp.adapters.MoviesAdapter;
import com.mudasir.moviesapp.adapters.ShowAllMoviesAdapter;
import com.mudasir.moviesapp.listeners.MovieItemClickListener;
import com.mudasir.moviesapp.models.Movies;

import java.util.ArrayList;
import java.util.List;

public class ShowAllAdventureMovies extends AppCompatActivity implements MovieItemClickListener {

    Toolbar mToolbar;
    RecyclerView recyclerView;
    List<Movies> moviesListAdventure;
    private DatabaseReference mDatabaseRefAdventure;
    ShowAllMoviesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_adventure_movies);
        mToolbar=findViewById(R.id.app_bar_showAllAdventureMovies);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ALL Adventure Movies");


        mDatabaseRefAdventure= FirebaseDatabase.getInstance().getReference("Movies").child("Adventure");

        mToolbar=findViewById(R.id.app_bar_showAllAdventureMovies);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ALL Adventure Movies");

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView=findViewById(R.id.rv_show_all_adventureMovies);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,1));

        populateAdventureCategoryMovies();

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
            options = ActivityOptions.makeSceneTransitionAnimation(ShowAllAdventureMovies.this,
                    movieImageView,"sharedName");
            startActivity(intent,options.toBundle());
        }

        // i l make a simple test to see if the click works

        Toast.makeText(this,"item clicked : " + movie.getTitle(),Toast.LENGTH_LONG).show();
        // it works great
    }


    private void populateAdventureCategoryMovies() {

        moviesListAdventure=new ArrayList<>();
        mDatabaseRefAdventure.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()){

                    List<String> AdventureMoviekeys=new ArrayList<>();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        AdventureMoviekeys.add(snapshot.getKey());

                        Movies movies=snapshot.getValue(Movies.class);
                        moviesListAdventure.add(movies);
                    }

                    mAdapter=new ShowAllMoviesAdapter(moviesListAdventure,ShowAllAdventureMovies.this,ShowAllAdventureMovies.this);
                    recyclerView.setAdapter(mAdapter);
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
