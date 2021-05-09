package com.example.instclone.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.instclone.objects.user;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;


public class updateProfileActivity extends AppCompatActivity {
    private EditText email;
    private EditText userName;
    private EditText password;
    private Button submitButton;
    private ImageView profileImage;
    private DatabaseReference findUser;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String imageUrl;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        setUpWidgets();
        setUpStuff();
        //clickListeners
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUser();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosePicture();
            }
        });
    }

    private void setUpStuff() {
        findUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    user currentUser = snapshot1.getValue(user.class);
                    if(currentUser.getUserId().equals(user.getUid())) {
                        email.setText(currentUser.getUserEmail());
                        password.setText(currentUser.getUserPassword());
                        userName.setText(currentUser.getUserName());
                        Glide.with(getApplicationContext()).load(currentUser.getImageUrl()).into(profileImage);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void changeUser() {
        String newEmail = email.getText().toString();
        String newPassword = password.getText().toString();
        String newUserName = userName.getText().toString();
        if(checkIfCorrect(newEmail,newPassword,newUserName)==1)return;
        HashMap<String,String> changedUser = new HashMap<>();
        changedUser.put("userId",user.getUid());
        changedUser.put("userName",newUserName);
        changedUser.put("userEmail",newEmail);
        changedUser.put("userPassword",newPassword);
        changedUser.put("imageUrl",imageUrl);
        user.updateEmail(newEmail);
        user.updatePassword(newPassword);
        findUser.child(user.getUid()).setValue(changedUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Successfully updated",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int checkIfCorrect(String newEmail, String newPassword, String newUserName) {
        if(TextUtils.isEmpty(newEmail)||TextUtils.isEmpty(newPassword)||TextUtils.isEmpty(newUserName)){
            Toast.makeText(getApplicationContext(),"Fill in the fields.",Toast.LENGTH_SHORT).show();
            return 1;
        }
        if(newPassword.length()<8){
            Toast.makeText(getApplicationContext(),"Password too short.",Toast.LENGTH_SHORT).show();
            return 1;
        }
        return 0;
    }

    private void setUpWidgets() {
        email = findViewById(R.id.updateProfileEmail);
        password = findViewById(R.id.updateProfilePassword);
        userName = findViewById(R.id.updateProfileUsername);
        profileImage = findViewById(R.id.updateProfilePic);
        submitButton = findViewById(R.id.updateSubmitButton);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference =firebaseStorage.getReference();
        findUser = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Users");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
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
            profileImage.setImageURI(imageUri);
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