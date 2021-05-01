package com.example.instclone.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instclone.R;
import com.example.instclone.objects.post;
import com.example.instclone.objects.user;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAdapterPosts extends RecyclerView.Adapter<MyAdapterPosts.MyViewHolder> {
    ArrayList<post> posts;
    Context context;
    DatabaseReference ref;

    public MyAdapterPosts(ArrayList<post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        post Post = posts.get(position);
        holder.description.setText(Post.getDescription());
        Glide.with(context).load(Post.getImageUrl()).into(holder.postImage);
        ref = FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Users").child(Post.getUserId());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RequestOptions options = new RequestOptions();
                options.centerCrop();
                user User = snapshot.getValue(user.class);
                Glide.with(context).load(User.getImageUrl()).apply(options).into(holder.ownerImage);
                holder.userName.setText(User.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage = itemView.findViewById(R.id.postImageItem);
        ImageView ownerImage = itemView.findViewById(R.id.postUserImage);
        TextView description = itemView.findViewById(R.id.postDescItem);
        TextView userName=  itemView.findViewById(R.id.postUserName);
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
