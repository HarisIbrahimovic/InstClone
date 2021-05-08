package com.example.instclone.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.instclone.R;
import com.example.instclone.adapters.MyAdapterPosts;
import com.example.instclone.objects.post;
import com.example.instclone.posts.configPostActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class myPostsActivity extends AppCompatActivity implements MyAdapterPosts.touchListener{
    private RecyclerView recyclerView;
    private MyAdapterPosts myAdapterPosts;
    private ArrayList<post> posts;
    private DatabaseReference findPosts;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);
        setUpWidgets();
        showPosts();
    }

    private void showPosts() {
        findPosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    post Post = dataSnapshot.getValue(post.class);
                    if(Post.getUserId().equals(auth.getCurrentUser().getUid())){
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

    private void setUpWidgets() {
        recyclerView = findViewById(R.id.myPostsRecView);
        auth = FirebaseAuth.getInstance();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        posts= new ArrayList<>();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        findPosts = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Posts");
        myAdapterPosts = new MyAdapterPosts(posts,getApplicationContext(),this);
    }

    @Override
    public void onNoteClick(int position) {
        post Post = posts.get(position);
        Intent intent = new Intent(getApplicationContext(), configPostActivity.class);
        intent.putExtra("postId",Post.getPostId());
        intent.putExtra("postImageUrl",Post.getImageUrl());
        intent.putExtra("postDesc",Post.getDescription());
        startActivity(intent);

    }
}