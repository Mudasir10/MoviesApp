package com.mudasir.moviesapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mudasir.moviesapp.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {


    ProgressDialog mProgress;
    ProgressDialog mProgressDialog;
    private ListView settingList;
    private String[] list = {" Delete Account", "About Team", "About App", "Logout"};
    private int images[] = {R.drawable.manager, R.drawable.about_team, R.drawable.question, R.drawable.backpacker};

    private List<String> myGroupsInGeneral;

    Toolbar mToolbar;

    CircleImageView imageView;
    TextView UserName, UserEmail;

    FirebaseAuth mAuth;
    DatabaseReference mDatabaseRef;
    FirebaseUser mCurentUser;

    DatabaseReference mDatabaseMyGroups;
    DatabaseReference mDatabasePublicGroups;
    StorageReference mStorageRefProfileImage;



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

        myGroupsInGeneral=new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mDatabaseMyGroups=FirebaseDatabase.getInstance().getReference("MyGroups");
        mDatabasePublicGroups=FirebaseDatabase.getInstance().getReference("groups");
        mStorageRefProfileImage= FirebaseStorage.getInstance().getReference("profileImages");


        mToolbar.setNavigationOnClickListener(v -> finish());

        settingList = findViewById(R.id.settingListView);
        SettingAdapter SettingAdapter = new SettingAdapter();
        settingList.setAdapter(SettingAdapter);

        mCurentUser = mAuth.getCurrentUser();
        uid = mCurentUser.getUid();

        mDatabaseMyGroups.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        myGroupsInGeneral.add(snapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        findViewById(R.id.linearBtn).setOnClickListener(v -> {

            if (isConnectedToInternet()){
                callProfileActivity();
            }
            else{
                Toast.makeText(SettingsActivity.this, "Connect to Internet To See Profile Information", Toast.LENGTH_SHORT).show();
            }



        });



        settingList.setOnItemClickListener((AdapterView.OnItemClickListener) (parent, view, position, id) -> {
            switch (position) {

                case 0:
                    final View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.verification_dialog, null);
                    AlertDialog.Builder builder=new AlertDialog.Builder(SettingsActivity.this);
                    builder.setTitle("Verification/Reauthentication Message");
                    builder.setMessage("First Verify That its you By Providing your Movies Room Email and Password");
                    builder.setIcon(R.drawable.verify);
                    builder.create();
                    builder.setCancelable(false);

                    final EditText etmail = (EditText) view1.findViewById(R.id.et_verification_email);
                    final EditText etpassword = (EditText) view1.findViewById(R.id.et_verification_password);

                    builder.setPositiveButton("Verify", (dialog, which) -> {

                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                      String email=  etmail.getText().toString();
                      String passsword= etpassword.getText().toString();

                      if (!email.isEmpty() && !passsword.isEmpty()){
                          funReauthenticateAndDelete(email,passsword,user);
                      }else {
                          Toast.makeText(SettingsActivity.this, "Please Fill the Proper Info", Toast.LENGTH_SHORT).show();
                          dialog.cancel();
                      }

                    });
                    builder.setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    });
                    builder.setView(view1);
                    builder.show();

                    break;
                case 1:
                    startActivity(new Intent(SettingsActivity.this,AboutTeam.class));
                    break;
                case 2:
                    startActivity(new Intent(SettingsActivity.this,AboutApp.class));
                    break;
                case 3:
                    MethodLogOutUser();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + position);
            }
        });

    }


    private String getfileExtension(Uri uri)
    {
        String extension;
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension= mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }

    private void funReauthenticateAndDelete(String email, String passsword, FirebaseUser user) {

        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setTitle("Deleting Account");
        mProgressDialog.setMessage("Please Wait Until it Finishes...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIcon(R.drawable.delete);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();

        mDatabaseRef.child(uid).removeValue();
        if (myGroupsInGeneral.size()>=0){
            mDatabaseMyGroups.child(uid).removeValue();
        }
        if (!imageView.getDrawable().equals(R.drawable.man)){
            mStorageRefProfileImage.child(uid).child(uid+"."+getfileExtension(Uri.parse(image_uri))).delete();
        }
        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, passsword);
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(task ->
                        user.delete().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                if (myGroupsInGeneral.size()>=0){
                                    for (int i = 0; i < myGroupsInGeneral.size(); i++) {
                                        mDatabasePublicGroups.child(myGroupsInGeneral.get(i)).removeValue();
                                    }
                                }
                                Intent intent=new Intent(SettingsActivity.this,RegisterActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                                mProgressDialog.dismiss();
                              // Log.d("Settings Activity", "User account deleted.");
                            }

                        }));

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

    private void MethodLogOutUser() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setIcon(R.drawable.confirm);
        builder.setMessage("Are You Sure You Want to Log Out");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {

            mProgress = new ProgressDialog(SettingsActivity.this);
            mProgress.setTitle("Logging Off...");
            mProgress.setMessage("Please Wait");
            mProgress.show();

            mAuth.signOut();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            mProgress.dismiss();

        });
        builder.setNegativeButton("cancel", (dialog, which) -> dialog.dismiss());
        builder.create();
        builder.show();
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

                email = String.valueOf(dataSnapshot.child("Email").getValue());
                user_name = String.valueOf(dataSnapshot.child("UserName").getValue());
                image_uri = String.valueOf(dataSnapshot.child("image").getValue());


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
