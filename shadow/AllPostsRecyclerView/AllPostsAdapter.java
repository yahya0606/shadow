package com.yahya.shadow.AllPostsRecyclerView;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yahya.shadow.CommentRecyclerView.CommentActivity;
import com.yahya.shadow.CommentRecyclerView.CommentAdapter;
import com.yahya.shadow.CommentRecyclerView.CommentObject;
import com.yahya.shadow.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AllPostsAdapter extends RecyclerView.Adapter<PostsViewHolder>  {
    private List<PostsObject> postList;
    private Context context;
    private FirebaseAuth mAuth;
    private RecyclerView.Adapter adapter;
    private PopupWindow popupWindow;
    private View popupView;
    private int i;
    private View relativeLayout;


    public AllPostsAdapter (List<PostsObject> postList, Context context) {
        this.postList = postList;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        itemList.clear();
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        PostsViewHolder rcv = new PostsViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder2, int position) {

        relativeLayout = (View) holder2.mCommentLayout;

        holder2.userNamePost.setText(postList.get(position).getPoster_user());
        holder2.Post_desc.setText(postList.get(position).getPost_desc());
        holder2.post_Poster_user.setText(postList.get(position).getPoster_user()+":");
        holder2.Post_id.setText(postList.get(position).getPost_id());
        holder2.Poster_id.setText(postList.get(position).getPosterId());

        holder2.mCommentLayout.setVisibility(View.GONE);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        holder2.mCommentLayout.setLayoutParams(lp);
        i=0;
        // Showing refresh animation before making http call
        adapter = new CommentAdapter(getDataSetComments(),context);
        holder2.mCommentsRecyclerView.setAdapter(adapter);
        holder2.mCommentsRecyclerView.setNestedScrollingEnabled(false);
        holder2.mCommentsRecyclerView.setHasFixedSize(true);
        holder2.mCommentsRecyclerView.setLayoutManager(new GridLayoutManager(context,1,RecyclerView.VERTICAL,false));

        holder2.mShowHide.setText("Show comments ("+itemList.size()+")▼");



        holder2.mShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (itemList.size()==0){
                        //holder2.mShowHide.setText("Hide comments ▲");
                        holder2.mNoComment.setVisibility(View.VISIBLE);
                        holder2.mCommentsRecyclerView.setVisibility(View.GONE);
                        holder2.mCommentLayout.setVisibility(View.GONE);
                    }else{
                        //holder2.mShowHide.setText("Hide comments ▲");
                        holder2.mNoComment.setVisibility(View.GONE);
                        holder2.mCommentLayout.setVisibility(VISIBLE);
                        holder2.mCommentLayout.setVisibility(View.GONE);

                        Intent intent = new Intent(v.getContext(), CommentActivity.class);
                        Bundle b = new Bundle();
                        b.putString("postId",holder2.Post_id.getText().toString());
                        intent.putExtras(b);
                        v.getContext().startActivity(intent);
                    }
            }
        });

        //setting texts
        String user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("posts").child(holder2.Post_id.getText().toString()).child("likes").child("likers");
        ref.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //like
                    holder2.mLike.setImageResource(R.drawable.liked);

                } else {
                    //unlike
                    holder2.mLike.setImageResource(R.drawable.likep);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //populating pic
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("users").child(holder2.Poster_id.getText().toString());
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if (snapshot.child("profile").exists()){
                        String profile = snapshot.child("profile").getValue().toString();
                        Picasso.get().load(profile).into(holder2.mProfile);
                    }else{
                        holder2.mProfile.setImageResource(R.drawable.account);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference().child("posts").child(holder2.Post_id.getText().toString());
        ref3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if (snapshot.child("pic_id").child("post").exists()){
                        String profile = snapshot.child("pic_id").child("post").getValue().toString();
                        Picasso.get().load(profile).into(holder2.post);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //populating ended
        DatabaseReference userIdDatabase = FirebaseDatabase.getInstance().getReference().child("posts");
        userIdDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot posts : snapshot.getChildren()){
                        //getComments(posts.getKey());
                        itemList.clear();
                        getComments(posts.getKey());
                        itemList.clear();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    private void getComments(String post_id) {
        itemList.clear();
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(post_id).child("comments");
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot posts : snapshot.getChildren()){
                        FetchCommentInformations(post_id,posts.getKey());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void FetchCommentInformations(String post_id, String comment_id) {
        itemList.clear();
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
                                                                if (itemList.size()<4) {
                                                                    itemList.add(obj);
                                                                    adapter.notifyDataSetChanged();
                                                                }
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


    private ArrayList itemList = new ArrayList<CommentObject>();
    private ArrayList<CommentObject> getDataSetComments() {
        return itemList;
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}
