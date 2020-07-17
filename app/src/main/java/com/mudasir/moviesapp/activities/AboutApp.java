package com.mudasir.moviesapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.mudasir.moviesapp.R;
import com.mudasir.moviesapp.adapters.MyExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AboutApp extends AppCompatActivity {

    ExpandableListView expandableListView;
    MyExpandableListAdapter listAdapter;

    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        mToolbar=findViewById(R.id.app_bar_about_app);
        mToolbar.setTitle("About App");
        mToolbar.setNavigationIcon(getDrawable(R.drawable.ic_arrow_left));
        mToolbar.setNavigationOnClickListener(v -> finish());


        expandableListView=findViewById(R.id.expandableListView);
        // preparing list data
        prepareListData();

        listAdapter = new MyExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expandableListView.setAdapter(listAdapter);

    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("What is Movies Room?");
        listDataHeader.add("How Users Can Create a Group?");
        listDataHeader.add("How Users Can Join the Group?");
        listDataHeader.add("Why To Register in Movies Room?");
        listDataHeader.add("Can User Change Their Account Details information After Creating an Account?");
        listDataHeader.add("What if User Wants to Watch Movie Individualy?");
        listDataHeader.add("Can User Delete an Account From Movies Room App?");
        listDataHeader.add("Can user Search a Particular Movie?");
        listDataHeader.add("Can User See Their Groups Created?");
        listDataHeader.add("While Watching Movie in Group Can User Chat with Members Who Joined?");
        listDataHeader.add("Is Movies Room App Secure? ");


        // Adding child data
        List<String> q0 = new ArrayList<String>();
        q0.add("Movies Room is Platform where Users Can Watch Movies With Their Friends or Family.");

        List<String> q1 = new ArrayList<String>();
        q1.add("Users Can Create Groups by going into Movie Details Page And their user Will Find " +
                "a Button named Watch Movie With Friends, Users will Click That Button and Create any name for his/her group" +
                ",after creating the group you will be redirected to Players Activity and Their YOu can Share the Group Code With Your Friends or Family Via WhattsApp," +
                "Messenger,default Messages App and through so Many Others.");

        List<String> q2 = new ArrayList<String>();
        q2.add("When User Open Movie Room App He Will find so many options at bottom," +
                "from their if he/she click on plus icon/join button he/she will find a Dialog where he/she can put the group code he/she has recieved from his/her " +
                "friends...");

        List<String> q3 = new ArrayList<String>();
        q3.add("Registeration is Very Important Because We Need to Manage each Users Group Data. so it was neccessay to do it," +
                " and Also Each Users Favourite Movies if he/she is inserting Some Movies into Fav List.");

        List<String> q4 = new ArrayList<String>();
        q4.add("Yes. Users Can Change Their User Name And Profile Photo, but not Email Address he/she has Given While Registration.");


        List<String> q5 = new ArrayList<String>();
        q5.add("Yes User Can Watch Movie Individualy if he/she Wants, to do that he Needs to go to Movie Details Page After that" +
                "Click The Circular Play button to Watch Movie individualy.");

        List<String> q6 = new ArrayList<String>();
        q6.add("Yes. After Registration in Movies Room App, If In Future user want to Delete an Account From Movies Room ,then user " +
                "need to go to settings and Then user will find an Option Called Delete Account," +
                "if If Clicks On Delete Account Option then He/She is Asked for Credentials i Mean ,Email and Password if he/she put right Information Then His/Her Account Will be Deleted Permanently from" +
                "Movies Room.");


        List<String> q7 = new ArrayList<String>();
        q7.add("Yes. User Can Search a Particular Movie By Clicking on More Button Bellow Each Category, " +
                " if You Click On More Button You Will Find Movies Related To That Category and Their You Will " +
                "find A Search.");

        List<String> q8 = new ArrayList<String>();
        q8.add("Yes Users Can see their Group. Each Every User Can See Their Own Groups." +
                "And its the Resposibilty of Group Creator to Destory the Group After Watching the Movie with Friends.");


        List<String> q9 = new ArrayList<String>();
        q9.add("Yes. User Can Chat With the Joined Users. even EveryOne Who Joined the Group can Chat into Group.");



        List<String> q10 = new ArrayList<String>();
        q10.add("Yes. Movies Room App is safe and Secure, your Security and Privacy is our Goal.");



        listDataChild.put(listDataHeader.get(0), q0); // Header, Child data
        listDataChild.put(listDataHeader.get(1), q1);
        listDataChild.put(listDataHeader.get(2), q2);
        listDataChild.put(listDataHeader.get(3), q3);
        listDataChild.put(listDataHeader.get(4), q4);
        listDataChild.put(listDataHeader.get(5), q5);
        listDataChild.put(listDataHeader.get(6), q6);
        listDataChild.put(listDataHeader.get(7), q7);
        listDataChild.put(listDataHeader.get(8), q8);
        listDataChild.put(listDataHeader.get(9), q9);
        listDataChild.put(listDataHeader.get(10), q10);


    }
}