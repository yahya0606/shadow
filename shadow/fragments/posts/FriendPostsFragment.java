package com.yahya.shadow.fragments.posts;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yahya.shadow.AllPostsRecyclerView.AllPostsAdapter;
import com.yahya.shadow.AllPostsRecyclerView.PostsObject;
import com.yahya.shadow.PostingActivity;
import com.yahya.shadow.R;
import com.yahya.shadow.UsersRecyclerView.UserObject;
import com.yahya.shadow.fragments.posts.userDisplayer.friendPicsAdapter;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendPostsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private RecyclerView.Adapter mPostsAdapter;
    private RecyclerView.Adapter mUsersAdapter;
    private RecyclerView.LayoutManager mPostsLayoutManager;
    private RecyclerView.LayoutManager mUsersLayoutManager;
    private String user_id,currentUserId,posterId,post_pic_id,following_id;
    SwipeRefreshLayout mSwipeRefreshLayout,mSwipeRefreshLayout1;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ImageView MyPic;

    private CircleImageView share;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_posts, container, false);

        share = view.findViewById(R.id.fpost);
        MyPic = view.findViewById(R.id.MyPic);
        mAuth = FirebaseAuth.getInstance();

        //posts
        RecyclerView mPostsRecyclerView = (RecyclerView) view.findViewById(R.id.fposts);
        mPostsRecyclerView.setNestedScrollingEnabled(false);
        mPostsRecyclerView.setHasFixedSize(true);
        mPostsLayoutManager = new LinearLayoutManager(getActivity());
        mPostsRecyclerView.setLayoutManager(mPostsLayoutManager);
        mPostsAdapter = new AllPostsAdapter(getDataSetPosts(), getActivity());
        mPostsRecyclerView.setAdapter(mPostsAdapter);

        //users
        RecyclerView mUsersRecyclerView = (RecyclerView) view.findViewById(R.id.fusers);
        mUsersRecyclerView.setNestedScrollingEnabled(false);
        mUsersRecyclerView.setHasFixedSize(true);
        mUsersLayoutManager = new GridLayoutManager(getContext(),1,RecyclerView.HORIZONTAL,false);
        mUsersRecyclerView.setLayoutManager(mUsersLayoutManager);
        mUsersAdapter = new friendPicsAdapter(getDataSetUsers(), getActivity());
        mUsersRecyclerView.setAdapter(mUsersAdapter);

        //posting page
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostingActivity.class);
                startActivity(intent);
            }
        });


        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fswipeer);
        mSwipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.purple_700,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        // SwipeRefreshLayout
        mSwipeRefreshLayout1 = (SwipeRefreshLayout) view.findViewById(R.id.swiper);
        mSwipeRefreshLayout1.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);
        mSwipeRefreshLayout1.setColorSchemeResources(R.color.purple_700,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        DatabaseReference fillingUserPic = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);
        fillingUserPic.child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String profile = snapshot.getValue(String.class);
                    Picasso.get().load(profile).into(MyPic);
                }else{
                    MyPic.setImageResource(R.drawable.account);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //populating
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getUserIds();


        //swiper
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                if (mSwipeRefreshLayout != null){
                    mSwipeRefreshLayout.setRefreshing(true);
                    mSwipeRefreshLayout1.setRefreshing(true);
                }
                // Fetching data from server
            }
        });

        return view;
    }

    private void getUserIds() {
        DatabaseReference userIdDatabase1 = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        userIdDatabase1.child("following").addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void refresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        resultsPosts.clear();

        DatabaseReference userIdDatabase = FirebaseDatabase.getInstance().getReference().child("posts");
        userIdDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot posts : snapshot.getChildren()){
                        FetchPostInformations(posts.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        usersList.clear();
        DatabaseReference userIdDatabase1 = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        userIdDatabase1.child("following").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot users : snapshot.getChildren()){
                        FetchUsersInformations(users.getKey());
                    }
                }else{
                    mSwipeRefreshLayout1.setRefreshing(false);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference fillingUserPic = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);
        fillingUserPic.child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String profile = snapshot.getValue(String.class);
                    Picasso.get().load(profile).into(MyPic);
                }else{
                    MyPic.setImageResource(R.drawable.account);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void FetchUsersInformations(String userKey) {
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
                                    following_id = userKey;
                                    getPostIds();
                                    usersList.add(obj);
                                    mUsersAdapter.notifyDataSetChanged();
                                    mSwipeRefreshLayout1.setRefreshing(false);
                                }
                                // Stopping swipe refresh
                                mSwipeRefreshLayout1.setRefreshing(false);
                            }else{
                                String userImage = "https://firebasestorage.googleapis.com/v0/b/shadow-6e261.appspot.com/o/Profile%20Pics%2Fprofile.png?alt=media&token=78a3b2dd-179a-419e-98ae-c890fa0be28a";
                                UserObject obj = new UserObject(user,userKey,userImage);
                                if (!userKey.equals(user_id)){
                                    following_id = userKey;
                                    getPostIds();
                                    usersList.add(obj);
                                    mUsersAdapter.notifyDataSetChanged();
                                }
                                // Stopping swipe refresh
                                mSwipeRefreshLayout1.setRefreshing(false);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                mSwipeRefreshLayout1.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onRefresh() {

        // Fetching data from server
        refresh();

    }

    private void getPostIds() {
        // Showing refresh animation before making http call
        mSwipeRefreshLayout.setRefreshing(true);
        resultsPosts.clear();

        DatabaseReference userIdDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(following_id).child("postsUser");
        userIdDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot posts : snapshot.getChildren()){
                        FetchPostInformations(posts.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void FetchPostInformations(String post_id) {
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(post_id).child("posterId");
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String posterId = snapshot.getValue(String.class);

                    //checking if the user is following the poster
                        DatabaseReference post1 = FirebaseDatabase.getInstance().getReference().child("posts").child(post_id);
                        post1.child("description").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String post_desc = snapshot.getValue(String.class);
                                DatabaseReference post2 = FirebaseDatabase.getInstance().getReference().child("posts").child(post_id);
                                post2.child("posterUserId").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            String poster_user = snapshot.getValue(String.class);
                                            DatabaseReference post2 = FirebaseDatabase.getInstance().getReference().child("posts").child(post_id);
                                            post2.child("pic_id").child("post").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()){
                                                        String post_pic_id = snapshot.getValue(String.class);
                                                        PostsObject obj = new PostsObject(post_id,post_desc,posterId,poster_user,post_pic_id);
                                                        resultsPosts.add(obj);
                                                        mPostsAdapter.notifyDataSetChanged();
                                                        mSwipeRefreshLayout.setRefreshing(false);
                                                    }else {
                                                        String post_pic_id = "likep.png";
                                                        PostsObject obj = new PostsObject(post_id, post_desc, posterId, poster_user, post_pic_id);
                                                        resultsPosts.add(obj);
                                                        mPostsAdapter.notifyDataSetChanged();
                                                        mSwipeRefreshLayout.setRefreshing(false);

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            //PostsObject obj = new PostsObject(post_id,post_desc,posterId,poster_user,post_pic_id);
                                            //resultsPosts.add(obj);
                                            //mPostsAdapter.notifyDataSetChanged();

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


                    // Stopping swipe refresh
                    mSwipeRefreshLayout.setRefreshing(false);
                    }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private ArrayList resultsPosts = new ArrayList<UserObject>();
    private ArrayList<PostsObject> getDataSetPosts() {
        return resultsPosts;
    }
    private ArrayList usersList = new ArrayList<UserObject>();
    private ArrayList<UserObject> getDataSetUsers() {
        return usersList;
    }
}