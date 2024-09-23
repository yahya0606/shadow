package com.yahya.shadow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.squareup.picasso.Picasso;

public class RequestFullPageActivity extends AppCompatActivity {

    private ImageView mSenderProfile;
    private TextView mMsg;
    private String userId,requester,requestId;
    private Button mCancel,mKeep;
    private SlidrInterface slidr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_full_page);

        mCancel = findViewById(R.id.fish);
        mKeep = findViewById(R.id.keep);
        mSenderProfile = findViewById(R.id.RequestPic);
        mMsg = findViewById(R.id.RequestMsg);
        userId = "";

        slidr = Slidr.attach(this);

        mKeep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId.length()>2){
                    DatabaseReference current_user_db1 = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
                    current_user_db1.child(userId).setValue("no");
                    DatabaseReference current_user_db2 = FirebaseDatabase.getInstance().getReference().child("users").child(requester).child("requests sent");
                    current_user_db2.child(requestId).removeValue();
                    DatabaseReference current_user_db3 = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("requests received");
                    current_user_db3.child(requestId).removeValue();
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("current",1);
                    intent.putExtras(b);
                    v.getContext().startActivity(intent);
                }
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                Bundle b = new Bundle();
                b.putInt("current",1);
                intent.putExtras(b);
                v.getContext().startActivity(intent);
            }
        });

        Bundle user = getIntent().getExtras();
        if (user!=null){
            mMsg.setText(user.getString("message"));
            userId = user.getString("userId");
            requester = user.getString("requester");
            requestId = user.getString("requestId");
            //profile
            DatabaseReference profileReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            profileReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.getChildrenCount() >0){
                        if (snapshot.hasChild("profile")){
                            String Image = snapshot.child("profile").getValue().toString();
                            Picasso.get().load(Image).into(mSenderProfile);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        mSenderProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), UserSingleActivity.class);
                Bundle b = new Bundle();
                b.putString("userId",userId);
                b.putString("destination","home");
                intent.putExtras(b);
                v.getContext().startActivity(intent);
            }
        });
    }
}