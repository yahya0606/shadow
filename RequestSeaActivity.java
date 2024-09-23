package com.yahya.shadow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.yahya.shadow.rquestsRecyclerView.RequestsAdapter;
import com.yahya.shadow.rquestsRecyclerView.RequestsObject;

import java.util.ArrayList;

public class RequestSeaActivity extends AppCompatActivity {

    private RecyclerView.Adapter mRequestAdapter;
    private RecyclerView.LayoutManager mRequestsLayoutManager;
    private String receivedORsent;
    private SlidrInterface slidr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_sea);
        slidr = Slidr.attach(this);
        findViewById(R.id.noRequests).setVisibility(View.GONE);

        Bundle user = getIntent().getExtras();
        if (user!=null){
            receivedORsent = user.getString("receivedORsent");
        }

        //requests recyclerView
        RecyclerView mRequestsRecyclerView = (RecyclerView) findViewById(R.id.requests_r);
        mRequestsRecyclerView.setNestedScrollingEnabled(false);
        mRequestsRecyclerView.setHasFixedSize(true);
        mRequestsLayoutManager = new GridLayoutManager(RequestSeaActivity.this,1,RecyclerView.HORIZONTAL,false);
        mRequestsRecyclerView.setLayoutManager(mRequestsLayoutManager);
        mRequestAdapter = new RequestsAdapter(getDataSetRequests(), RequestSeaActivity.this);
        mRequestsRecyclerView.setAdapter(mRequestAdapter);

        if (!(receivedORsent.isEmpty())){
            if (receivedORsent.equals("received")){
                getRequestsReceived();
            }else{
                getRequestsSent();
            }
        }
    }
    //requests received
    private void getRequestsReceived() {
        DatabaseReference requests = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        requests.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("requests received").exists()){
                    for (DataSnapshot req : snapshot.child("requests received").getChildren()){
                        fetchRequestDetails(req.getKey());
                    }
                }else{
                    findViewById(R.id.noRequests).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchRequestDetails(String requestId) {
        DatabaseReference requests = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("requests received").child(requestId);
        requests.child("sender").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String requester = snapshot.getValue(String.class);
                    requests.child("message").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String message = snapshot.getValue(String.class);
                            RequestsObject obj = new RequestsObject(requester,message,requestId);
                            resultsRequests.add(obj);
                            mRequestAdapter.notifyDataSetChanged();
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
    //requests sent
    private void getRequestsSent() {
        DatabaseReference requests = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("requests sent");
        requests.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot req : snapshot.getChildren()){
                        fetchRequestSentDetails(req.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchRequestSentDetails(String requestId) {
        DatabaseReference requests = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("requests sent").child(requestId);
        requests.child("sender").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String requester = snapshot.getValue(String.class);
                    requests.child("message").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String message = snapshot.getValue(String.class);
                            RequestsObject obj = new RequestsObject(requester,message,requestId);
                            resultsRequests.add(obj);
                            mRequestAdapter.notifyDataSetChanged();
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

    private ArrayList resultsRequests = new ArrayList<RequestsObject>();
    private ArrayList<RequestsObject> getDataSetRequests() {
        return resultsRequests;
    }
}