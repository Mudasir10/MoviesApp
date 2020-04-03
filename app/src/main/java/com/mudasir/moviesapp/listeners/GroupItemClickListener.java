package com.mudasir.moviesapp.listeners;

import android.widget.ImageView;

import com.mudasir.moviesapp.models.Group;
import com.mudasir.moviesapp.models.Movies;

public interface GroupItemClickListener {
    void onGroupDeleteClick(Group group);
    void onGroupInRequestCalled(Group group);
}
