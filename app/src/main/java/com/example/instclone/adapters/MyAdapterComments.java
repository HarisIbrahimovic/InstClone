package com.example.instclone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instclone.R;
import com.example.instclone.objects.comment;

import java.util.ArrayList;

public class MyAdapterComments extends RecyclerView.Adapter<MyAdapterComments.MyViewHolder> {
    private Context context;
    private ArrayList<comment> comments;

    public MyAdapterComments(Context context, ArrayList<comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        comment Comment = comments.get(position);
        holder.userName.setText(Comment.getUserName());
        holder.commentContent.setText(Comment.getCommentContent());
        Glide.with(context).load(Comment.getImageUrl()).into(holder.userImage);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView userName = itemView.findViewById(R.id.commentUserName);
        ImageView userImage = itemView.findViewById(R.id.commentPhoto);
        TextView commentContent = itemView.findViewById(R.id.commentContent);
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
