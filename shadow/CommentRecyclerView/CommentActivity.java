package com.yahya.shadow.CommentRecyclerView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.yahya.shadow.R;
import com.yahya.shadow.UsersRecyclerView.UserObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView.LayoutManager mCommentsLayoutManager;
    private RecyclerView.Adapter mCommentsAdapter;
    private String user_id,currentUserId,posterId,post_pic_id,post_id;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private DatabaseReference mDatabase;
    private TextView mNoComments;

    private CircleImageView share;

    private SlidrInterface slidr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        //posts recyclerView
        RecyclerView mCommentsRecyclerView = (RecyclerView) findViewById(R.id.comments);
        mCommentsRecyclerView.setNestedScrollingEnabled(false);
        mCommentsRecyclerView.setHasFixedSize(true);
        mCommentsLayoutManager = new LinearLayoutManager(this);
        mCommentsRecyclerView.setLayoutManager(mCommentsLayoutManager);
        mCommentsAdapter = new CommentAdapter(getDataSetComments(), this);
        mCommentsRecyclerView.setAdapter(mCommentsAdapter);

        Bundle post = getIntent().getExtras();
        slidr = Slidr.attach(this);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mNoComments = findViewById(R.id.noCommentsFound);

        mNoComments.setVisibility(View.GONE);

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiper);
        mSwipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.purple_700,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        if (post!=null){
            post_id = post.getString("postId");
        }

        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getCommentIds(post_id);
        resultsComments.clear();

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

    }

    private void getCommentIds(String post_id) {
        resultsComments.clear();
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(post_id).child("comments");
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot posts : snapshot.getChildren()){
                        FetchCommentInformations(post_id,posts.getKey());
                    }
                }else {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mNoComments.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void FetchCommentInformations(String post_id, String comment_id) {
        resultsComments.clear();
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(post_id).child("comments");
        userDatabase.child(comment_id).child("comment").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String comment = snapshot.getValue(String.class);
                    userDatabase.child(comment_id).child("commenter").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String commenter = snapshot.getValue(String.class);
                                DatabaseReference post2 = FirebaseDatabase.getInstance().getReference().child("users").child(commenter);
                                post2.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            String commenter_user = snapshot.getValue(String.class);
                                            post2.child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String Profile = snapshot.getValue(String.class);
                                                    userDatabase.child(comment_id).child("postID").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()){
                                                                String postID = snapshot.getValue(String.class);
                                                                CommentObject obj = new CommentObject(commenter_user,comment,commenter,Profile);
                                                                resultsComments.add(obj);
                                                                mCommentsAdapter.notifyDataSetChanged();
                                                                mSwipeRefreshLayout.setRefreshing(false);
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
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
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onRefresh() {
        getCommentIds(post_id);
        resultsComments.clear();


    }
    private ArrayList resultsComments = new ArrayList<UserObject>();
    private ArrayList<CommentObject> getDataSetComments() {
        return resultsComments;
    }
}