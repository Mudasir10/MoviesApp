package com.mudasir.moviesapp.listeners;

import android.graphics.Movie;
import android.widget.ImageView;

import com.mudasir.moviesapp.models.Movies;

public interface MovieItemClickListener {
    void onMovieClick(Movies movie, ImageView movieImageView); // we will need the imageview to make the shared animation between the two activity
}
