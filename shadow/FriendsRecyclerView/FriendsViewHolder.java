package com.yahya.shadow.FriendsRecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yahya.shadow.R;
import com.yahya.shadow.TextingActivity;
import com.yahya.shadow.UserSingleActivity;

import java.util.Objects;

public class FriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView userName;
    public TextView userKey;
    public TextView mProfile,mLastMsg,mLastMsgNS;
    public CardView mCard;
    public ImageView mProfilePic;
    public RecyclerView mCommentsRecyclerView;
    public LinearLayout mLinearLastMsgNS;

    public FriendsViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        userName = itemView.findViewById(R.id.usernameItem);
        userKey = itemView.findViewById(R.id.userKeyItem);
        mProfile = itemView.findViewById(R.id.profile);
        mLastMsg = itemView.findViewById(R.id.LastMsg);
        mLastMsgNS = itemView.findViewById(R.id.LastMsgNS);
        mCard = itemView.findViewById(R.id.ProfileCard);
        mProfilePic = itemView.findViewById(R.id.profilePic);
        mCommentsRecyclerView = itemView.findViewById(R.id.comments_recycler_view);
        mLinearLastMsgNS = itemView.findViewById(R.id.LayoutLastMsgNS);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), UserSingleActivity.class);
                Bundle b = new Bundle();
                b.putString("userId",userKey.getText().toString());
                b.putString("destination","friends");
                intent.putExtras(b);
                v.getContext().startActivity(intent);
            }
        });

        String string = userKey.getText().toString();
        String string1 = mAuth.getCurrentUser().getUid();
        String pos1 = string + " with " + string1;
        String pos2 = string1 + " with " + string;
        DatabaseReference RequestsDatabase = FirebaseDatabase.getInstance().getReference().child("chats");
        RequestsDatabase.child(pos1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    RequestsDatabase.child(pos2).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                Toast.makeText(itemView.getContext(), "pos2", Toast.LENGTH_SHORT).show();
                                populate(pos2);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    Toast.makeText(itemView.getContext(), "pos1", Toast.LENGTH_SHORT).show();
                    populate(pos1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void populate(String pos) {
        DatabaseReference RequestsDatabase = FirebaseDatabase.getInstance().getReference().child("chats");
        RequestsDatabase.child(pos).child("infos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String seen = snapshot.child("seen").getValue(String.class);
                    String lastMsg = snapshot.child("lastMsg").getValue(String.class);


                    if (seen.equals("false")){
                        mLinearLastMsgNS.setVisibility(View.VISIBLE);
                        mLastMsg.setVisibility(View.INVISIBLE);
                        mLastMsgNS.setText(lastMsg);
                    }else{
                        mLinearLastMsgNS.setVisibility(View.INVISIBLE);
                        mLastMsg.setVisibility(View.VISIBLE);
                        mLastMsg.setText(lastMsg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), TextingActivity.class);
        Bundle b = new Bundle();
        b.putString("userId",userKey.getText().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);

    }
}
