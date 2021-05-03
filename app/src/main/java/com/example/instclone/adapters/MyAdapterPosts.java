package com.example.instclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instclone.R;
import com.example.instclone.objects.post;
import com.example.instclone.objects.user;
import com.example.instclone.posts.commentsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MyAdapterPosts extends RecyclerView.Adapter<MyAdapterPosts.MyViewHolder> {
    private ArrayList<post> posts;
    private Context context;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;

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
        final int[] num = {2};
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        //checkIfUserLikedPost
        ref =  FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Posts").child(Post.getPostId()).child("Likes");
        ref.child("1").setValue("1");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(user.getUid())&&num[0]!=1){
                    holder.likeButton.setImageResource(R.drawable.liked);
                }else if(num[0]==2){
                    holder.likeButton.setImageResource(R.drawable.like);
                    num[0] =1;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        //setUpStuff
        holder.description.setText(Post.getDescription());
        Glide.with(context).load(Post.getImageUrl()).into(holder.postImage);
        Glide.with(context).load(Post.getUserImageUrl()).into(holder.ownerImage);
        holder.userName.setText(Post.getUserName());

        //LikeOrDislikePost
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref =  FirebaseDatabase.getInstance().getReference("SocialNetwork").child("Posts").child(Post.getPostId());
                if(num[0]==1){
                    ref.child("Likes").child(user.getUid()).setValue(user.getUid());
                    holder.likeButton.setImageResource(R.drawable.liked);
                }else{
                    ref.child("Likes").child(user.getUid()).removeValue();
                    holder.likeButton.setImageResource(R.drawable.like);

                }
            }
        });

        //comments
        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, commentsActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("postId",Post.getPostId());
                context.startActivity(intent);
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
        ImageView likeButton = itemView.findViewById(R.id.likeImage);
        ImageView commentButton = itemView.findViewById(R.id.postComments);
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
