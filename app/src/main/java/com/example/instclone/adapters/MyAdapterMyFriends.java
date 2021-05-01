package com.example.instclone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instclone.R;
import com.example.instclone.objects.user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyAdapterMyFriends extends RecyclerView.Adapter<MyAdapterMyFriends.MyViewHolder> {
    Context context;
    ArrayList<user> users;

    public MyAdapterMyFriends(Context context, ArrayList<user> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.myfriendsusers,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        user User = users.get(position);
        holder.userName.setText(User.getUserName());
        Glide.with(context).load(User.getImageUrl()).into(holder.profileImage);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth =FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Users").child(user.getUid()).child("Friends").child(User.getUserId());
                databaseReference.removeValue();
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName = itemView.findViewById(R.id.friendUsername);
        ImageView profileImage = itemView.findViewById(R.id.friendPicture);
        Button delete = itemView.findViewById(R.id.deleteFriend);
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
