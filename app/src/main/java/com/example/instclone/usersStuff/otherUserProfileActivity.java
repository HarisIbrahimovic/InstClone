package com.example.instclone.usersStuff;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class otherUserProfileActivity extends AppCompatActivity {
    private ImageView profileImage;
    private TextView userName;
    private Button addFriend, messageUser;
    private String imageUrl;
    private String UserName;
    private String UserId;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);
        getIncomingIntent();
        configWidgets();
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("FriendReq").child(user.getUid()).setValue(user.getUid());
                Toast.makeText(getApplicationContext(),"Request sent.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getIncomingIntent() {
        if(getIntent().hasExtra("userId")&&getIntent().hasExtra("userName")&&getIntent().hasExtra("imageUrl")){
            imageUrl = getIntent().getStringExtra("imageUrl");
            UserName = getIntent().getStringExtra("userName");
            UserId = getIntent().getStringExtra("userId");
        }
    }

    private void configWidgets() {
        profileImage = findViewById(R.id.otherProfilePic);
        userName = findViewById(R.id.personUserName);
        addFriend = findViewById(R.id.addFriend);
        databaseReference = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Users").child(UserId);
        Glide.with(getApplicationContext()).load(imageUrl).into(profileImage);
        userName.setText(UserName);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }
}