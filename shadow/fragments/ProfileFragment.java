package com.yahya.shadow.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.yahya.shadow.MyRatingHistory.RatingObject;
import com.yahya.shadow.ProfileMakingActivity;
import com.yahya.shadow.R;
import com.yahya.shadow.RateHistoryActivity;
import com.yahya.shadow.VpAdapters.VpAdapter;
import com.yahya.shadow.VpAdapters.VpHomeAdapter;
import com.yahya.shadow.VpAdapters.VpProfile;
import com.yahya.shadow.fragments.posts.AllPostsFragment;
import com.yahya.shadow.fragments.posts.FriendPostsFragment;
import com.yahya.shadow.fragments.posts.SearchPostsFragment;
import com.yahya.shadow.fragments.profileOrganisator.ProfileDataFragment;
import com.yahya.shadow.fragments.profileOrganisator.ProfilePostsFragment;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private DatabaseReference mDatabase2,profileReference,mDatabase4,mDatabase5;

    private CircleImageView mProfile;
    private CardView mRateHistory;
    private StorageReference storageProfile;
    private TextView mRating;
    private float s,reputation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView mUsername = (TextView) view.findViewById(R.id.usertxt);
        TextView mPoints = view.findViewById(R.id.points);
        mRating = view.findViewById(R.id.thanks);
        mRateHistory = view.findViewById(R.id.RateHistory);
        mProfile = view.findViewById(R.id.profile);
        //filling the blanks
        mAuth = FirebaseAuth.getInstance();
        String user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        profileReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);

        //profile
        profileReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() >0){
                    if (snapshot.hasChild("profile")){
                        String Image = snapshot.child("profile").getValue().toString();
                        Picasso.get().load(Image).into(mProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //points
        mDatabase4 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("points");
        mDatabase4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer points = dataSnapshot.getValue(Integer.class);
                String poi = Integer.toString(points);
                mPoints.setText(poi);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error points !", Toast.LENGTH_SHORT).show();
            }
        });

        //reputation
        mDatabase5 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("reputation");
        mDatabase5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reputation = dataSnapshot.getValue(float.class);
                if (reputation<0) {
                    reputation = reputation * -1;
                }
                mRating.setText(String.valueOf(reputation));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error reputation !", Toast.LENGTH_SHORT).show();
            }
        });
        //username
        mDatabase2 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("user");
        mDatabase2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user = dataSnapshot.getValue(String.class);
                mUsername.setText(user);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Error user !",Toast.LENGTH_SHORT).show();
            }
        });

        mRateHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RateHistoryActivity.class);
                startActivity(intent);
            }
        });

        //TabLayout
        tabLayout = view.findViewById(R.id.Ptabs);
        viewPager = view.findViewById(R.id.Pviews);
        tabLayout.setupWithViewPager(viewPager);
        VpHomeAdapter vpAdapter = new VpHomeAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new ProfileDataFragment(),"Data");
        vpAdapter.addFragment(new ProfilePostsFragment(),"My Posts");
        viewPager.setAdapter(vpAdapter);
        viewPager.setCurrentItem(0);

        return view;
    }
}