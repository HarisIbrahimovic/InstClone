package com.example.instclone.signin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instclone.MainActivity;
import com.example.instclone.R;
import com.example.instclone.objects.user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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

public class newAccActivity extends AppCompatActivity {

    private EditText userName;
    private EditText userEmail;
    private EditText userPassword;
    private EditText cPassword;
    private EditText age;
    private ImageView profileImage;
    private Button signInButton;
    private TextView toLoginText;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String imageUrl;
    private FirebaseAuth auth;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_acc);
        configWidgets();


        //clickListeners
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
        toLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), loginActivity.class));
                finish();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosePicture();
            }
        });
    }


    private void createUser() {
        String UserName = userName.getText().toString();
        String UserEmail = userEmail.getText().toString();
        String UserPassword = userPassword.getText().toString();
        String CPassword = cPassword.getText().toString();
        String Age = age.getText().toString();
        if(inputTests(UserName,UserEmail,UserPassword,Age,CPassword,imageUrl)==1)return;
        auth.createUserWithEmailAndPassword(UserEmail,UserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                HashMap<String, Object> newUser = new HashMap<>();
                newUser.put("userName",UserName);
                newUser.put("userEmail",UserEmail);
                newUser.put("userPassword",UserPassword);
                newUser.put("userId",auth.getCurrentUser().getUid());
                newUser.put("imageUrl",imageUrl);
                databaseReference.child(auth.getCurrentUser().getUid()).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(),"Welcome",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                    }
                });
            }
        });
    }


    private int inputTests(String UserName, String UserEmail, String UserPassword, String Age,String CPassword,String imageUrl) {
        if(TextUtils.isEmpty(UserName)||TextUtils.isEmpty(UserEmail)||TextUtils.isEmpty(UserPassword)||TextUtils.isEmpty(CPassword)||TextUtils.isEmpty(Age)){
            Toast.makeText(getApplicationContext(), "Fill in all the fields.", Toast.LENGTH_SHORT).show();
            return 1;
        }
        if(TextUtils.isEmpty(imageUrl)){
            Toast.makeText(getApplicationContext(),"Select Picture.",Toast.LENGTH_SHORT).show();
            return 1;
        }
        if(!UserPassword.equals(CPassword)){
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return 1;
        }
        if(UserPassword.length()<8) {
            Toast.makeText(getApplicationContext(), "Password too short", Toast.LENGTH_SHORT).show();
            return 1;
        }
        return 0;
    }


    private void configWidgets() {
        userName = findViewById(R.id.newAccUserName);
        userEmail = findViewById(R.id.newAccEmail);
        userPassword = findViewById(R.id.newAccPassword);
        cPassword = findViewById(R.id.newAccCPassword);
        age = findViewById(R.id.newAccAge);
        signInButton = findViewById(R.id.signInButton);
        toLoginText = findViewById(R.id.textToLogin);
        profileImage = findViewById(R.id.imageSignUp);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference =firebaseStorage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Users");
        auth = FirebaseAuth.getInstance();
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