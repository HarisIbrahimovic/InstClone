package com.example.instclone.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.instclone.R;
import com.example.instclone.adapters.MyAdapterUsers;
import com.example.instclone.messaging.messagingActivity;
import com.example.instclone.objects.message;
import com.example.instclone.objects.user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class recentChatsActivity extends AppCompatActivity implements MyAdapterUsers.touchListener {
    private DatabaseReference checkMessages;
    private DatabaseReference findUser;
    private MyAdapterUsers myAdapterUsers;
    private ArrayList<user> users;
    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayList<String> usersId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_chats);
        setUpWidgets();
        addChats();
    }

    private void addChats() {
        findUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    user User = dataSnapshot.getValue(user.class);
                    checkMessages.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                if(usersId.contains(User.getUserId()))break;
                                if(User.getUserId().equals(user.getUid()))break;
                                message Message = snapshot1.getValue(message.class);
                                if(Message.getReciverId().equals(user.getUid())&&Message.getSenderId().equals(User.getUserId())||
                                        Message.getSenderId().equals(user.getUid())&&Message.getReciverId().equals(User.getUserId())
                                ){
                                    users.add(User);
                                    usersId.add(User.getUserId());
                                    break;
                                }
                            }
                            myAdapterUsers.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        recyclerView.setAdapter(myAdapterUsers);
    }

    private void setUpWidgets() {
        users = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        recyclerView = findViewById(R.id.recentChatRecView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapterUsers = new MyAdapterUsers(getApplicationContext(),users,this);
        checkMessages = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Messages");
        findUser = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Users");
        usersId= new ArrayList<>();
        }

    @Override
    public void onNoteClick(int position) {
        user User = users.get(position);
        Intent intent = new Intent(getApplicationContext(), messagingActivity.class);
        intent.putExtra("reciverId",User.getUserId());
        intent.putExtra("imageUrl",User.getImageUrl());
        intent.putExtra("reciverName",User.getUserName());
        startActivity(intent);
    }
}