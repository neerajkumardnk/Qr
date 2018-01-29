package com.example.reach.qr;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private CircleImageView circleImageViewProfilePic;
    public TextView  ProfileName, Optns;
    private String userId;
    private MaterialFancyButton signOut;
    private ProgressBar progressBarProfileLoading;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private ImageButton Add, Send, Receive, See;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();
        userId= auth.getCurrentUser().getUid().trim();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Optns=(TextView)findViewById(R.id.yourItemsTextView);
        See=(ImageButton)findViewById(R.id.profileSee);
        Add=(ImageButton)findViewById(R.id.profileAdd);
        Send=(ImageButton)findViewById(R.id.profileSend);
        Receive=(ImageButton)findViewById(R.id.profileReceived);
        progressBarProfileLoading=(ProgressBar)findViewById(R.id.progressBarProfileLoading);
        signOut=(MaterialFancyButton)findViewById(R.id.btn_previewLogout);
        ProfileName=(TextView)findViewById(R.id.profileName);
        circleImageViewProfilePic=(CircleImageView)findViewById(R.id.profile_Pic);
        progressBarProfileLoading.setVisibility(View.VISIBLE);

        authListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this,Login.class));
                    finish();
                }
            }
        };

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
            }
        });
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Add Your Items",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,Add.class));
                finish();
            }
        });
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Requests Sent",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,Send.class));
                finish();
            }
        });
        Receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Requests Received",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,Receive.class));
                finish();
            }
        });
        See.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"View Your Items",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,See.class));
                finish();
            }
        });
        firebaseFirestore.collection("Users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userName=documentSnapshot.getString("name");
                String userImage=documentSnapshot.getString("image");
                ProfileName.setText(userName);
                RequestOptions placeHolderOption=new RequestOptions();
                placeHolderOption.placeholder(R.mipmap.ic_account_circle_black_48dp);
                Glide.with(getApplicationContext()).setDefaultRequestOptions(placeHolderOption).load(userImage).into(circleImageViewProfilePic);

                progressBarProfileLoading.setVisibility(View.GONE);
                Optns.setVisibility(View.VISIBLE);
                Add.setVisibility(View.VISIBLE);
                Send.setVisibility(View.VISIBLE);
                Receive.setVisibility(View.VISIBLE);
                See.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

    }
}
