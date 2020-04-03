package com.mudasir.moviesapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mudasir.moviesapp.R;

public class LoginActivity extends AppCompatActivity {


    private EditText et_email;
    private EditText et_pass;
    private Button btn_login;
    private Button btnGotoReg;


    private FirebaseAuth mAuth;

    private Toolbar mToolBar;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mToolBar=findViewById(R.id.app_bar_login);
        mToolBar.setTitle("Login Activity");
        setSupportActionBar(mToolBar);

        mAuth=FirebaseAuth.getInstance();


        bindingView();


    }

    private void bindingView() {
        et_email=findViewById(R.id.et_login_email);
        et_pass=findViewById(R.id.et_login_pass);
        btn_login=findViewById(R.id.btn_login);
        btnGotoReg=findViewById(R.id.btn_gotoRegister);



        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



              String email=et_email.getText().toString();
              String password=et_pass.getText().toString();

              if (!email.isEmpty() && !password.isEmpty()){

                  mProgress = new ProgressDialog(LoginActivity.this);
                  mProgress.setTitle("Logging User...");
                  mProgress.setMessage("Please Wait We Are Logging In...");
                  mProgress.show();

                  signIn(email,password);
              }
              else{
                  Toast.makeText(LoginActivity.this, "You Must Fill All THe Fields", Toast.LENGTH_SHORT).show();
              }


            }
        });


        btnGotoReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);


            }
        });



    }


    private void signIn(String email,String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mProgress.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            mProgress.hide();
                            Toast.makeText(LoginActivity.this, "Login failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgress.hide();
                Toast.makeText(LoginActivity.this, "Login Failed due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
