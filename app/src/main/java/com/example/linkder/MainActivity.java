package com.example.linkder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private Intent intent;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("Preference", Context.MODE_PRIVATE);
        intent = prefs.getBoolean("logueado", false) ? new Intent(MainActivity.this,HomeActivity.class)
                : new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();

    }
}
