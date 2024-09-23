package com.yahya.shadow.CommentRecyclerView;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.yahya.shadow.FriendsRecyclerView.FriendsViewHolder;
import com.yahya.shadow.R;
import com.yahya.shadow.UsersRecyclerView.UserObject;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {
    public static final int My_Comment = 0;
    public static final int Others_Comment = 1;
    private ArrayList<CommentObject> itemList;
    private Context context;
    private FirebaseAuth mAuth;
    private String MineOrNot;

    public CommentAdapter(ArrayList<CommentObject> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }



    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == My_Comment){
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.own_comment,null,false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);
            CommentViewHolder rcv = new CommentViewHolder(layoutView);
            return rcv;
        }else{
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.others_comment,null,false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);
            CommentViewHolder rcv = new CommentViewHolder(layoutView);
            return rcv;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.mCommenter.setText(itemList.get(position).getCommenter_id());
        holder.mComment.setText(itemList.get(position).getComment_text());
        holder.mUserId = itemList.get(position).getUserId();
        Picasso.get().load(itemList.get(position).getUserProfile()).into(holder.mProfile);

    }

    @Override
    public int getItemViewType(int position) {
        if (itemList.get(position).getUserId().equals(mAuth.getCurrentUser().getUid())){
            return My_Comment;
        }else{
            return Others_Comment;
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
