package com.mudasir.moviesapp.adapters;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mudasir.moviesapp.R;
import com.mudasir.moviesapp.listeners.MovieItemClickListener;
import com.mudasir.moviesapp.models.Movies;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder>{


    private Context mContext;
    private List<Movies> moviesList;
    MovieItemClickListener movieItemClickListener;

    public MoviesAdapter(Context mContext, List<Movies> moviesList,MovieItemClickListener listener) {
        this.mContext = mContext;
        this.moviesList = moviesList;
        movieItemClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(mContext).inflate(R.layout.item_movies,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Movies movies=moviesList.get(position);
        holder.title.setText(movies.getTitle());
        holder.title.setMovementMethod(new ScrollingMovementMethod());
        holder.category.setText(movies.getCategory());
        Glide.with(mContext).load(movies.getThumbnail()).into(holder.moviesImage);

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView moviesImage;
        private TextView title;
        private TextView category;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            moviesImage=itemView.findViewById(R.id.movies_img);
            title=itemView.findViewById(R.id.movie_title);
            category=itemView.findViewById(R.id.movie_rat);

            itemView.setOnClickListener(v ->
                    movieItemClickListener.onMovieClick(moviesList.get(getAdapterPosition()),moviesImage)
            );


        }
    }




}
