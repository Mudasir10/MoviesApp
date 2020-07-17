package com.mudasir.moviesapp.classes;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mudasir.moviesapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.viewHolder>{

    private List<String> mNameList;
    private List<String> mDesignationList;
    private int[] mImageList;
    private List<String> mRollList;
    private Typeface typeface;


    public ChildAdapter(List<String> mNameList, List<String> mDesignationList, int[] mImageList, List<String> mRollList, Typeface mFace) {
        this.mNameList = mNameList;
        this.mDesignationList = mDesignationList;
        this.mImageList = mImageList;
        this.mRollList = mRollList;
        this.typeface=mFace;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_item, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {


        holder.nameTextView.setTypeface(typeface);


        holder.nameTextView.setText(mNameList.get(position));
        holder.designationTextView.setText(mDesignationList.get(position));
        holder.rollNumberTextView.setText(mRollList.get(position));

        holder.circularImageView.setImageResource(mImageList[position]);

    }

    @Override
    public int getItemCount() {
        return mNameList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView, designationTextView,rollNumberTextView;
        private CircleImageView circularImageView;


        public viewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.nameTextView);
            designationTextView = itemView.findViewById(R.id.designationTextView);
            rollNumberTextView = itemView.findViewById(R.id.rollNumberTextView);
            circularImageView = itemView.findViewById(R.id.profileImage);


        }
    }

}
