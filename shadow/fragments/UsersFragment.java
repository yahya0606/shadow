package com.yahya.shadow.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yahya.shadow.AddFriendActivity;
import com.yahya.shadow.FriendRequestsActivity;
import com.yahya.shadow.FriendsRecyclerView.FriendsAdapter;
import com.yahya.shadow.PostingActivity;
import com.yahya.shadow.R;
import com.yahya.shadow.UsersRecyclerView.UserObject;
import com.yahya.shadow.UsersRecyclerView.UsersAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class UsersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView.Adapter mUsersAdapter;
    private RecyclerView.LayoutManager mUserLayoutManager;
    private String user_id,currentUserId,s;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private DatabaseReference mDatabase;
    private CircleImageView mAdd;
    private FirebaseAuth mAuth;
    private TextView mNoFriends,mRequests;
    private EditText mSearchBox;
    private ImageView mSearchBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        RecyclerView mUsersRecyclerView = (RecyclerView) view.findViewById(R.id.users);
        mUsersRecyclerView.setNestedScrollingEnabled(false);
        mUsersRecyclerView.setHasFixedSize(true);
        mUserLayoutManager = new LinearLayoutManager(getActivity());
        mUsersRecyclerView.setLayoutManager(mUserLayoutManager);
        mUsersAdapter = new FriendsAdapter(getDataSetUsers(), getActivity());
        mUsersRecyclerView.setAdapter(mUsersAdapter);

        mSearchBtn = view.findViewById(R.id.searchBtn);
        mSearchBox = view.findViewById(R.id.searchBox);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mAdd = view.findViewById(R.id.add);
        mAuth = FirebaseAuth.getInstance();
        mNoFriends = view.findViewById(R.id.NoFriends);
        mRequests = view.findViewById(R.id.Requests);
        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiper);
        mSwipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.purple_700,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!(s.toString().trim().length()==0)){
                    mNoFriends.setVisibility(View.GONE);
                    resultsUsers.clear();
                    filter(s.toString().trim());
                    resultsUsers.clear();
                }else{
                    resultsUsers.clear();
                    //getUserIds();
                    getUserIds();
                }
            }
        });
        //get friend requests
        getFriendRequests();
        mRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FriendRequestsActivity.class);
                startActivity(intent);
            }
        });
        //populating
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //resultsUsers.clear();
        getUserIds();

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                startActivity(intent);
            }
        });

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

        return view;
    }

    private void getFriendRequests() {
        DatabaseReference RequestsDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        RequestsDatabase.child("friend requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Integer q = 0;
                    for (DataSnapshot frequests : snapshot.getChildren()){
                        q++;
                    }
                    mRequests.setText("Requests ("+String.valueOf(q)+")");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void filter(String userName) {
        DatabaseReference userIdDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        userIdDatabase.child("following").addListenerForSingleValueEvent(new ValueEventListener() {
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

        DatabaseReference userIdDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        userIdDatabase.child("following").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot users : snapshot.getChildren()){
                            FetchUsersInformations(users.getKey());
                            mNoFriends.setVisibility(View.GONE);
                        }
                    }else{
                        mSwipeRefreshLayout.setRefreshing(false);
                        mNoFriends.setText("No friends found!");
                        mNoFriends.setVisibility(View.VISIBLE);
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
        mSwipeRefreshLayout.setRefreshing(false);
        refresh();
    }

    private void getUserIds() {
        // Showing refresh animation before making http call
        mSwipeRefreshLayout.setRefreshing(false);
        mNoFriends.setVisibility(View.GONE);
        resultsUsers.clear();

        DatabaseReference userIdDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        userIdDatabase.child("following").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot users : snapshot.getChildren()){
                            FetchUsersInformations(users.getKey());
                        }
                    }else{
                        mNoFriends.setText("No friends found!");
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
                mSwipeRefreshLayout.setRefreshing(false);
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