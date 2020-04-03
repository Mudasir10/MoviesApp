package com.mudasir.moviesapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mudasir.moviesapp.R;
import com.mudasir.moviesapp.adapters.GroupsAdapter;
import com.mudasir.moviesapp.listeners.GroupItemClickListener;
import com.mudasir.moviesapp.models.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupsActivity extends AppCompatActivity  implements GroupItemClickListener {


    Toolbar mToolbar;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private FirebaseUser mCurrentUser;
    DatabaseReference mDatabase;

    private String uid="";

    private List<Group> groupList;

    private long count;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_groups);

            init();

    }

    private void init() {
        mToolbar=findViewById(R.id.app_bar_groups);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("My Groups");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        groupList=new ArrayList<>();

        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();
        uid=mCurrentUser.getUid();


        mDatabaseRef= FirebaseDatabase.getInstance().getReference("MyGroups").child(uid);
        mDatabase= FirebaseDatabase.getInstance().getReference("groups");


        recyclerView=findViewById(R.id.rv_my_groups);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);


        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                groupList.clear();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Group group= snapshot.getValue(Group.class);

                    groupList.add(group);
                }

                count= dataSnapshot.getChildrenCount();

                getSupportActionBar().setSubtitle(count+" Groups Created");

                GroupsAdapter adapter=new GroupsAdapter(groupList,GroupsActivity.this,uid,GroupsActivity.this);
                recyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onGroupDeleteClick(Group group) {


        AlertDialog.Builder alert=new AlertDialog.Builder(GroupsActivity.this)
                .setTitle("Confirmation Message")
                .setIcon(R.drawable.ic_delete)
                .setMessage("Are You Sure You Want to Delete?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String code=group.getGroupCode();

                        mDatabaseRef.child(code).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                mDatabase.child(code).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {


                                        Toast.makeText(GroupsActivity.this, "Done", Toast.LENGTH_SHORT).show();


                                    }
                                });

                            }
                        });


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();

                    }
                });
        alert.create();
        alert.show();



    }

    @Override
    public void onGroupInRequestCalled(Group group) {



        progressDialog=new ProgressDialog(GroupsActivity.this);
        progressDialog.setTitle("Getting Into the Group");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent sendTOWatchMoviesInGroupActivity=new Intent(GroupsActivity.this,
                        WatchMovieInGroup.class);
                sendTOWatchMoviesInGroupActivity.putExtra("group_code",group.getGroupCode());
                startActivity(sendTOWatchMoviesInGroupActivity);
                progressDialog.dismiss();

            }
        },3000);






    }
}
