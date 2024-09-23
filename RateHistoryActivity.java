package com.yahya.shadow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yahya.shadow.CommentRecyclerView.CommentAdapter;
import com.yahya.shadow.CommentRecyclerView.CommentObject;
import com.yahya.shadow.MyRatingHistory.RatingAdapter;
import com.yahya.shadow.MyRatingHistory.RatingObject;
import com.yahya.shadow.UsersRecyclerView.UserObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RateHistoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView.LayoutManager mRatingsLayoutManager;
    private RecyclerView.Adapter mRatingsAdapter;
    private String user_id,currentUserId,post_id;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private DatabaseReference mDatabase;
    private TextView mNoComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_history);
        //posts recyclerView
        RecyclerView mRatingsRecyclerView = (RecyclerView) findViewById(R.id.ratings);
        mRatingsRecyclerView.setNestedScrollingEnabled(false);
        mRatingsRecyclerView.setHasFixedSize(true);
        mRatingsLayoutManager = new LinearLayoutManager(this);
        mRatingsRecyclerView.setLayoutManager(mRatingsLayoutManager);
        mRatingsAdapter = new RatingAdapter(getDataSetRatings(), this);
        mRatingsRecyclerView.setAdapter(mRatingsAdapter);
        
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mNoComments = findViewById(R.id.noRatingsFound);

        mNoComments.setVisibility(View.GONE);

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperRH);
        mSwipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.purple_700,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        

        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getRatingsIds();
        resultsRatings.clear();

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

    private void getRatingsIds() {
        resultsRatings.clear();
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ratings");
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot rater : snapshot.getChildren()){
                        FetchCommentInformations(rater.getKey());
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

    private void FetchCommentInformations(String rater_id) {
        resultsRatings.clear();
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        userDatabase.child(rater_id).child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String user = snapshot.getValue(String.class);
                    userDatabase.child(rater_id).child("rated").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                for (DataSnapshot rated : snapshot.getChildren()){
                                    String test = rated.getKey();
                                    assert test != null;
                                    if (test.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        userDatabase.child(rater_id).child("rated").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("value").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    float value = snapshot.getValue(float.class);
                                                    RatingObject obj = new RatingObject(user,value,rater_id);
                                                    resultsRatings.add(obj);
                                                    mRatingsAdapter.notifyDataSetChanged();
                                                    mSwipeRefreshLayout.setRefreshing(false);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
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
        getRatingsIds();
        resultsRatings.clear();


    }
    private ArrayList resultsRatings = new ArrayList<RatingObject>();
    private ArrayList<RatingObject> getDataSetRatings() {
        return resultsRatings;
    }
}