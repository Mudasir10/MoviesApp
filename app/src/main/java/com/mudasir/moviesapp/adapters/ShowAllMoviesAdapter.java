package com.mudasir.moviesapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mudasir.moviesapp.R;
import com.mudasir.moviesapp.listeners.MovieItemClickListener;
import com.mudasir.moviesapp.models.Movies;

import java.util.ArrayList;
import java.util.List;

public class ShowAllMoviesAdapter extends RecyclerView.Adapter<ShowAllMoviesAdapter.ShowAllViewHolder>  implements Filterable {


    private List<Movies> moviesList;
    private List<Movies> moviesListFull;
    private Context mContext;
    MovieItemClickListener movieItemClickListener;

    public ShowAllMoviesAdapter(List<Movies> moviesList, Context mContext ,MovieItemClickListener listener) {
        this.moviesList = moviesList;
        this.mContext = mContext;
        moviesListFull=new ArrayList<>(moviesList);
        movieItemClickListener = listener;
    }

    @NonNull
    @Override
    public ShowAllViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.single_item_movies_show_all,parent,false);

        return new ShowAllViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowAllViewHolder holder, int position) {

        Movies movies=moviesList.get(position);

        holder.tvMovietitle.setText(movies.getTitle());
        holder.tvMovieCategory.setText(movies.getCategory());

        Glide.with(mContext).load(movies.getThumbnail()).into(holder.ivMovieThumbnail);

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }


    @Override
    public Filter getFilter() {
        return exampleFilter;
    }


    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Movies> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(moviesListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Movies item : moviesListFull) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            moviesList.clear();
            moviesList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };




    public class ShowAllViewHolder extends RecyclerView.ViewHolder {

        ImageView ivMovieThumbnail;
        TextView tvMovietitle,tvMovieCategory;


        public ShowAllViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMovieThumbnail=itemView.findViewById(R.id.show_all_movies_img);
            tvMovietitle=itemView.findViewById(R.id.show_all_movie_title);
            tvMovieCategory=itemView.findViewById(R.id.show_all_movie_category);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    movieItemClickListener.onMovieClick(moviesList.get(getAdapterPosition()),ivMovieThumbnail);


                }
            });


        }
    }
}
