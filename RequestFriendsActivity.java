package com.yahya.shadow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RequestFriendsActivity extends AppCompatActivity {

    private EditText mContent;
    private ImageView mSendRequest;
    private int i,j,k,random,value;
    private String PickedReceiverId,uniqueMsgId,forbiddenList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_friends);

        mContent = findViewById(R.id.RequestContent);
        mSendRequest = findViewById(R.id.SendRequest);
        PickedReceiverId="";

        mSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRandmReciever();
                PickedReceiverId="";
            }
        });

    }

    private void getRandmReciever() {
        i=0;
        k=0;
        DatabaseReference userIdDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        userIdDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot userCount : snapshot.getChildren()){
                        if(userCount.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                           k=i;
                        }else{
                            DatabaseReference userIdDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("blocked accounts");
                            userIdDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        for (DataSnapshot users : snapshot.getChildren()){
                                            if (users.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                forbiddenList = forbiddenList+String.valueOf(i)+" ";
                                                break;
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        i++;
                    }
                }
                pickTheReceiver(i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void pickTheReceiver(int i) {
        j=0;
        do {
            random = new Random().nextInt(i);
        }while ((random == k) || (forbiddenList.contains(String.valueOf(random))));
        DatabaseReference userIdDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        userIdDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot users : snapshot.getChildren()){
                        if (j == random){
                            PickedReceiverId = users.getKey();
                            Toast.makeText(RequestFriendsActivity.this, PickedReceiverId, Toast.LENGTH_SHORT).show();
                            SendRequest(PickedReceiverId);
                            break;
                        }else{
                            j++;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void SendRequest(String pickedReceiverId) {
        uniqueMsgId = UUID.randomUUID().toString();
        //sender side
        DatabaseReference current_user_db1 = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("requests sent").child(uniqueMsgId);
        current_user_db1.child("receiver").setValue(pickedReceiverId);
        current_user_db1.child("sender").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        current_user_db1.child("timestamp").setValue(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        current_user_db1.child("message").setValue(mContent.getText().toString());

        //receiver side
        DatabaseReference current_user_db2 = FirebaseDatabase.getInstance().getReference().child("users").child(pickedReceiverId).child("requests received").child(uniqueMsgId);
        current_user_db2.child("sender").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        current_user_db2.child("receiver").setValue(pickedReceiverId);
        current_user_db2.child("timestamp").setValue(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        current_user_db2.child("message").setValue(mContent.getText().toString());

        //going back
        Intent intent = new Intent(RequestFriendsActivity.this, MainActivity.class);
        Bundle b = new Bundle();
        b.putInt("current",1);
        intent.putExtras(b);
        startActivity(intent);
    }
}