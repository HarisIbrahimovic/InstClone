package com.example.instclone.messaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instclone.R;
import com.example.instclone.adapters.MyAdapterMessages;
import com.example.instclone.objects.message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class messagingActivity extends AppCompatActivity {
    private TextView reciverName;
    private ImageView reciverImage;
    private EditText messageText;
    private ImageButton sendButton;
    private String reciverId;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private DatabaseReference checkMessages;
    private ArrayList<message> messages;
    private MyAdapterMessages myAdapterMessages;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        setUpWidgets();
        getIncomingIntent();
        getMessages();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
             }
        });
    }

    private void getMessages() {
        checkMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    message Message = dataSnapshot.getValue(message.class);
                    if(Message.getSenderId().equals(reciverId)&&Message.getReciverId().equals(user.getUid())
                            ||Message.getSenderId().equals(user.getUid())&&Message.getReciverId().equals(reciverId)){
                        messages.add(Message);
                    }
                }
                myAdapterMessages.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        recyclerView.setAdapter(myAdapterMessages);

    }

    private void sendMessage() {
        String messContent = messageText.getText().toString();
        if(TextUtils.isEmpty(messContent))return;
        databaseReference = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Messages");
        HashMap<String, String> newMessage = new HashMap<>();
        newMessage.put("reciverId",reciverId);
        newMessage.put("senderId",user.getUid());
        newMessage.put("content",messContent);
        databaseReference.push().setValue(newMessage);
        messageText.setText("");
    }

    private void getIncomingIntent() {
        if(getIntent().hasExtra("imageUrl")&&getIntent().hasExtra("reciverName")&&getIntent().hasExtra("reciverId")){
            reciverId = getIntent().getStringExtra("reciverId");
            reciverName.setText(getIntent().getStringExtra("reciverName"));
            Glide.with(getApplicationContext()).load(getIntent().getStringExtra("imageUrl")).into(reciverImage);
        }
    }

    private void setUpWidgets() {
        reciverName= findViewById(R.id.messReciverName);
        reciverImage = findViewById(R.id.messagingPic);
        messageText = findViewById(R.id.textSend);
        sendButton = findViewById(R.id.sendButton);
        recyclerView = findViewById(R.id.recyclerViewMess);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        checkMessages = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Messages");
        messages = new ArrayList<>();
        myAdapterMessages = new MyAdapterMessages(messages,getApplicationContext());
    }
}