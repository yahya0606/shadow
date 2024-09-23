package com.yahya.shadow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostingActivity extends AppCompatActivity {

    private String uniquePostId,user_id,userName0;
    private EditText mDescription,mTag;
    private Button mShare;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private int i;
    private Handler handler;

    private DatabaseReference mPostDatabase;
    private Switch mPicTextSwitch;
    private CardView mTextCard,mPicCard;

    private ImageView postPic;
    private Uri imageUri;
    private String myUri;
    private StorageTask uploadTask;
    private StorageReference storageProfilePicRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        handler = new Handler();

        mDescription = findViewById(R.id.caption);
        mTag = findViewById(R.id.tag);
        mShare = findViewById(R.id.share);
        mTextCard = findViewById(R.id.TextCard);
        mPicCard = findViewById(R.id.PicCard);
        mPicTextSwitch = findViewById(R.id.PicTextSwitch);

        mAuth = FirebaseAuth.getInstance();
        user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();


        postPic = findViewById(R.id.post_pic);
        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("posts");
        storageProfilePicRef = FirebaseStorage.getInstance().getReference().child("Post Pics");

        mPicTextSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    mTextCard.setVisibility(View.GONE);
                    mPicCard.setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    mTextCard.setVisibility(View.VISIBLE);
                    mPicCard.setVisibility(View.GONE);
                }
            }
        });

        DatabaseReference userName = FirebaseDatabase.getInstance().getReference().child("userIds").child(user_id);
        userName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String userName = snapshot.getValue(String.class);
                    userName0 = userName;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String uniqueID = UUID.randomUUID().toString();
        mAuth = FirebaseAuth.getInstance();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("posts");
        ref.child(uniqueID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    // user already followed
                    // unfollow
                    do {
                        uniquePostId = UUID.randomUUID().toString();
                        ref.child(uniquePostId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    Toast.makeText(PostingActivity.this,"Retrying",Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(PostingActivity.this,"error",Toast.LENGTH_LONG).show();

                            }
                        });
                    } while (snapshot.exists());
                    uniquePostId = uniqueID;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PostingActivity.this,"Eroror 1 ",Toast.LENGTH_LONG).show();

            }
        });

        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progress Dialog
                final ProgressDialog progressDialog = ProgressDialog.show(PostingActivity.this,"Please wait","Logging In",true);
                // User does not exist. NOW call createUserWithEmailAndPassword
                // Your previous code here.
                uniquePostId = uniqueID;
                //description
                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("posts").child(uniquePostId);
                current_user_db.child("description").setValue(mDescription.getText().toString());
                //tag
                DatabaseReference current_user_db6 = FirebaseDatabase.getInstance().getReference().child("posts").child(uniquePostId);
                current_user_db6.child("tag").setValue(mTag.getText().toString());
                //posterId
                DatabaseReference current_user_db1 = FirebaseDatabase.getInstance().getReference().child("posts").child(uniquePostId);
                current_user_db1.child("posterUserId").setValue(userName0);
                //likeCount
                DatabaseReference current_user_db2 = FirebaseDatabase.getInstance().getReference().child("posts").child(uniquePostId);
                current_user_db2.child("likes").setValue(0);
                //userId
                DatabaseReference current_user_db4 = FirebaseDatabase.getInstance().getReference().child("posts").child(uniquePostId);
                current_user_db4.child("posterId").setValue(user_id);
                //timestamp
                String timeStamp = String.valueOf(- TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                DatabaseReference current_user_db5 = FirebaseDatabase.getInstance().getReference().child("posts").child(uniquePostId);
                current_user_db5.child("timestamp").setValue(timeStamp);
                //likeCount
                DatabaseReference current_user_db3 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("postsUser");
                current_user_db3.child(uniqueID).setValue("nope");
                getPostPic();
                uploadPostPic();

                progressDialog.dismiss();

                Intent intent = new Intent(PostingActivity.this, MainActivity.class);
                Bundle b = new Bundle();
                b.putInt("current",1);
                intent.putExtras(b);
                Toast.makeText(PostingActivity.this,"Post published ",Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });
        postPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).start(PostingActivity.this);
            }
        });
    }



    private void getPostPic() {
        mPostDatabase.child(uniquePostId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() >0){
                    if (snapshot.hasChild("pic_id")){
                        String Image = snapshot.child("pic_id").getValue().toString();
                        Picasso.get().load(Image).into(postPic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            postPic.setImageURI(imageUri);
        }else{
            Toast.makeText(PostingActivity.this,"error",Toast.LENGTH_LONG).show();
        }
    }


    private void uploadPostPic() {

        if (imageUri!=null){
            final StorageReference fileRef = storageProfilePicRef.child(uniquePostId);

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task <Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri =task.getResult();
                        myUri = downloadUri.toString();

                        HashMap<String , Object> userMap = new HashMap<>();
                        userMap.put("post",myUri);

                        mPostDatabase.child(uniquePostId).child("pic_id").updateChildren(userMap);
                        Toast.makeText(PostingActivity.this, "Post SAVED!", Toast.LENGTH_LONG).show();

                    }
                }
            });
        }else{
            Toast.makeText(PostingActivity.this,"No Image Selected",Toast.LENGTH_LONG).show();
        }
    }
}