package com.example.reach.qr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import de.hdodenhof.circleimageview.CircleImageView;


public class Add extends AppCompatActivity {
    private CircleImageView circleImageSelctObjectsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        circleImageSelctObjectsView=(CircleImageView)findViewById(R.id.ObjectSelectionImageView;
        circleImageSelctObjectsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(Add.this,MainActivity.class));
        finish();
    }
}
