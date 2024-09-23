package com.yahya.shadow.CommentRecyclerView;

import android.text.Layout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.yahya.shadow.R;

public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView mComment,mCommenter;
    public ImageView mProfile;
    public  String mUserId;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mComment = itemView.findViewById(R.id.comment);
        mCommenter = itemView.findViewById(R.id.userName);
        mProfile = itemView.findViewById(R.id.profile);
        mUserId = "";



    }

    @Override
    public void onClick(View v) {


    }
}
