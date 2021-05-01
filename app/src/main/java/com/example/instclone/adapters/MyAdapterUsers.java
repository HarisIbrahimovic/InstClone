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
import com.example.instclone.objects.user;

import java.util.ArrayList;

public class MyAdapterUsers extends RecyclerView.Adapter<MyAdapterUsers.MyViewHolder> {
    Context context;
    ArrayList<user> users;

    public MyAdapterUsers(Context context, ArrayList<user> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        user User = users.get(position);
        Glide.with(context).load(User.getImageUrl()).into(holder.imageView);
        holder.userName.setText(User.getUserName());

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void filterList(ArrayList<user> filteredList) {
        users = filteredList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName = itemView.findViewById(R.id.searchUserName);
        ImageView imageView = itemView.findViewById(R.id.searchUserPicture);
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
