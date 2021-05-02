package com.example.instclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.instclone.Profile.myProfileActivity;
import com.example.instclone.Profile.recentChatsActivity;
import com.example.instclone.adapters.MyAdapterPosts;
import com.example.instclone.objects.post;
import com.example.instclone.posts.addPostActivity;
import com.example.instclone.signin.loginActivity;
import com.example.instclone.signin.newAccActivity;
import com.example.instclone.usersStuff.findUserActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FloatingActionButton searchButton, myProfileButton, signOutButton, addPostButton, messagesButton;
    private RecyclerView recyclerView;
    private ArrayList<post> posts;
    private MyAdapterPosts myAdapterPosts;
    private DatabaseReference databaseReference;
    private DatabaseReference findFriends;
    private ArrayList<String> friendsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkUser();
        setUpWidgets();
        checkFriends();
        showPosts();
        //clickListeners
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
        messagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), recentChatsActivity.class));
            }
        });
    }

    private void checkFriends() {
        friendsId.add(auth.getCurrentUser().getUid());
        findFriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    String userId = snapshot1.getValue(String.class);
                    friendsId.add(userId);
                }
                myAdapterPosts.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void showPosts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    post Post = snapshot1.getValue(post.class);
                    if(friendsId.contains(Post.getUserId())){
                        posts.add(Post);
                    }
                }
                myAdapterPosts.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        recyclerView.setAdapter(myAdapterPosts);
    }

    private void signOut() {
        auth.signOut();
        startActivity(new Intent(getApplicationContext(),loginActivity.class));
        finish();
    }

    private void setUpWidgets() {
        //widgets
        searchButton = findViewById(R.id.searchButton);
        signOutButton = findViewById(R.id.logoutButton);
        messagesButton = findViewById(R.id.messagesButton);
        addPostButton = findViewById(R.id.addPostButton);
        myProfileButton = findViewById(R.id.profileButton);
        recyclerView = findViewById(R.id.postsRecycleView);
        messagesButton = findViewById(R.id.messagesButton);
        //firebasestuff
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Posts");
        findFriends = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Users").child(auth.getCurrentUser().getUid()).child("Friends");
        //recViewStuff
        posts = new ArrayList<>();
        friendsId = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        myAdapterPosts = new MyAdapterPosts(posts,getApplicationContext());
    }

    private void checkUser() {
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null){
            startActivity(new Intent(getApplicationContext(), loginActivity.class));
            finish();
        }
    }
}