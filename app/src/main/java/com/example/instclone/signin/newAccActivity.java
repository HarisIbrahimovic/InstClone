package com.example.instclone.signin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instclone.MainActivity;
import com.example.instclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class newAccActivity extends AppCompatActivity {
    private EditText userName;
    private EditText userEmail;
    private EditText userPassword;
    private EditText cPassword;
    private EditText age;
    private Button signInButton;
    private TextView toLoginText;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_acc);
        configWidgets();
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }
    private void createUser() {
        String UserName = userName.getText().toString();
        String UserEmail = userEmail.getText().toString();
        String UserPassword = userPassword.getText().toString();
        String CPassword = cPassword.getText().toString();
        String Age = age.getText().toString();
        if(TextUtils.isEmpty(UserName)||TextUtils.isEmpty(UserEmail)||TextUtils.isEmpty(UserPassword)||TextUtils.isEmpty(CPassword)||TextUtils.isEmpty(Age)){
            Toast.makeText(getApplicationContext(), "Fill in all the fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!UserPassword.equals(CPassword)){
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("SocialNetworkA").child("Users");
        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(UserEmail,UserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                HashMap<String, String> newUser = new HashMap<>();
                newUser.put("username",UserName);
                newUser.put("email",UserEmail);
                newUser.put("password",UserPassword);
                newUser.put("id",auth.getCurrentUser().getUid());
                databaseReference.setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void configWidgets() {
        userName = findViewById(R.id.newAccUserName);
        userEmail = findViewById(R.id.newAccEmail);
        userPassword = findViewById(R.id.newAccPassword);
        cPassword = findViewById(R.id.newAccCPassword);
        age = findViewById(R.id.newAccAge);
        signInButton = findViewById(R.id.signInButton);
        toLoginText = findViewById(R.id.textToLogin);
    }
}