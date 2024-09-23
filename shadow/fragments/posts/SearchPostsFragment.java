package com.yahya.shadow.fragments.posts;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yahya.shadow.RequestSeaActivity;
import com.yahya.shadow.Auxilairies.ProgressBarAnimation;
import com.yahya.shadow.R;
import com.yahya.shadow.RequestFriendsActivity;
import com.yahya.shadow.callingRelatedActivities.RandomCallActivity;
import com.yahya.shadow.map.TestingMapsActivity;
import com.yahya.shadow.rank.RankingActivity;

import java.util.concurrent.TimeUnit;

public class SearchPostsFragment extends Fragment {

    private ProgressBar nSearchLeft,mTimeLeftProgress;
    private long deadlineInt,difference;
    private TextView mMonitorSearchPts,mTimeLeft,mGoodLuck,mRank,mHours,mMinutes,mDays,mSeconds;
    private int progress1,animation1,animation,q,value;
    private Handler handler1,handler;
    private CardView mCallCenter,mRank_cv,mPoints_cv,mRqReceived,mRqSent;

    private ImageView mRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_posts, container, false);

        nSearchLeft = view.findViewById(R.id.search_left);
        mMonitorSearchPts = view.findViewById(R.id.monitorSearchPts);
        mTimeLeft = view.findViewById(R.id.timeLeft);
        mTimeLeftProgress = view.findViewById(R.id.time_left);
        mGoodLuck = view.findViewById(R.id.goodLuck);
        mRank = view.findViewById(R.id.rank);
        mCallCenter = view.findViewById(R.id.callCenter);

        mDays = view.findViewById(R.id.days);
        mHours = view.findViewById(R.id.hours);
        mMinutes = view.findViewById(R.id.minutes);
        mSeconds = view.findViewById(R.id.seconds);

        mRequest = view.findViewById(R.id.fish);

        mGoodLuck.setVisibility(View.VISIBLE);

        handler = new Handler();
        handler1 = new Handler();

        q=1;

        mRank.setVisibility(View.VISIBLE);
        mRank_cv = view.findViewById(R.id.Rank_cv);
        mRank_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RankingActivity.class);
                startActivity(intent);
            }
        });
        mPoints_cv = view.findViewById(R.id.points_cv);
        mPoints_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMonitorSearchPts.getVisibility()==View.VISIBLE){
                    mMonitorSearchPts.setVisibility(View.INVISIBLE);
                }else{
                    mMonitorSearchPts.setVisibility(View.VISIBLE);
                }
            }
        });
        mRqReceived = view.findViewById(R.id.rq_received);
        mRqReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TestingMapsActivity.class);
                intent.putExtra("receivedORsent", "received");
                startActivity(intent);
            }
        });
        mRqSent = view.findViewById(R.id.rq_sent);
        mRqSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RequestSeaActivity.class);
                intent.putExtra("receivedORsent", "sent");
                startActivity(intent);
            }
        });


        //PUT RandomCallActivity.class INSTEAD OF UselessActivity.class
        mCallCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(), RandomCallActivity.class); //to be back
                Intent intent = new Intent(getActivity(), RandomCallActivity.class);//to be removed
                startActivity(intent);
            }
        });

        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference FollowingDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                FollowingDatabase.child("searchPts").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            value = snapshot.getValue(Integer.class);
                            if (value>=1){
                                Intent intent = new Intent(getActivity(), RequestFriendsActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getActivity(), "Not enough search points", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        //requests recyclerView
        backup();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                for (animation=1;!(animation==0);animation++){
                    try {
                        Thread.sleep(20);
                        //backup();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }

            }
        });
        thread.start();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (animation1=1;!(animation1==0);animation1++){
                    try {
                        Thread.sleep(10);
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


        DatabaseReference reputation = FirebaseDatabase.getInstance().getReference().child("users");
        reputation.orderByChild("reputation").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot users : snapshot.getChildren()){
                        if (users.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                           mRank.setText("#"+ String.valueOf(q));
                        }else{
                            q++;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void backup() {
        DatabaseReference value = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("searchPts");
        value.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    progress1 = snapshot.getValue(Integer.class);
                    mMonitorSearchPts.setText(String.valueOf(progress1));

                    nSearchLeft.setMax(10000);
                    ProgressBarAnimation anim = new ProgressBarAnimation(nSearchLeft, 0, progress1*300);
                    anim.setDuration(1000);
                    nSearchLeft.startAnimation(anim);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void update() {
        DatabaseReference deadline = FirebaseDatabase.getInstance().getReference().child("WeeklyReward").child("deadline");
        deadline.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    deadlineInt = snapshot.getValue(Integer.class);
                    Long timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                    difference = deadlineInt-timestamp;
                    if (!(difference<=0)){
                        long days = (difference/3600)/24;
                        long hours = (difference%(3600*24))/3600;
                        long minutes = (difference%3600)/60;
                        long seconds = difference%60;

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
                        mGoodLuck.setText("till sending gifts ...");
                    }else {
                        mGoodLuck.setText("Gifts are getting ready to be sent!!!");
                        //mGoodLuck.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}