package com.mudasir.moviesapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mudasir.moviesapp.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {


    Toolbar mToolbar;


    private static int RESULT_LOAD_IMAGE = 1;
    CircleImageView profileImage;

    String image_uri,user_name,email;

    TextView tvEmail,tvUserName;
    Button btnSaveChanges;

     DatabaseReference mDataBaseRef;
    private StorageReference mStorageRef;
     FirebaseAuth mAuth;
     FirebaseUser mCurrentUser;
     String uid;

     String newUserName;
    Uri croppedImageUri;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mToolbar=findViewById(R.id.app_bar_profile);
        mToolbar.setTitle("Profile Activity");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        mDataBaseRef= FirebaseDatabase.getInstance().getReference("Users");
        mStorageRef = FirebaseStorage.getInstance().getReference("profileImages");
        btnSaveChanges=findViewById(R.id.btnSaveChanges);


        mAuth=FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser()!=null){
            mCurrentUser=mAuth.getCurrentUser();
            uid= mCurrentUser.getUid();
        }


        mToolbar.setNavigationOnClickListener(v -> finish());


         profileImage =findViewById(R.id.profile_img);
        tvEmail=findViewById(R.id.profile_email);
        tvUserName=findViewById(R.id.profile_userName);

        // saving the Changes
        btnSaveChanges.setOnClickListener(v -> {

            if (isConnectedToInternet()){


                try {

                    if (!tvUserName.getText().toString().isEmpty() && croppedImageUri!=null){

                        progressDialog=new ProgressDialog(this);
                        progressDialog.setTitle("saving Changes...");
                        progressDialog.setMessage("Please Wait until It Finishes");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        final String fileNameAndExtension= uid + "." + getFileExtension(croppedImageUri);
                        StorageReference fileReference = mStorageRef.child(uid).child(fileNameAndExtension);
                        fileReference.putFile(croppedImageUri).addOnSuccessListener(taskSnapshot -> {

                            Task<Uri> profile_url= taskSnapshot.getMetadata().getReference().getDownloadUrl();

                            profile_url.addOnSuccessListener(uri -> {

                                String url = uri.toString();
                                mDataBaseRef.child(uid).child("image").setValue(url);
                                mDataBaseRef.child(uid).child("UserName").setValue(tvUserName.getText().toString());
                                Toast.makeText(this, "Saved Changes", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                            });

                        }).addOnFailureListener(e -> {

                            progressDialog.hide();
                            Toast.makeText(ProfileActivity.this, "Failed to Make Changes", Toast.LENGTH_SHORT).show();
                        });



                    }
                    else{
                        Toast.makeText(this, "Already Saved", Toast.LENGTH_SHORT).show();
                    }





                }
                catch (Exception ex){
                    ex.printStackTrace();
                }





            }
            else{
                Toast.makeText(this, "Cannot Make Changes Without Internet", Toast.LENGTH_SHORT).show();
            }

        });

        findViewById(R.id.btn_pick_image).setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*") ;
            startActivityForResult(intent, RESULT_LOAD_IMAGE);

        });

        tvUserName.setOnClickListener(v -> {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);
            alertDialog.setTitle("Change User Name");
            alertDialog.setIcon(R.drawable.change);
            final EditText input = new EditText(ProfileActivity.this);
            input.setHint("type a New Username");
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            input.setLayoutParams(lp);
            alertDialog.setView(input);
            alertDialog.setPositiveButton("Save", (dialog, which) -> {
             newUserName= input.getText().toString();

              if (!newUserName.isEmpty()){

                      dialog.dismiss();
                      tvUserName.setText(newUserName);
                      mDataBaseRef.child(uid).child("UserName").setValue(newUserName);


              }
              else{
                  Toast.makeText(this, "Please Enter a User Name", Toast.LENGTH_SHORT).show();
              }

            });
            alertDialog.setNegativeButton("cancel", (dialog, which) -> {

                dialog.cancel();
                Toast.makeText(this, "canceled", Toast.LENGTH_SHORT).show();
            });

            alertDialog.show();




        });



        if (getIntent() != null){

            email=getIntent().getExtras().get("email").toString();
            user_name= getIntent().getExtras().get("user_name").toString();
            image_uri= getIntent().getExtras().get("image").toString();

            tvUserName.setText(user_name);
            tvEmail.setText(email);

            if (image_uri.equals("default")){
                Glide.with(this).load(R.drawable.man).into(profileImage);
            }
            else{
                Glide.with(this).load(image_uri).into(profileImage);
            }
        }


    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data!=null) {

            launchCropper(data.getData());

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

               croppedImageUri= result.getUri();
               setImage(croppedImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }

    private void setImage(Uri uri) {
        profileImage.setImageURI(uri);
    }

    private void launchCropper(Uri data) {

        CropImage.activity(data)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.RECTANGLE) // default is rectangle
                .start(this);
    }



}
