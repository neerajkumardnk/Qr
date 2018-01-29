package com.example.reach.qr;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.libizo.CustomEditText;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

public class Login extends AppCompatActivity {

    public CustomEditText Password, Email;
    public MaterialFancyButton Submit ,SignUpButton;
    public ProgressBar progressBar;
    private FirebaseAuth firebaseAuth, auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_login);
        SignUpButton=(MaterialFancyButton)findViewById(R.id.btn_previewSignUp1);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLogin);
        Email = (CustomEditText) findViewById(R.id.emailTextViewLogin);
        Password = (CustomEditText) findViewById(R.id.pwTextViewLogin);
        Submit = (MaterialFancyButton) findViewById(R.id.btn_previewLogin);
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = Email.getText().toString().trim();
                final String password = Password.getText().toString();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> LoginTask) {

                            if (!LoginTask.isSuccessful()) {
                                if (password.length() < 6) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Password Length Should be grater than 6 characters", Toast.LENGTH_SHORT).show();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(Login.this, "Error " + LoginTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }

                            } else {

                                Toast.makeText(getApplicationContext(), "Login", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

                }

            }
        });


    }
}
