package com.example.instclone.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instclone.R;
import com.example.instclone.objects.user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class myProfileActivity extends AppCompatActivity {
    private ImageView profileImage;
    private TextView userName;
    private String userId;
    private Button updateProfile;
    private Button friendsButton;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private user currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        setUpWidgets();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    currentUser = snapshot1.getValue(user.class);
                    if(currentUser.getUserId().equals(userId)){
                        Glide.with(getApplicationContext()).load(currentUser.getImageUrl()).into(profileImage);
                        userName.setText(currentUser.getUserName());
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),updateProfileActivity.class);
                intent.putExtra("userId",currentUser.getUserId());
                intent.putExtra("userName",currentUser.getUserName());
                intent.putExtra("userEmail",currentUser.getUserEmail());
                intent.putExtra("imageUrl",currentUser.getImageUrl());
                intent.putExtra("userPassword",currentUser.getUserPassword());
                startActivity(intent);

            }
        });
    }

    private void setUpWidgets() {
        profileImage = findViewById(R.id.myProfilePic);
        userName = findViewById(R.id.myUsernam);
        updateProfile = findViewById(R.id.updateProfileButton);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Users");
    }
}