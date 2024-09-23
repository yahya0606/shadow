package com.yahya.shadow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.squareup.picasso.Picasso;
import com.yahya.shadow.MessageRecyclerView.MsgAdapter;
import com.yahya.shadow.MessageRecyclerView.MsgObject;
import com.yahya.shadow.callingRelatedActivities.CallingActivity;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TextingActivity extends AppCompatActivity {
    private RecyclerView mMsgsRecyclerView;
    private RecyclerView.LayoutManager mMsgsLayoutManager;
    private RecyclerView.Adapter mMsgsAdapter;

    private LinearLayout mNewUserLayout;

    private String user_id,uniqueChatId,uniqueMsgId,CurrentChat,receiver;
    private ImageView mProfile,mSend,mCall,mVcall,mRate,mImageSend;
    private EditText msg;
    private TextView friendUsername;
    private String test;
    private Boolean isFriend;
    private Button mBlock,mAccept,mDelete;

    private SlidrInterface slidr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texting);

        mProfile = findViewById(R.id.msg_receiver_pic);
        friendUsername = findViewById(R.id.msg_receiver_name);
        msg = findViewById(R.id.msg);
        mImageSend = findViewById(R.id.image_msg);
        mSend = findViewById(R.id.send);
        mCall = findViewById(R.id.call);
        mVcall = findViewById(R.id.Vcall);
        mRate = findViewById(R.id.rate);

        mNewUserLayout = findViewById(R.id.newUserLayout);
        mNewUserLayout.setVisibility(View.GONE);

        mAccept = findViewById(R.id.accept_friend);
        mBlock = findViewById(R.id.block_friend);
        mDelete = findViewById(R.id.decline_friend);

        isFriend = false;

        slidr = Slidr.attach(this);

        Bundle user = getIntent().getExtras();

        if (user!=null){
            user_id = user.getString("userId");
        }

        //check if hes a friend or a requester
        checkFriendship();
        //top right buttons
        mRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TextingActivity.this, RateActivity.class);
                Intent intent1 = new Intent(v.getContext(), RateActivity.class);
                Bundle b = new Bundle();
                b.putString("userId",user_id);
                intent.putExtras(b);
                v.getContext().startActivity(intent1);
                startActivity(intent);
            }
        });
        mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TextingActivity.this, CallingActivity.class);
                Bundle b = new Bundle();
                b.putString("userId",user_id);
                b.putString("Call_type","voice call");
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        mVcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TextingActivity.this, CallingActivity.class);
                Bundle b = new Bundle();
                b.putString("userId",user_id);
                b.putString("Call_type","video call");
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        //new friend request
        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference friend = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                friend.child("following").child(user_id).setValue("no");
                friend.child("friend requests").child(user_id).removeValue();
                mNewUserLayout.setVisibility(View.GONE);
            }
        });
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference friend = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                friend.child("friend requests").child(user_id).removeValue();
                mNewUserLayout.setVisibility(View.GONE);
                Intent intent = new Intent(TextingActivity.this, MainActivity.class);
                Bundle b = new Bundle();
                b.putInt("current",0);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        mBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference friend = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                friend.child("blocked accounts").child(user_id).setValue("yes");
                friend.child("friend requests").child(user_id).removeValue();
                mNewUserLayout.setVisibility(View.GONE);
                Intent intent = new Intent(TextingActivity.this, MainActivity.class);
                Bundle b = new Bundle();
                b.putInt("current",0);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        mMsgsRecyclerView = (RecyclerView) findViewById(R.id.msgs);
        mMsgsRecyclerView.setNestedScrollingEnabled(false);
        mMsgsRecyclerView.setHasFixedSize(true);
        mMsgsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMsgsRecyclerView.setLayoutManager(mMsgsLayoutManager);
        mMsgsAdapter = new MsgAdapter(getDataSetMsgs(), this);
        mMsgsRecyclerView.setAdapter(mMsgsAdapter);

        //profile
        DatabaseReference userProfile = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        userProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String user = snapshot.child("user").getValue(String.class);
                    friendUsername.setText(user);
                    String profile = snapshot.child("profile").getValue(String.class);
                    Picasso.get().load(profile).into(mProfile);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //populating messages
        getChatIds();
        test = "";
        checking();
        //send msg
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uniqueMsgId = UUID.randomUUID().toString();
                //DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("chats");
                //current_user_db.child(CurrentChat).child(uniqueMsgId).setValue("done");
                DatabaseReference current_user_db1 = FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentChat).child("messages").child(uniqueMsgId).child("message");
                current_user_db1.setValue(msg.getText().toString());
                DatabaseReference current_user_db2 = FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentChat).child("messages").child(uniqueMsgId).child("sender");
                current_user_db2.setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                DatabaseReference current_user_db3 = FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentChat).child("messages").child(uniqueMsgId).child("receiver");
                current_user_db3.setValue(user_id);

                DatabaseReference current_user_notif1 = FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentChat).child("messages").child(uniqueMsgId).child("delivered");
                current_user_notif1.setValue("no");
                DatabaseReference current_user_notif2 = FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentChat).child("messages").child(uniqueMsgId).child("notified");
                current_user_notif2.setValue("no");
                DatabaseReference current_user_notif3 = FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentChat).child("messages").child(uniqueMsgId).child("seen");
                current_user_notif3.setValue("no");
                //timestamp
                String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                DatabaseReference current_user_db4 = FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentChat).child("messages").child(uniqueMsgId).child("timestamp");
                current_user_db4.setValue(timeStamp);

                //Saving last message
                DatabaseReference current_user_db5 = FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentChat).child("infos").child("lastMsg");
                current_user_db5.setValue(msg.getText().toString());
                DatabaseReference current_user_db6 = FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentChat).child("infos").child("sender");
                current_user_db6.setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                DatabaseReference current_user_db8 = FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentChat).child("infos").child("seen");
                current_user_db8.setValue("false");

                DatabaseReference current_user_notif4 = FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentChat).child("messages").child(uniqueMsgId).child("delivered");
                current_user_notif4.setValue("no");
                DatabaseReference current_user_notif5 = FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentChat).child("messages").child(uniqueMsgId).child("notified");
                current_user_notif5.setValue("no");
                DatabaseReference current_user_notif6 = FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentChat).child("messages").child(uniqueMsgId).child("seen");
                current_user_notif6.setValue("no");
                //timestamp
                DatabaseReference current_user_db7 = FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentChat).child("infos").child("timestamp");
                current_user_db7.setValue(timeStamp);

                MsgObject obj = new MsgObject(FirebaseAuth.getInstance().getCurrentUser().getUid(),user_id,msg.getText().toString());
                resultsMsgs.add(obj);
                mMsgsLayoutManager.scrollToPosition(resultsMsgs.size()-1);
                msg.setText(null);
                mMsgsAdapter.notifyDataSetChanged();
            }
        });
    }

    private void checkFriendship() {
        DatabaseReference userIdDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userIdDatabase.child("following").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot friends : snapshot.getChildren()){
                        if (friends.getKey().equals(user_id)){
                            isFriend=true;
                            break;
                        }else{
                            isFriend = false;
                        }
                    }

                }
                //isFriend=false;
                if (!isFriend){
                    mNewUserLayout.setVisibility(View.VISIBLE);
                }else {
                    mNewUserLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getChatIds() {
        resultsMsgs.clear();
        DatabaseReference userIdDatabase = FirebaseDatabase.getInstance().getReference().child("chats");
        userIdDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot chats : snapshot.getChildren()){
                        String Chat1 = (FirebaseAuth.getInstance().getCurrentUser().getUid()+" with "+user_id);
                        String Chat2 = (user_id+" with "+FirebaseAuth.getInstance().getCurrentUser().getUid());
                        String chatTest = chats.getKey();
                        assert chatTest != null;
                        if (chatTest.equals(Chat1) || chatTest.equals(Chat2)){
                            FetchMsgs(chats.getKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void FetchMsgs(String ChatID) {
        DatabaseReference userIdDatabase = FirebaseDatabase.getInstance().getReference().child("chats").child(ChatID).child("messages");
        userIdDatabase.orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot Msgs : snapshot.getChildren()){
                        FetchMsgInformations(Msgs.getKey(),ChatID);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void FetchMsgInformations(String MsgId, String chatID) {
        DatabaseReference userIdDatabase = FirebaseDatabase.getInstance().getReference().child("chats").child(chatID).child("messages").child(MsgId);
        userIdDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    DatabaseReference current_user_notif1 = FirebaseDatabase.getInstance().getReference().child("chats").child(chatID).child("messages").child(MsgId).child("delivered");
                    current_user_notif1.setValue("yes");
                    DatabaseReference current_user_notif3 = FirebaseDatabase.getInstance().getReference().child("chats").child(chatID).child("messages").child(MsgId).child("seen");
                    current_user_notif3.setValue("yes");

                    String sender = snapshot.child("sender").getValue(String.class);
                    String receiver = snapshot.child("receiver").getValue(String.class);
                    String messsage = snapshot.child("message").getValue(String.class);
                    MsgObject obj = new MsgObject(sender,receiver,messsage);
                    resultsMsgs.add(obj);
                    mMsgsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //checking if a convo already took place
    private void checking() {
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("chats");
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot chats : snapshot.getChildren()){
                        String Chat1 = (FirebaseAuth.getInstance().getCurrentUser().getUid()+" with "+user_id);
                        String Chat2 = (user_id+" with "+FirebaseAuth.getInstance().getCurrentUser().getUid());
                        String chatTest = chats.getKey();
                        assert chatTest != null;
                        if (chatTest.equals(Chat1) || chatTest.equals(Chat2)){
                            CurrentChat = chatTest;
                        }else{
                            CurrentChat = (FirebaseAuth.getInstance().getCurrentUser().getUid()+" with "+user_id);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private ArrayList<MsgObject> resultsMsgs = new ArrayList<MsgObject>();
    private ArrayList<MsgObject> getDataSetMsgs() {
        return resultsMsgs;
    }

}