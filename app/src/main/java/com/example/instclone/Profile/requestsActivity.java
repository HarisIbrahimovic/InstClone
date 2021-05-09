package com.example.instclone.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.instclone.R;
import com.example.instclone.adapters.MyAdapterFriendRequests;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import com.example.instclone.objects.user;
import com.google.firebase.database.ValueEventListener;

public class requestsActivity extends AppCompatActivity {
    private FirebaseUser user;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private MyAdapterFriendRequests myAdapterFriendRequests;
    private ArrayList<user> users;
    private DatabaseReference findUser;
    private ArrayList<String> usersIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        configWidgets();
        findRequests();
        showRequests();
    }

    private void findRequests() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String UserId = dataSnapshot.getValue(String.class);
                    usersIds.add(UserId);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showRequests() {
        findUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    user User = dataSnapshot.getValue(user.class);
                    if(usersIds.contains(User.getUserId())){
                        users.add(User);
                    }
                }
                myAdapterFriendRequests.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        recyclerView.setAdapter(myAdapterFriendRequests);
    }

    private void configWidgets() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        users = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Users").child(user.getUid()).child("FriendReq");
        findUser = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Users");
        recyclerView = findViewById(R.id.reqRecView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapterFriendRequests = new MyAdapterFriendRequests(users,getApplicationContext());
        usersIds = new ArrayList<>();
    }
}