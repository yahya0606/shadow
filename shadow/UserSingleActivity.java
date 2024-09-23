package com.yahya.shadow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yahya.shadow.fragments.UsersFragment;

import java.util.Map;
import java.util.Objects;

public class UserSingleActivity extends AppCompatActivity {

    private String destination,user_id,privacy,tester;
    private Button mDone,mDelete;
    private TextView mEmail, mCountry, mUsername, mPoints, mReputation, mBio;
    private DatabaseReference mDatabase, mDatabase1, mDatabase2, mDatabase3, mDatabase4, mDatabase5,mDatabase6;

    private ImageView mProfile;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_single);

        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.email);
        mCountry = findViewById(R.id.country);
        mUsername = findViewById(R.id.usernameDown);
        mPoints = findViewById(R.id.points);
        mReputation = findViewById(R.id.thanks);
        mBio = findViewById(R.id.bio);
        mDone = findViewById(R.id.Done);
        mDelete = findViewById(R.id.Delete);

        mProfile = findViewById(R.id.profileImg);

        Bundle user = getIntent().getExtras();
        if (user!=null){
            user_id = user.getString("userId");
            destination = user.getString("destination");
        }

        mBio.setEnabled(false);
        mEmail.setEnabled(false);
        mCountry.setEnabled(false);

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (destination.equals("home")){
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("current",1);
                    intent.putExtras(b);
                    v.getContext().startActivity(intent);
                }else if (destination.equals("friends")){
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("current",0);
                    intent.putExtras(b);
                    v.getContext().startActivity(intent);
                }else{
                    Intent intent = new Intent(v.getContext(), AddFriendActivity.class);
                    v.getContext().startActivity(intent);
                }
            }
        });

        DatabaseReference FollowingDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        FollowingDatabase.child("following").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(user_id)){
                    mDelete.setText("Delete Friend");
                }else{
                    mDelete.setText("Add Friend");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference FollowingDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
                FollowingDatabase.child("following").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(user_id)){
                            snapshot.getRef().removeValue();
                        }else{
                            DatabaseReference current_user_db7 = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("following");
                            current_user_db7.child(user_id).setValue("no");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        //user
        mDatabase2 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("user");
        mDatabase2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user = dataSnapshot.getValue(String.class);
                mUsername.setText(user+" profile");
                tester = user;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserSingleActivity.this, "Error user !", Toast.LENGTH_SHORT).show();
            }
        });

        //profile
        DatabaseReference profileReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        profileReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() >0){
                    if (snapshot.hasChild("profile")){
                        String Image = snapshot.child("profile").getValue().toString();
                        Picasso.get().load(Image).into(mProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //privacy
        privacy = "";
        mDatabase6 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("privacy");
        mDatabase6.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                privacy = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserSingleActivity.this, "Error email !", Toast.LENGTH_SHORT).show();
            }
        });

        //email
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("email");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String email = dataSnapshot.getValue(String.class);
                    if (privacy.equals("no")){
                        mEmail.setText(tester+" does't want to show his email");
                    }else{
                        mEmail.setText(email);
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserSingleActivity.this, "Error email !", Toast.LENGTH_SHORT).show();
            }
        });

        //country
        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("country");
        mDatabase1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String country = dataSnapshot.getValue(String.class);
                mCountry.setText(country);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserSingleActivity.this, "Error country !", Toast.LENGTH_SHORT).show();
            }
        });

        //bio
        mDatabase3 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("bio");
        mDatabase3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String bio = dataSnapshot.getValue(String.class);
                mBio.setText(bio);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserSingleActivity.this, "Error bio !", Toast.LENGTH_SHORT).show();
            }
        });
        //points
        mDatabase4 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("points");
        mDatabase4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float points = dataSnapshot.getValue(float.class);
                mPoints.setText(String.valueOf(points));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserSingleActivity.this, "Error points !", Toast.LENGTH_SHORT).show();
            }
        });

        //reputation
        mDatabase5 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("reputation");
        mDatabase5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer reputation = dataSnapshot.getValue(Integer.class);
                String repu = Integer.toString(reputation);
                mReputation.setText(repu);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserSingleActivity.this, "Error reputation !", Toast.LENGTH_SHORT).show();
            }
        });

    }
}




