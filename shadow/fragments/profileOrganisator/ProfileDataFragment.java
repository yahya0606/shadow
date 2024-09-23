package com.yahya.shadow.fragments.profileOrganisator;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yahya.shadow.LoginActivity;
import com.yahya.shadow.ProfileMakingActivity;
import com.yahya.shadow.R;
import com.yahya.shadow.services.NotificationService;

import java.util.Objects;


public class ProfileDataFragment extends Fragment {

    private FirebaseAuth mAuth;
    private Switch mPrivacyEmail,mPrivacy;
    private DatabaseReference mDatabase,mDatabase1,mDatabase2,mDatabase3,profileReference,mDatabase4,mDatabase5,mDatabase6,mDatabase7;
    private TextView mEmail,mCountry,mUsername,mUser,mBio;
    private String user_id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_data, container, false);

        mEmail = (TextView) view.findViewById(R.id.emailtxt);
        mCountry = (TextView) view.findViewById(R.id.countrytxt);
        mUsername = (TextView) view.findViewById(R.id.usernametxt);
        mUser = (TextView) view.findViewById(R.id.usertxt);
        mBio = (TextView) view.findViewById(R.id.biotxt);

        mPrivacy = view.findViewById(R.id.privacy);
        mPrivacyEmail = view.findViewById(R.id.privacyEmail);



        //filling the blanks
        mAuth = FirebaseAuth.getInstance();
        user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        profileReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);

        //disabling edit txts
        mBio.setEnabled(false);
        mEmail.setEnabled(false);
        mCountry.setEnabled(false);
        mUsername.setEnabled(false);
        //disabling switches
        mPrivacy.setEnabled(false);
        mPrivacyEmail.setEnabled(false);

        //email
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("email");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String email = dataSnapshot.getValue(String.class);
                mEmail.setText(email);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Error email !",Toast.LENGTH_SHORT).show();
            }
        });
        //country
        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("country");
        mDatabase1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String country = dataSnapshot.getValue(String.class);
                mCountry.setText(country);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Error country !",Toast.LENGTH_SHORT).show();
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

        //bio
        mDatabase3 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("bio");
        mDatabase3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String bio = dataSnapshot.getValue(String.class);
                mBio.setText(bio);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Error bio !",Toast.LENGTH_SHORT).show();
            }
        });

        //privacy
        mDatabase6 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("privacy");
        mDatabase6.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String pri = dataSnapshot.getValue(String.class);
                if (pri.equals("no")){
                    mPrivacy.setChecked(false);
                }else{
                    mPrivacy.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error email !", Toast.LENGTH_SHORT).show();
            }
        });
        //privacyEmail
        mDatabase7 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("privacyEmail");
        mDatabase7.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String priEm = dataSnapshot.getValue(String.class);
                if (priEm.equals("no")){
                    mPrivacyEmail.setChecked(false);
                }else{
                    mPrivacyEmail.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error email !", Toast.LENGTH_SHORT).show();
            }
        });

        //logging out
        ImageButton mLogout = (ImageButton) view.findViewById(R.id.logout);
        mLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FirebaseAuth.getInstance().signOut();
                getActivity().stopService(new Intent(getContext(), NotificationService.class));
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        //edit data
        Button mEditPro = (Button) view.findViewById(R.id.Edit);
        mEditPro.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), ProfileMakingActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }


}