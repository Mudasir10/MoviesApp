package com.mudasir.moviesapp.activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.provider.FirebaseInitProvider;
import com.mudasir.moviesapp.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText etUserName;

    private EditText etEmail;
    private EditText etCreatePass;
    private EditText etConfirmPass;

    private Button btnRegister;
    private Button btnAlreadyHaveAnAccount;

    private Toolbar mToolBar;

    private ProgressDialog mProgress;

    private DatabaseReference mDatabaseRef;



    private FirebaseUser mCurrentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mToolBar=findViewById(R.id.app_bar_register);
        mToolBar.setTitle("Register Activity");
        setSupportActionBar(mToolBar);


        mAuth = FirebaseAuth.getInstance();

        bindingView();





    }


    private void bindingView() {

        etUserName=findViewById(R.id.reg_user_name);
        etEmail=findViewById(R.id.reg_email);
        etCreatePass=findViewById(R.id.reg_create_pass);
        etConfirmPass=findViewById(R.id.reg_confirm_pass);
        btnRegister=findViewById(R.id.btn_register);
        btnAlreadyHaveAnAccount=findViewById(R.id.btn_reg_already_have_an_account);



        btnRegister.setOnClickListener(v -> {


         String user_name = etUserName.getText().toString();
            String email =  etEmail.getText().toString();
            String Createpassword = etCreatePass.getText().toString();
            String ConfirmPassword = etConfirmPass.getText().toString();

            if (!user_name.isEmpty() &&  !email.isEmpty() && !Createpassword.isEmpty() && !ConfirmPassword.isEmpty()){

                if (Createpassword.equals(ConfirmPassword)){

                    mProgress = new ProgressDialog(RegisterActivity.this);
                    mProgress.setTitle("Registering User...");
                    mProgress.setMessage("Please Wait We Are Creating an Account");
                    mProgress.show();

                    signUp(user_name,email,ConfirmPassword);

                }
                else{
                    Toast.makeText(RegisterActivity.this, "Password Does Not Match", Toast.LENGTH_SHORT).show();
                }


            }
            else{
                Toast.makeText(RegisterActivity.this, "You Must Fill All The Fields", Toast.LENGTH_SHORT).show();
            }





        });


        btnAlreadyHaveAnAccount.setOnClickListener(v -> {

            Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        });



    }

    private void signUp(final String userName, final String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        mCurrentuser = mAuth.getCurrentUser();
                        String uid = mCurrentuser.getUid();
                        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                        Map<String, Object> userUpdates = new HashMap<>();
                        userUpdates.put("UserName", userName);
                        userUpdates.put("Email", email);
                        userUpdates.put("Id", uid);
                        userUpdates.put("image", "default");
                        mDatabaseRef.updateChildren(userUpdates);

                        mProgress.dismiss();

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        // ...
                    }
                    else{
                        mProgress.hide();
                        Toast.makeText(RegisterActivity.this, "Registration failed.",
                                Toast.LENGTH_SHORT).show();
                    }

                });


    }


}
