package com.yahya.shadow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yahya.shadow.UsersRecyclerView.UserObject;
import com.yahya.shadow.UsersRecyclerView.UsersAdapter;

import java.util.ArrayList;

public class AddFriendActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView.Adapter mUsersAdapter;
    private RecyclerView.LayoutManager mUserLayoutManager;
    private String user_id,currentUserId;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mNoFriends;
    private EditText mSearchBox;
    private ImageView mSearchBtn,mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        RecyclerView mUsersRecyclerView = (RecyclerView) findViewById(R.id.users);
        mUsersRecyclerView.setNestedScrollingEnabled(false);
        mUsersRecyclerView.setHasFixedSize(true);
        mUserLayoutManager = new LinearLayoutManager(AddFriendActivity.this);
        mUsersRecyclerView.setLayoutManager(mUserLayoutManager);
        mUsersAdapter = new UsersAdapter(getDataSetUsers(), AddFriendActivity.this);
        mUsersRecyclerView.setAdapter(mUsersAdapter);

        mSearchBtn = findViewById(R.id.searchBtn);
        mSearchBox = findViewById(R.id.searchBox);
        mNoFriends = findViewById(R.id.NoFriends);
        mBack = findViewById(R.id.back);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiper);
        mSwipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.purple_700,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                Bundle b = new Bundle();
                b.putInt("current",0);
                intent.putExtras(b);
                v.getContext().startActivity(intent);
            }
        });

        //populating
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        resultsUsers.clear();
        getUserIds();



        //swiper
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                if (mSwipeRefreshLayout != null){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                // Fetching data from server
            }
        });
        mSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mNoFriends.setVisibility(View.GONE);
                resultsUsers.clear();
                filter(s.toString().trim());
                resultsUsers.clear();
            }
        });

    }

    private void filter(String userName) {
        DatabaseReference userIdDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        userIdDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot users : snapshot.getChildren()){
                        FetchFilteredInformations(users.getKey(),userName);
                        mNoFriends.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void FetchFilteredInformations(String userID, String userName) {
        resultsUsers.clear();
        mNoFriends.setVisibility(View.GONE);
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        userDatabase.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String user = snapshot.getValue(String.class);
                    DatabaseReference userDatabase1 = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
                    userDatabase1.child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String userImage = snapshot.getValue(String.class);
                                UserObject obj = new UserObject(user,userID,userImage);
                                if (!userID.equals(user_id)){
                                    if (user.toLowerCase().trim().contains(userName.toLowerCase().trim())){
                                        mNoFriends.setVisibility(View.GONE);
                                        resultsUsers.add(obj);
                                        mUsersAdapter.notifyDataSetChanged();
                                    }else{
                                        if (resultsUsers.size()==0){
                                            mNoFriends.setText("No matches found!");
                                            mNoFriends.setVisibility(View.VISIBLE);
                                        }else{
                                            mNoFriends.setVisibility(View.GONE);
                                        }
                                    }
                                }
                                // Stopping swipe refresh
                                mSwipeRefreshLayout.setRefreshing(false);
                            }else{
                                String userImage = "https://firebasestorage.googleapis.com/v0/b/shadow-6e261.appspot.com/o/Profile%20Pics%2Fprofile.png?alt=media&token=78a3b2dd-179a-419e-98ae-c890fa0be28a";
                                UserObject obj = new UserObject(user,userID,userImage);
                                if (user.toLowerCase().trim().contains(userName)){
                                    resultsUsers.add(obj);
                                    mUsersAdapter.notifyDataSetChanged();
                                }
                                // Stopping swipe refresh
                                mSwipeRefreshLayout.setRefreshing(false);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void refresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        resultsUsers.clear();

        DatabaseReference userIdDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        userIdDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
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

    @Override
    public void onRefresh() {

        // Fetching data from server
        resultsUsers.clear();
        refresh();
    }

    private void getUserIds() {
        // Showing refresh animation before making http call
        mSwipeRefreshLayout.setRefreshing(false);
        resultsUsers.clear();

        DatabaseReference userIdDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        userIdDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot users : snapshot.getChildren()){
                        DatabaseReference userIdDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("blocked accounts");
                        userIdDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    for (DataSnapshot users : snapshot.getChildren()){
                                        if (users.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            break;
                                        }else{
                                            FetchUsersInformations(users.getKey());
                                        }
                                    }
                                }else{
                                    FetchUsersInformations(users.getKey());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }else{
                    mNoFriends.setText("No Users found!");
                    mNoFriends.setVisibility(View.VISIBLE);
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
                                if (!userKey.equals(user_id)){
                                    resultsUsers.add(obj);
                                    mUsersAdapter.notifyDataSetChanged();
                                }
                                // Stopping swipe refresh
                                mSwipeRefreshLayout.setRefreshing(false);
                            }else{
                                String userImage = "https://firebasestorage.googleapis.com/v0/b/shadow-6e261.appspot.com/o/Profile%20Pics%2Fprofile.png?alt=media&token=78a3b2dd-179a-419e-98ae-c890fa0be28a";
                                UserObject obj = new UserObject(user,userKey,userImage);
                                if (!userKey.equals(user_id)){
                                    resultsUsers.add(obj);
                                    mUsersAdapter.notifyDataSetChanged();
                                }
                                // Stopping swipe refresh
                                mSwipeRefreshLayout.setRefreshing(false);
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

    private ArrayList resultsUsers = new ArrayList<UserObject>();
    private ArrayList<UserObject> getDataSetUsers() {
        return resultsUsers;
    }
}
