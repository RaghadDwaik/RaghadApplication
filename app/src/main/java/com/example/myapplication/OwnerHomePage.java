package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class OwnerHomePage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_home_page);

    }

    public void btn(View view) {
        String ownerId = getIntent().getStringExtra("ownerId");

        Intent intent = new Intent(OwnerHomePage.this, OwnerAddPlace.class);
        intent.putExtra("ownerId", ownerId);

        startActivity(intent);
    }

    public void btnshow(View view) {
        String ownerId = getIntent().getStringExtra("ownerId");

        Intent intent = new Intent(OwnerHomePage.this, ViewOwnerPlace.class);
        intent.putExtra("ownerId", ownerId);
        intent.putExtra("visited", true);
        startActivity(intent);
    }
}