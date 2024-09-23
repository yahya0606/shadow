package com.yahya.shadow;

import static android.graphics.Color.argb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yahya.shadow.callingRelatedActivities.FetchingIncomingCalls;
import com.yahya.shadow.services.NotificationService;

import java.util.Random;

public class WelcomeActivity extends AppCompatActivity {

    private ProgressBar mProgress;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private TextView mPercentage,mTitle;
    private String percentage;
    private RelativeLayout mYellowScreen;

    private Integer progress, random;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mTitle = findViewById(R.id.title);
        mPercentage = findViewById(R.id.percentage);
        mProgress = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        handler = new Handler();
        progress = 0;
        //if (progress == null) {
        //    percentage = "0";
        //} else {
            percentage = Integer.toString(progress);

        //}

        mPercentage.setText(percentage + "%");
        random = new Random().nextInt(20);

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    //animation
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (progress=0;!(progress==100);progress++){
                                try {
                                    Thread.sleep(30);
                                    mProgress.setProgress(progress);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mPercentage.setText(String.valueOf(progress)+"%");
                                        if (progress==100){
                                            //startService(new Intent(this, NotificationService.class));
                                            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                                            intent.putExtra("current", 1);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }

                        }
                    });
                    thread.start();
                }else{

                    //animate
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (progress=0;!(progress==100);progress++){
                                try {
                                    Thread.sleep(60);
                                    mProgress.setProgress(progress);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mPercentage.setText(String.valueOf(progress)+"%");
                                        if (progress==100){
                                            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                                            finish();
                                        }
                                    }
                                });
                            }

                        }
                    });
                    thread.start();
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

}