package com.example.instclone.usersStuff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.instclone.R;
import com.example.instclone.adapters.MyAdapterUsers;
import com.example.instclone.objects.user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class findUserActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private MyAdapterUsers myAdapterUsers;
    private ArrayList<user> users;
    private EditText searchBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        configWidgets();
    }

    private void configWidgets() {
        recyclerView= findViewById(R.id.userRecView);
        searchBar = findViewById(R.id.findUserSearchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());

            }
        });
        auth = FirebaseAuth.getInstance();
        users= new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Users");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapterUsers = new MyAdapterUsers(getApplicationContext(),users);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    user User = snapshot1.getValue(user.class);
                    if(!User.getUserId().equals(auth.getCurrentUser().getUid())){
                        users.add(User);
                    }
                }
                myAdapterUsers.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.setAdapter(myAdapterUsers);
    }

    private void filter(String text) {
        ArrayList<user> filteredList = new ArrayList<>();
        for(user User: users){
            if(User.getUserName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(User);
            }
        }
        myAdapterUsers.filterList(filteredList);
    }
}