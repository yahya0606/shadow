package com.yahya.shadow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class RateActivity extends AppCompatActivity {

    private RatingBar mRating;
    private String user_id;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private TextView mDays,mHours,mMinutes,mSeconds;
    private Handler handler1;
    private int animation1,RateTime,i;
    private Button mRank;
    private long difference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        Bundle user = getIntent().getExtras();
        if (user!=null){
            user_id = user.getString("userId");
        }

        mDays = findViewById(R.id.days);
        mHours = findViewById(R.id.hours);
        mMinutes = findViewById(R.id.minutes);
        mSeconds = findViewById(R.id.seconds);
        handler1 = new Handler();


        mRating = findViewById(R.id.ratingBar);
        mRank = findViewById(R.id.rateb);


        mAuth = FirebaseAuth.getInstance();

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (animation1=1;!(animation1==0);animation1++){
                    try {
                        Thread.sleep(10);
                        if (difference<=0){
                            mRank.setText("Rate !");
                            mRank.setClickable(true);
                        }else{
                            mRank.setText("Locked");
                            mRank.setClickable(false);
                        }
                        update();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler1.post(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }

            }
        });
        thread1.start();

        mRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(difference>0)){
                    mRank.setText("Rate !");
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                    mDatabase.child(user_id).child("ratings").child(mAuth.getCurrentUser().getUid()).setValue(mRating.getRating());
                    mDatabase.child(mAuth.getCurrentUser().getUid()).child("rated").child(user_id).child("value").setValue(mRating.getRating());
                    mDatabase.child(mAuth.getCurrentUser().getUid()).child("rated").child(user_id).child("timestamp").setValue(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    mDatabase.child(user_id).child("reputation").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                float prevRate = snapshot.getValue(float.class);
                                if (prevRate == 0){
                                    mDatabase.child(user_id).child("reputation").setValue(mRating.getRating());
                                }else{
                                    mDatabase.child(user_id).child("reputation").setValue((mRating.getRating()+prevRate)/2);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    mRank.setText("Locked");
                    Toast.makeText(RateActivity.this, "You still have to wait", Toast.LENGTH_SHORT).show(); }
            }
        });
    }

    private void update() {
        DatabaseReference deadline = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("rated");
        deadline.child(user_id).child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    RateTime = snapshot.getValue(Integer.class);
                    Long timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                    difference = (604800-(timestamp-RateTime));
                    if (!(difference<0)){
                        long days = (difference/3600)/24;
                        long hours = (difference%(3600*24))/3600;
                        long minutes = (difference%3600)/60;
                        long seconds = (difference%60)-1;

                        float progress12 = difference;
                        String sProgressTime = String.valueOf((progress12/561600)*100);
                        float progresstime = Float.valueOf(sProgressTime);

                        if (days<10){
                            mDays.setText("0"+String.valueOf(days));
                        }else{
                            mDays.setText(String.valueOf(days));
                        }
                        if (hours<10){
                            mHours.setText("0"+String.valueOf(hours));
                        }else{
                            mHours.setText(String.valueOf(hours));
                        }
                        if (minutes<10){
                            mMinutes.setText("0"+String.valueOf(minutes));
                        }else{
                            mMinutes.setText(String.valueOf(minutes));
                        }
                        if (seconds<10){
                            mSeconds.setText("0"+String.valueOf(seconds));
                        }else{
                            mSeconds.setText(String.valueOf(seconds));
                        }

                        //String sDifference = String.valueOf(difference/10);

                    }else {
                        mRank.setClickable(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}