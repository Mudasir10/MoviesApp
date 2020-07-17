package com.mudasir.moviesapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import com.mudasir.moviesapp.R;
import com.mudasir.moviesapp.classes.ChildAdapter;

import java.util.ArrayList;
import java.util.List;

public class AboutTeam extends AppCompatActivity {

    private List<String> mNameList=new ArrayList<>();
    private List<String> mDesignationList=new ArrayList<>();
    private List<String> mRollList=new ArrayList<>();
    private int mImageList []={R.drawable.person,R.drawable.girl,R.drawable.person,R.drawable.girl,R.drawable.girl,R.drawable.girl};

    private RecyclerView aboutTeamRecyclerView;
    private ChildAdapter childAdapter;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_team);

        mToolbar=findViewById(R.id.app_bar_about_team);
        mToolbar.setTitle("About Team");
        mToolbar.setNavigationIcon(getDrawable(R.drawable.ic_arrow_left));
        mToolbar.setNavigationOnClickListener(v -> finish());


        mNameList.add("Mudasir Hussain ");
        mNameList.add("Almas Ansari");
        mNameList.add("Ahsan Junejo ");
        mNameList.add("Shabana Jamali");
        mNameList.add("Irum Bhatti");
        mNameList.add("Mahnoor Bhambharo");
        mRollList.add("17IT10");
        mRollList.add("17IT25");
        mRollList.add("17IT55");
        mRollList.add("17IT09");
        mRollList.add("17IT01");
        mRollList.add("17IT60");
        mDesignationList.add("Group Leader");
        mDesignationList.add("Group Member");
        mDesignationList.add("Group Member");
        mDesignationList.add("Group Member");
        mDesignationList.add("Group Member");
        mDesignationList.add("Group Member");


        Typeface MBold= Typeface.createFromAsset(getAssets(),"fonts/MmBold.ttf");
        Typeface MRegular=Typeface.createFromAsset(getAssets(),"fonts/MmRegular.ttf");

        aboutTeamRecyclerView = findViewById(R.id.aboutTeamRecyclerView);
        aboutTeamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        aboutTeamRecyclerView.hasFixedSize();
        childAdapter = new ChildAdapter(mNameList, mDesignationList, mImageList, mRollList,MBold);
        aboutTeamRecyclerView.setAdapter(childAdapter);

    }
}