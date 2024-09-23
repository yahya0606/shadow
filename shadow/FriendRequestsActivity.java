package com.yahya.shadow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yahya.shadow.FriendsRecyclerView.FriendsAdapter;
import com.yahya.shadow.UsersRecyclerView.UserObject;

import java.util.ArrayList;

public class FriendRequestsActivity extends AppCompatActivity {

    private RecyclerView.Adapter mUsersAdapter;
    private RecyclerView.LayoutManager mUserLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        RecyclerView mUsersRecyclerView = (RecyclerView) findViewById(R.id.frequest_rv);
        mUsersRecyclerView.setNestedScrollingEnabled(false);
        mUsersRecyclerView.setHasFixedSize(true);
        mUserLayoutManager = new LinearLayoutManager(FriendRequestsActivity.this);
        mUsersRecyclerView.setLayoutManager(mUserLayoutManager);
        mUsersAdapter = new FriendsAdapter(getDataSetUsers(), FriendRequestsActivity.this);
        mUsersRecyclerView.setAdapter(mUsersAdapter);

        getUserIds();

    }
    private void getUserIds() {
        // Showing refresh animation before making http call
        resultsUsers.clear();

        DatabaseReference userIdDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userIdDatabase.child("friend requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot users : snapshot.getChildren()){
                        FetchUsersInformations(users.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void FetchUsersInformations(String userKey) {
        resultsUsers.clear();
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userKey);
        userDatabase.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String user = snapshot.getValue(String.class);
                    DatabaseReference userDatabase1 = FirebaseDatabase.getInstance().getReference().child("users").child(userKey);
                    userDatabase1.child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String userImage = snapshot.getValue(String.class);
                                UserObject obj = new UserObject(user,userKey,userImage);
                                if (!userKey.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    resultsUsers.add(obj);
                                    mUsersAdapter.notifyDataSetChanged();
                                }
                            }else{
                                String userImage = "https://firebasestorage.googleapis.com/v0/b/shadow-6e261.appspot.com/o/Profile%20Pics%2Fprofile.png?alt=media&token=78a3b2dd-179a-419e-98ae-c890fa0be28a";
                                UserObject obj = new UserObject(user,userKey,userImage);
                                if (!userKey.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    resultsUsers.add(obj);
                                    mUsersAdapter.notifyDataSetChanged();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private ArrayList<UserObject> resultsUsers = new ArrayList<UserObject>();
    private ArrayList<UserObject> getDataSetUsers() {
        return resultsUsers;
    }
}