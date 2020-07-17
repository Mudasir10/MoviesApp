package com.mudasir.moviesapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mudasir.moviesapp.R;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;

    private Button btnLogin,btnNeedAccount;
    private EditText etEmail,etPassword;

   private ProgressDialog mProgress;
   private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
        mToolbar=findViewById(R.id.app_bar_login);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login Activity");

        btnLogin= findViewById(R.id.btn_login);
        btnNeedAccount=findViewById(R.id.btnNeedAccount);

        etEmail=findViewById(R.id.login_email);
        etPassword=findViewById(R.id.login_pass);


        btnLogin.setOnClickListener(v -> {
            //login
           String email =  etEmail.getText().toString();
            String password =  etPassword.getText().toString();

            if (!email.isEmpty() && !password.isEmpty()){

                mProgress = new ProgressDialog(LoginActivity.this);
                mProgress.setTitle("Logging In...");
                mProgress.setMessage("Please Wait We Are Creating an Account");
                mProgress.show();

                signInWithEmailAndPassword(email,password);
            }



        });


        btnNeedAccount.setOnClickListener(v -> {

            Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        });
    }

    private void signInWithEmailAndPassword(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            mProgress.dismiss();
                            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {

                            mProgress.hide();
                            Toast.makeText(LoginActivity.this, "Login Error", Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

}


