package com.mudasir.moviesapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mudasir.moviesapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {


    private ListView settingList;
    private String[] list = {"Account", "Groups", "About Team", "About App", "Logout"};
    private int images[] = {R.drawable.manager, R.drawable.group, R.drawable.about_team, R.drawable.question, R.drawable.backpacker};


    Toolbar mToolbar;

    CircleImageView imageView;
    TextView UserName, UserEmail;

    FirebaseAuth mAuth;
    DatabaseReference mDatabaseRef;
    FirebaseUser mCurentUser;

    String uid, user_name, email, image_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = findViewById(R.id.app_bar_settings);
        mToolbar.setTitle("Settings");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        imageView = findViewById(R.id.userImage);
        UserName = findViewById(R.id.userName);
        UserEmail = findViewById(R.id.userEmail);

        mAuth = FirebaseAuth.getInstance();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");


        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        settingList = findViewById(R.id.settingListView);
        SettingAdapter SettingAdapter = new SettingAdapter();
        settingList.setAdapter(SettingAdapter);

        findViewById(R.id.linearBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConnectedToInternet()){
                    callProfileActivity();
                }
                else{
                    Toast.makeText(SettingsActivity.this, "Connect to Internet To See Profile Information", Toast.LENGTH_SHORT).show();
                }



            }
        });



        settingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {

                    case 0:
                        break;

                    case 1:
                        break;

                    case 2:
                        break;

                    case 3:
                        break;


                    case 4:
                        break;

                    case 5:
                        break;

                }
            }
        });

    }


    private Boolean isConnectedToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        else
            return false;
    }




    private void callProfileActivity() {


        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user_name",user_name);
        intent.putExtra("email",email);
        intent.putExtra("image",image_uri);
        startActivity(intent);


    }


    @Override
    protected void onStart() {
        super.onStart();

        mCurentUser = mAuth.getCurrentUser();
        uid = mCurentUser.getUid();


        mDatabaseRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                email = dataSnapshot.child("Email").getValue().toString();
                user_name = dataSnapshot.child("UserName").getValue().toString();
                image_uri = dataSnapshot.child("image").getValue().toString();


                UserName.setText(user_name);
                UserEmail.setText(email);

                if (image_uri.equals("default")) {
                    Glide.with(SettingsActivity.this).load(R.drawable.man).into(imageView);

                } else {
                    Glide.with(SettingsActivity.this).load(image_uri).into(imageView);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public class SettingAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return list.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = getLayoutInflater().inflate(R.layout.settings_item, parent, false);
            TextView listTitle = v.findViewById(R.id.listItemDes);
            /* ImageView listItemImage=v.findViewById(R.id.listItemImage);*/
            Drawable img = getApplicationContext().getResources().getDrawable(images[position]);

            img.setBounds(0, 0, 60, 60);
            listTitle.setCompoundDrawables(img, null, null, null);

            listTitle.setText(list[position]);
            return v;
        }


    }


}
