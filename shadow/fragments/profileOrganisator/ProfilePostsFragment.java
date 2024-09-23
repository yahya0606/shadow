package com.yahya.shadow.fragments.profileOrganisator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yahya.shadow.AllPostsRecyclerView.AllPostsAdapter;
import com.yahya.shadow.AllPostsRecyclerView.PostsObject;
import com.yahya.shadow.R;
import com.yahya.shadow.UsersRecyclerView.UserObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfilePostsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView.Adapter mPostsAdapter;
    private RecyclerView.LayoutManager mPostsLayoutManager;
    private String user_id,currentUserId,posterId,post_pic_id;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private CircleImageView share;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_posts, container, false);

        mAuth = FirebaseAuth.getInstance();

        RecyclerView mPostsRecyclerView = (RecyclerView) view.findViewById(R.id.posts);
        mPostsRecyclerView.setNestedScrollingEnabled(false);
        mPostsRecyclerView.setHasFixedSize(true);
        mPostsLayoutManager = new LinearLayoutManager(getActivity());
        mPostsRecyclerView.setLayoutManager(mPostsLayoutManager);
        mPostsAdapter = new AllPostsAdapter(getDataSetPosts(), getActivity());
        mPostsRecyclerView.setAdapter(mPostsAdapter);


        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        mSwipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.purple_700,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


        //populating
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getPostIds();
        resultsPosts.clear();


        //swiper
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                if (mSwipeRefreshLayout != null){
                    mSwipeRefreshLayout.setRefreshing(true);
                }
                // Fetching data from server
            }
        });

        return view;
    }

    @Override
    public void onRefresh() {

        // Fetching data from server
        resultsPosts.clear();
        getPostIds();
    }


    private void getPostIds() {
        // Showing refresh animation before making http call
        mSwipeRefreshLayout.setRefreshing(true);
        resultsPosts.clear();

        DatabaseReference userIdDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString());
        userIdDatabase.child("postsUser").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot posts : snapshot.getChildren()){
                        FetchPostInformations(posts.getKey());
                    }
                }else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void FetchPostInformations(String post_id) {
        resultsPosts.clear();
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(post_id).child("posterId");
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String posterId = snapshot.getValue(String.class);
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
                    // Stopping swipe refresh
                    mSwipeRefreshLayout.setRefreshing(false);
                }
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
}