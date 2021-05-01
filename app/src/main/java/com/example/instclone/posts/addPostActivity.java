package com.example.instclone.posts;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instclone.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class addPostActivity extends AppCompatActivity {
    private ImageView postImage;
    private EditText postDesc;
    private Button createButton;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private Uri imageUri;
    private String imageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        configWidgets();
         //clickListener
        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosePicture();
            }
        });
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPost();
            }
        });
        
    }

    private void createPost() {
        final String randomKey = UUID.randomUUID().toString();
        String description = postDesc.getText().toString();
        if(TextUtils.isEmpty(description))description= "No description.";
        HashMap<String, String> newPost = new HashMap<>();
        newPost.put("imageUrl", imageUrl);
        newPost.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        newPost.put("postId", randomKey);
        newPost.put("description",description);
        databaseReference.child(randomKey).setValue(newPost).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Post added",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    private void configWidgets() {
        postDesc = findViewById(R.id.addPostDesc);
        postImage = findViewById(R.id.addPostImage);
        createButton = findViewById(R.id.addPostButton);
        databaseReference = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Posts");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference =firebaseStorage.getReference();
    }

    private void chosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==1 &&resultCode ==RESULT_OK && data!=null &&data.getData()!=null){
            imageUri = data.getData();
            postImage.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        final String randomKey = UUID.randomUUID().toString();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading..");
        progressDialog.show();
        StorageReference ref = storageReference.child("images/"+randomKey);
        ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUrl=uri.toString();

                    }
                });
                Toast.makeText(getApplicationContext(),"Done upload..",Toast.LENGTH_SHORT).show();
            }
        });
    }
}