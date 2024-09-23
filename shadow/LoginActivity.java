package com.yahya.shadow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yahya.shadow.services.NotificationService;

import java.util.Objects;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail,mPass,mUser;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = findViewById(R.id.email);
        mUser = findViewById(R.id.username);
        Button mLogin = findViewById(R.id.login);
        mPass = findViewById(R.id.pass);
        TextView mSignUp = findViewById(R.id.signup);

        Switch mShow = findViewById(R.id.show);
        LinearLayout mLinear = findViewById(R.id.linear);

        mLinear.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    startService(new Intent(LoginActivity.this, NotificationService.class));
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("current", 1);
                    startActivity(intent);
                }
            }
        };

        mShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // hide password
                    mPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // show password
                    mPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        mLogin.setClickable(true);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this,"Please wait","Logging In",true);

                mAuth.signInWithEmailAndPassword(mEmail.getText().toString(),mPass.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "1", Toast.LENGTH_SHORT).show();
                                if(task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this,"Login successful",Toast.LENGTH_LONG).show();
                                    startService(new Intent(LoginActivity.this, NotificationService.class));
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("position", 2);
                                    startActivity(intent);

                                }else{
                                    Log.e("ERROR", Objects.requireNonNull(task.getException()).toString());
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    Toast.makeText(LoginActivity.this,"Forgot your password ??",Toast.LENGTH_LONG).show();
                                    mLinear.setVisibility(View.VISIBLE);

                                }
                            }
                        });
            }
        });
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this,"Please wait","Creating Account",true);

                mAuth.createUserWithEmailAndPassword(mEmail.getText().toString(), mPass.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(LoginActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
                                } else {
                                    //saving data
                                    //email
                                    String user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
                                    current_user_db.child("email").setValue(mEmail.getText().toString());
                                    //username
                                    DatabaseReference current_user_db6 = FirebaseDatabase.getInstance().getReference().child("userIds");
                                    current_user_db6.child(user_id).setValue(mUser.getText().toString());
                                    //user
                                    DatabaseReference current_user_db1 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
                                    current_user_db1.child("user").setValue(mUser.getText().toString());
                                    //country
                                    DatabaseReference current_user_db2 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
                                    current_user_db2.child("country").setValue("Not Mentioned");
                                    //bio
                                    DatabaseReference current_user_db3 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
                                    current_user_db3.child("bio").setValue("No Bio Yet!");
                                    //points
                                    DatabaseReference current_user_db4 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
                                    current_user_db4.child("points").setValue(0);
                                    //reputation
                                    DatabaseReference current_user_db5 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
                                    current_user_db5.child("reputation").setValue(0);
                                    //privacy
                                    DatabaseReference current_user_db7 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
                                    current_user_db7.child("privacy").setValue("no");
                                    //privacy
                                    DatabaseReference current_user_db8 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
                                    current_user_db8.child("privacyEmail").setValue("no");
                                    //moving on
                                    startService(new Intent(LoginActivity.this, NotificationService.class));
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("position", 2);
                                    startActivity(intent);
                                }
                            }
                        });
            }
        });

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