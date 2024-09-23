package com.yahya.shadow.AllPostsRecyclerView;

import static android.view.View.VISIBLE;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yahya.shadow.R;

import java.util.Objects;
import java.util.UUID;

public class PostsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView userNamePost,mShowHide;
    public TextView Post_desc,post_Poster_user;
    public TextView Post_id,Poster_id;
    public ImageView mLike;
    private String postId,uniqueId;
    public String user;
    private Integer likes;
    private Boolean testLike;
    public ImageView post;
    public ImageView mProfile;
    public ImageView mComment;
    public ImageView mCommentShow;
    private EditText mSpeak;
    public RecyclerView mCommentsRecyclerView;
    public TextView mNoComment;
    public View mCommentLayout;
    private int i;


    public PostsViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mCommentsRecyclerView = (RecyclerView) itemView.findViewById(R.id.comments);
        mNoComment = itemView.findViewById(R.id.NoComments);
        mShowHide = itemView.findViewById(R.id.show_hide_comments);
        mComment = itemView.findViewById(R.id.commentBtn);
        mCommentShow = itemView.findViewById(R.id.commentButton);
        mSpeak = itemView.findViewById(R.id.comment);
        userNamePost = itemView.findViewById(R.id.postUsername);
        Post_desc = itemView.findViewById(R.id.post_desc);
        Post_id = itemView.findViewById(R.id.postId);
        Poster_id = itemView.findViewById(R.id.posterId);
        mCommentLayout = itemView.findViewById(R.id.comment_shower);
        post_Poster_user = itemView.findViewById(R.id.post_Poster_user);

        post = itemView.findViewById(R.id.postPic);

        mProfile = itemView.findViewById(R.id.profile);

        mLike = itemView.findViewById(R.id.like);

        postId = Post_id.getText().toString();

        testLike = false;
        i=0;
        mShowHide.setText("Show comment ("+i+")▼");


        DatabaseReference like = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("likes");
         like.child("number").addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if(snapshot.exists()){
                     likes = snapshot.getValue(Integer.class);
                 }
             }
             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });


         mComment.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 uniqueId = UUID.randomUUID().toString();
                 String comment = mSpeak.getText().toString();
                 String postId = Post_id.getText().toString();
                 DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("comments");
                 DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("comments");
                 ref.child(uniqueId).addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                         if(snapshot.exists()){
                             // user already followed
                             // unfollow
                             do {
                                 uniqueId = UUID.randomUUID().toString();
                                 ref.child(uniqueId).addListenerForSingleValueEvent(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                                         if (snapshot.exists()){

                                         }
                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError error) {

                                     }
                                 });
                             } while (snapshot.exists());

                         }
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError error) {

                     }
                 });
                 current_user_db.child(uniqueId).child("commenter").setValue(mAuth.getCurrentUser().getUid());
                 DatabaseReference current_user_db2 = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("comments").child(uniqueId).child("comment");
                 current_user_db2.setValue(comment);
                 DatabaseReference current_user_db3 = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("comments").child(uniqueId).child("postID");
                 current_user_db3.setValue(postId);
                 mSpeak.setText("");
                 mSpeak.setFocusable(false);
             }
         });

        DatabaseReference postUser = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("posterUserId");
        postUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    user = snapshot.getValue(String.class);
                    userNamePost.setText(user);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mShowHide.setText("Show comment ▼");
        mShowHide.setVisibility(View.GONE);

        mCommentShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(Post_id.getText().toString()).child("comments");
                userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            i=0;
                            for (DataSnapshot posts : snapshot.getChildren()){
                                i++;
                            }
                            mShowHide.setText("Show comment ("+i+")▼");
                        }else{
                            mShowHide.setText("Show comment (0)▼");
                        }
                        i=0;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                if (mShowHide.getVisibility()==VISIBLE){
                    mShowHide.setVisibility(View.GONE);
                }else{
                    mShowHide.setVisibility(VISIBLE);
                }
            }
        });


        mLike.setOnClickListener(v -> {
            String user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            String postId = Post_id.getText().toString();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("likes");
            ref.child("likers").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        // user already followed
                        // unfollow
                        ref.child("likers").child(user_id).removeValue();
                        DatabaseReference current_user_db9 = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("likes");
                        if (likes==null){
                            current_user_db9.child("number").setValue(0);
                        }else{
                            current_user_db9.child("number").setValue(likes-1);
                        }
                        testLike = true;
                        mLike.setImageResource(R.drawable.likep);

                    } else {
                        // User does not exist. NOW call createUserWithEmailAndPassword
                        // Your previous code here.
                        DatabaseReference current_user_db7 = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("likes").child("likers");
                        current_user_db7.child(user_id).setValue("no");
                            DatabaseReference current_user_db8 = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("likes");
                            if (likes < 1) {
                                current_user_db8.child("number").setValue(1);
                            }else{
                                current_user_db8.child("number").setValue(likes+1);
                            }
                            mLike.setImageResource(R.drawable.liked);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        });


    }

    @Override
    public void onClick(View v) {

    }
}
