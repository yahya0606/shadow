package com.yahya.shadow.MyRatingHistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yahya.shadow.FriendsRecyclerView.FriendsViewHolder;
import com.yahya.shadow.R;
import com.yahya.shadow.UsersRecyclerView.UserObject;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingViewHolder>{

    private List<RatingObject> itemList;
    private Context context;
    private FirebaseAuth mAuth;

    public RatingAdapter(List<RatingObject> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_rating_item,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        RatingViewHolder rcv = new RatingViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        holder.mRaterUser.setText(itemList.get(position).getRaterUsername()+" :");
        holder.mRateVal.setRating(itemList.get(position).getRateVal());
        holder.mRateNum.setText("("+String.valueOf(itemList.get(position).getRateVal())+")");

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("users").child(itemList.get(position).getRaterId());
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if (snapshot.child("profile").exists()){
                        String profile = snapshot.child("profile").getValue().toString();
                        Picasso.get().load(profile).into(holder.mRaterPic);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
