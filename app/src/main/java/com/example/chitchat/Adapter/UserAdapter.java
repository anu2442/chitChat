package com.example.chitchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chitchat.Activity.ChatActivity;
import com.example.chitchat.R;
import com.example.chitchat.ModelClass.User;
import com.example.chitchat.Activity.HomeActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Context homeActivity;
    ArrayList<User> userArrayList;
    public UserAdapter(HomeActivity homeActivity, ArrayList<User> userArrayList) {
        this.homeActivity = homeActivity;
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(homeActivity).inflate(R.layout.item_user_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user = userArrayList.get(position);

        holder.userName.setText(user.getName());
        holder.userStatus.setText(user.getStatus());
        Picasso.get().load(user.getImageUri()).into(holder.userProfileImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeActivity, ChatActivity.class);
                intent.putExtra("name",user.getName());
                intent.putExtra("receiverImage",user.getImageUri());
                intent.putExtra("uid",user.getUid());
                homeActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView userProfileImage;
        TextView userName,userStatus;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userProfileImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            userStatus = itemView.findViewById(R.id.userStatus);
        }
    }
}
