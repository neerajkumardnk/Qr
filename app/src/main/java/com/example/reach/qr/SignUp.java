package com.example.reach.qr;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.libizo.CustomEditText;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private Uri ImageUri;
    private CustomEditText Email, Password, Name, Phone;
    private MaterialFancyButton SignUp;
    private CircleImageView circleImageViewButton;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBarSignUp;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ImageUri = null;
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressBarSignUp = (ProgressBar) findViewById(R.id.progressBarRegister);
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile_Images");
        firebaseAuth = FirebaseAuth.getInstance();
        Phone = (CustomEditText) findViewById(R.id.nameTextViewPhoneNumber);
        Email = (CustomEditText) findViewById(R.id.emailTextViewSignUp);
        Password = (CustomEditText) findViewById(R.id.pwTextViewSignUp);
        Name = (CustomEditText) findViewById(R.id.nameTextViewSignUp);
        SignUp = (MaterialFancyButton) findViewById(R.id.btn_previewSignUp);
        circleImageViewButton = (CircleImageView) findViewById(R.id.profile_image);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ImageUri != null) {
                    progressBarSignUp.setVisibility(View.VISIBLE);
                    final String email = Email.getText().toString().trim();
                    final String password = Password.getText().toString().trim();
                    final String name = Name.getText().toString().trim();
                    final String phone = Phone.getText().toString().trim();
                    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)) {
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                    final String user_id = firebaseAuth.getCurrentUser().getUid();
                                    final String Token = FirebaseInstanceId.getInstance().getToken();
                                    StorageReference userProfile = storageReference.child(user_id + ".jpeg");
                                    userProfile.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> uploadTask) {
                                            if (uploadTask.isSuccessful()) {
                                                String download_url = uploadTask.getResult().getDownloadUrl().toString();
                                                Map<String, Object> userMap = new HashMap<>();
                                                userMap.put("name", name);
                                                userMap.put("email", email);
                                                userMap.put("phone",phone);
                                                userMap.put("token",Token);
                                                userMap.put("image", download_url);

                                                firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getApplicationContext(), "Profile Image Uploaded", Toast.LENGTH_SHORT).show();
                                                        progressBarSignUp.setVisibility(View.INVISIBLE);
                                                        sendDataToFdb();
                                                        Intent intent = new Intent(com.example.reach.qr.SignUp.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();

                                                    }
                                                });


                                            } else {
                                                progressBarSignUp.setVisibility(View.INVISIBLE);
                                                Toast.makeText(getApplicationContext(), "Error : " + uploadTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                                } else {
                                    progressBarSignUp.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        if (TextUtils.isEmpty(email)) {
                            progressBarSignUp.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Enter email id", Toast.LENGTH_SHORT).show();
                        }
                        if (TextUtils.isEmpty(password)) {
                            progressBarSignUp.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
                        }
                        if (TextUtils.isEmpty(phone)){
                            progressBarSignUp.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Enter Phone Number", Toast.LENGTH_SHORT).show();
                        }
                        if (TextUtils.isEmpty(name)) {
                            progressBarSignUp.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Enter Name", Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Select Image", Toast.LENGTH_SHORT).show();

                }
            }
        });

        circleImageViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            ImageUri = data.getData();
            circleImageViewButton.setImageURI(ImageUri);
        }
    }

    public void sendDataToFdb() {

        String email = Email.getText().toString().trim();
        String name = Name.getText().toString().trim();
        String phone =Phone.getText().toString().trim();
        final String user_id = firebaseAuth.getCurrentUser().getUid().trim();
        final String Tokens = "Tokens";
        String Token = FirebaseInstanceId.getInstance().getToken();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference vehicleRef = database.getReference(""+user_id);
        vehicleRef.setValue("" + user_id);
        vehicleRef.child("email").setValue("" + email);
        vehicleRef.child("name").setValue("" + name);
        vehicleRef.child("phone").setValue("" +phone);
        vehicleRef.child("token").setValue("" + Token);
        DatabaseReference reference = database.getReference("" + Tokens);
        reference.child("" + user_id).setValue("" + Token);
        Toast.makeText(getApplicationContext(), "profile done", Toast.LENGTH_SHORT).show();


    }
}
