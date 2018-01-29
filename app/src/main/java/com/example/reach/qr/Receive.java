package com.example.reach.qr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Receive extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(Receive.this,MainActivity.class));
        finish();
    }
}
