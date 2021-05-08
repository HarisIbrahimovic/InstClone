package com.example.instclone.posts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instclone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class configPostActivity extends AppCompatActivity {
    private String PostId, PostImageUrl, PostDesc;
    private DatabaseReference databaseReference;
    private Button deleteButton, updateButton;
    private EditText postDescription;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_post);
        getIncomingIntent();
        setUpStuff();

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(PostId).removeValue();
                Toast.makeText(getApplicationContext(),"Post removed.",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newDesc = postDescription.getText().toString().trim();
                if(TextUtils.isEmpty(newDesc)){
                    databaseReference.child(PostId).child("description").setValue("No description");
                    Toast.makeText(getApplicationContext(),"Succesfully updated.",Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    databaseReference.child(PostId).child("description").setValue(newDesc);
                    Toast.makeText(getApplicationContext(),"Succesfully updated.",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void setUpStuff() {
        databaseReference = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Posts");
        deleteButton = findViewById(R.id.deletePostButton);
        updateButton = findViewById(R.id.updatePostButton);
        postDescription = findViewById(R.id.postDescConfig);
        postDescription.setText(PostDesc);
        imageView = findViewById(R.id.postImageConfig);
        Glide.with(getApplicationContext()).load(PostImageUrl).into(imageView);
    }

    private void getIncomingIntent() {
        PostId = getIntent().getStringExtra("postId");
        PostImageUrl = getIntent().getStringExtra("postImageUrl");
        PostDesc = getIntent().getStringExtra("postDesc");
    }
}