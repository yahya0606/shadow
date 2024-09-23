package com.yahya.shadow.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yahya.shadow.MessageRecyclerView.MsgAdapter;
import com.yahya.shadow.MessageRecyclerView.MsgObject;
import com.yahya.shadow.R;

import java.util.ArrayList;

public class NotificationService extends Service {

    public static final  String CHANNEL_1_ID = "channel1";
    public static final  String CHANNEL_2_ID = "channel2";
    private NotificationManagerCompat notificationManagerCompat;
    private Handler handler;
    private int progress;
    private String sender,message,timestamp;
    private int i;
    private RecyclerView.Adapter mMsgsAdapter;

    @Override
    public void onCreate() {
        mMsgsAdapter = new MsgAdapter(getDataSetMsgs(), this);
        handler = new Handler();
        i=1;
        notificationManagerCompat = NotificationManagerCompat.from(this);
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is channel 1");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Channel 2",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel1.setDescription("This is channel 2");


            NotificationManager manager = this.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        //do something
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (progress = 0; (progress == progress); progress++) {
                    try {
                        Thread.sleep(2000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            FetchNewMsgs();
                            //Toast.makeText(getApplicationContext(), String.valueOf(progress), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        thread.start();
    }

    private void FetchNewMsgs() {
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("chats");
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot rater : snapshot.getChildren()){
                        if (rater.getKey().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            CheckIncomedMsgs(rater.getKey());
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CheckIncomedMsgs(String key) {
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("chats").child(key).child("messages");
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot rater : snapshot.getChildren()){
                        if (rater.hasChild("receiver")){
                            if (rater.child("receiver").getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                if (rater.hasChild("notified")){
                                    if (rater.child("notified").getValue().equals("no")){
                                        DatabaseReference userDatabase1 = FirebaseDatabase.getInstance().getReference().child("chats").child(key).child("messages").child(rater.getKey());
                                        userDatabase1.child("notified").setValue("yes");

                                        getMsgDetails(key,rater.getKey());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMsgDetails(String key, String key1) {
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("chats").child(key).child("messages").child(key1);
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    message = snapshot.child("message").getValue(String.class);
                    timestamp = snapshot.child("timestamp").getValue(String.class);
                    String userID = snapshot.child("sender").getValue(String.class);

                    DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("userIds").child(userID);
                    userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                sender = snapshot.getValue(String.class);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    NotifyUser(key,key1);
                    MsgObject obj = new MsgObject(sender,FirebaseAuth.getInstance().getCurrentUser().getUid(),message);
                    resultsMsgs.add(obj);
                    mMsgsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void NotifyUser(String key, String key1) {
        Notification notification = new NotificationCompat.Builder(this, NotificationService.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.account)
                .setContentTitle(sender)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        int notificationId = i;
        i++;
        this.notificationManagerCompat.notify(notificationId, notification);

        DatabaseReference userDatabase1 = FirebaseDatabase.getInstance().getReference().child("chats").child(key).child("messages").child(key1);
        userDatabase1.child("notified").setValue("yes");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private ArrayList<MsgObject> resultsMsgs = new ArrayList<MsgObject>();
    private ArrayList<MsgObject> getDataSetMsgs() {
        return resultsMsgs;
    }
}
