package com.example.instclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.instclone.Profile.myProfileActivity;
import com.example.instclone.posts.addPostActivity;
import com.example.instclone.signin.loginActivity;
import com.example.instclone.signin.newAccActivity;
import com.example.instclone.usersStuff.findUserActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FloatingActionButton searchButton, myProfileButton, signOutButton, addPostButton, messagesButton;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpWidgets();
        checkUser();

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        myProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), myProfileActivity.class));
            }
        });
        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), addPostActivity.class));
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), findUserActivity.class));
            }
        });
    }


    private void signOut() {
        auth.signOut();
        startActivity(new Intent(getApplicationContext(),loginActivity.class));
        finish();
    }

    private void setUpWidgets() {
        searchButton = findViewById(R.id.searchButton);
        signOutButton = findViewById(R.id.logoutButton);
        messagesButton = findViewById(R.id.messagesButton);
        addPostButton = findViewById(R.id.addPostButton);
        myProfileButton = findViewById(R.id.profileButton);
        recyclerView = findViewById(R.id.postsRecycleView);
        auth = FirebaseAuth.getInstance();
    }

    private void checkUser() {

        if(auth.getCurrentUser()==null){
            startActivity(new Intent(getApplicationContext(), loginActivity.class));
            finish();
        }
    }
}