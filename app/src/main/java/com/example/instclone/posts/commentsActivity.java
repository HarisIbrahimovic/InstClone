package com.example.instclone.posts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.instclone.R;
import com.example.instclone.adapters.MyAdapterComments;
import com.example.instclone.objects.comment;
import com.example.instclone.objects.user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class commentsActivity extends AppCompatActivity {
    private String postId;
    private EditText messContent;
    private ImageButton addCommentButton;
    private user currentUser;
    private DatabaseReference findUser;
    private DatabaseReference findPost;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private MyAdapterComments myAdapterComments;
    private ArrayList<comment> comments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        getIncomingIntent();
        configWidgets();
        findCurrentUser();
        getComments();

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });
    }

    private void getComments() {
        findPost.child("Comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    comment Comment = dataSnapshot.getValue(comment.class);
                    comments.add(Comment);

                }
                myAdapterComments.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.setAdapter(myAdapterComments);
    }

    private void addComment() {
        String commentText = messContent.getText().toString();
        if(TextUtils.isEmpty(commentText))return;
        HashMap<String, String> newComment = new HashMap<>();
        newComment.put("commentContent",commentText);
        newComment.put("imageUrl",currentUser.getImageUrl());
        newComment.put("userName",currentUser.getUserName());
        messContent.setText("");
        findPost.child("Comments").push().setValue(newComment);
    }

    private void findCurrentUser() {
        findUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    user User = dataSnapshot.getValue(user.class);
                    if(User.getUserId().equals(user.getUid())){
                        currentUser = User;
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configWidgets() {
        messContent = findViewById(R.id.addCommentContent);
        addCommentButton = findViewById(R.id.addCommentButton);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        findUser = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Users");
        findPost = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Posts").child(postId);
        comments = new ArrayList<>();
        myAdapterComments = new MyAdapterComments(getApplicationContext(),comments);
        recyclerView = findViewById(R.id.recViewComments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getIncomingIntent() {
        if(getIntent().hasExtra("postId"))postId=getIntent().getStringExtra("postId");
    }
}