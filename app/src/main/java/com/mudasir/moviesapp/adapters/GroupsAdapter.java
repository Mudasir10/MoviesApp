package com.mudasir.moviesapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mudasir.moviesapp.R;
import com.mudasir.moviesapp.activities.MoviesDetailsActivity;
import com.mudasir.moviesapp.activities.WatchMovieInGroup;
import com.mudasir.moviesapp.listeners.GroupItemClickListener;
import com.mudasir.moviesapp.models.Group;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder> {


    private List<Group> groupList;
    private Context mContext;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabase;
    GroupItemClickListener groupItemClickListener;

    private String Uid;

    public GroupsAdapter(List<Group> groupList, Context mContext,String uid,GroupItemClickListener listener) {
        this.groupList = groupList;
        this.mContext = mContext;
        this.Uid=uid;
        this.groupItemClickListener=listener;


    }

    @NonNull
    @Override
    public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view= LayoutInflater.from(mContext).inflate(R.layout.single_item_group,parent,false);
        return new GroupsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsViewHolder holder, int position) {

        Group group=groupList.get(position);

        holder.tvGroupName.setText("Group Name : "+group.getGroupName());
        holder.tvGroupCode.setText("Group Code : "+group.getGroupCode());


    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }



    public class GroupsViewHolder extends RecyclerView.ViewHolder {


        TextView tvGroupName,tvGroupCode;
        FloatingActionButton btnDeleteGroup;
        CircleImageView circleImageView;

        Button btnGetINTOGroup;


        public GroupsViewHolder(@NonNull View itemView) {
            super(itemView);

            tvGroupName=itemView.findViewById(R.id.tvGroup_name);
            tvGroupCode=itemView.findViewById(R.id.tv_group_code);
            btnDeleteGroup=itemView.findViewById(R.id.btnDeleteGroup);
            circleImageView=itemView.findViewById(R.id.group_image);
            btnGetINTOGroup=itemView.findViewById(R.id.btnGetIN);


            btnGetINTOGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    groupItemClickListener.onGroupInRequestCalled(groupList.get(getAdapterPosition()));


                }
            });

            btnDeleteGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    groupItemClickListener.onGroupDeleteClick(groupList.get(getAdapterPosition()));



                }
            });

        }
    }



}
