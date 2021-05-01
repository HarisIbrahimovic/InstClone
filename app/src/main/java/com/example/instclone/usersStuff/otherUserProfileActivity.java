package com.example.instclone.usersStuff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instclone.R;
import com.example.instclone.adapters.MyAdapterPosts;
import com.example.instclone.objects.post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class otherUserProfileActivity extends AppCompatActivity {
    private ImageView profileImage;
    private TextView userName;
    private Button addFriend, messageUser;
    private String imageUrl;
    private String UserName;
    private String UserId;
    private DatabaseReference helpRef;
    private DatabaseReference databaseReference;
    private DatabaseReference postRef;
    private DatabaseReference findFriends;
    private DatabaseReference unfriend;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private RecyclerView recyclerView;
    private MyAdapterPosts myAdapterPosts;
    private ArrayList<post> posts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);
        getIncomingIntent();
        configWidgets();
        checkFriends();
        getPosts();

        //clickListeners
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addFriend.getText().equals("Unfriend")){
                    databaseReference.child("Friends").child(UserId).removeValue();
                    findFriends.child(user.getUid()).removeValue();
                    addFriend.setText("Add Friend");
                }else {
                    databaseReference.child("FriendReq").child(user.getUid()).setValue(user.getUid());
                    Toast.makeText(getApplicationContext(), "Request sent.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getPosts() {
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    post Post = snapshot1.getValue(post.class);
                    if(Post.getUserId().equals(UserId)){
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

    private void getIncomingIntent() {
        if(getIntent().hasExtra("userId")&&getIntent().hasExtra("userName")&&getIntent().hasExtra("imageUrl")){
            imageUrl = getIntent().getStringExtra("imageUrl");
            UserName = getIntent().getStringExtra("userName");
            UserId = getIntent().getStringExtra("userId");
        }
    }

    private void configWidgets() {
        //widgets
        profileImage = findViewById(R.id.otherProfilePic);
        userName = findViewById(R.id.personUserName);
        addFriend = findViewById(R.id.addFriend);
        //auth stuff
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        //dbRefs
        helpRef = FirebaseDatabase.getInstance().getReference("SocialNetwork");
        databaseReference = helpRef.child("Users").child(UserId);
        postRef = helpRef.child("Posts");
        findFriends = helpRef.child("Users").child(user.getUid()).child("Friends");
        unfriend = helpRef.child("Users").child(UserId).child("Friends");
        //setDetails
        Glide.with(getApplicationContext()).load(imageUrl).into(profileImage);
        userName.setText(UserName);
        //recViewStuff
        posts = new ArrayList<>();
        myAdapterPosts = new MyAdapterPosts(posts,getApplicationContext());
        recyclerView = findViewById(R.id.otherPersonPostsRec);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
    }

    private void checkFriends() {
        findFriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    String userId = snapshot1.getValue(String.class);
                    if(userId.equals(UserId)){
                        addFriend.setText("Unfriend");
                    }
                }
                myAdapterPosts.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}