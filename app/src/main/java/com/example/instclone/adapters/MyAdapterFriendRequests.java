package com.example.instclone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instclone.R;
import com.example.instclone.objects.user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MyAdapterFriendRequests extends RecyclerView.Adapter<MyAdapterFriendRequests.MyViewHolder>{
    private ArrayList<user> users;
    private Context context;
    private DatabaseReference databaseReference;
    private DatabaseReference otherUser;
    private FirebaseAuth auth;
    private FirebaseUser user;

    public MyAdapterFriendRequests(ArrayList<user> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.req_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        user User = users.get(position);
        Glide.with(context).load(User.getImageUrl()).into(holder.profileImage);
        holder.userName.setText(User.getUserName());
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth = FirebaseAuth.getInstance();
                user = auth.getCurrentUser();
                databaseReference = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Users").child(user.getUid()).child("FriendReq").child(User.getUserId());
                databaseReference.removeValue();
                otherUser = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Users").child(User.getUserId()).child("Friends").child(user.getUid());
                otherUser.setValue(user.getUid());
                databaseReference = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Users").child(user.getUid()).child("Friends");
                databaseReference.child(User.getUserId()).setValue(User.getUserId());
                Toast.makeText(context,"Request Accepted.",Toast.LENGTH_SHORT).show();
            }
        });
        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth = FirebaseAuth.getInstance();
                user = auth.getCurrentUser();
                databaseReference = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Users").child(user.getUid()).child("FriendReq").child(User.getUserId());
                databaseReference.removeValue();
                Toast.makeText(context,"Request declined.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage = itemView.findViewById(R.id.reqUserPic);
        TextView userName = itemView.findViewById(R.id.reqUserName);
        Button accept = itemView.findViewById(R.id.acceptButton);
        Button decline = itemView.findViewById(R.id.declineButton);
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
