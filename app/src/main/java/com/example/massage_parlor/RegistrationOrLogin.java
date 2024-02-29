package com.example.massage_parlor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class RegistrationOrLogin extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_or_login);

        Button reg_btn = findViewById(R.id.registration);
        Button auth_btn = findViewById(R.id.auth);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("id", sharedPreferences.getString("id", ""));
        dataMap.put("name", sharedPreferences.getString("name", ""));
        dataMap.put("surname", sharedPreferences.getString("surname", ""));
        dataMap.put("address", sharedPreferences.getString("address", ""));
        dataMap.put("login", sharedPreferences.getString("login", ""));
        dataMap.put("phone", sharedPreferences.getString("phone", ""));
        dataMap.put("balance", sharedPreferences.getString("balance", ""));

        boolean isDataEmpty = dataMap.containsValue("");
        if (!isDataEmpty) {
            Intent intent = new Intent(RegistrationOrLogin.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(RegistrationOrLogin.this, "Войдите в лк заного!", Toast.LENGTH_SHORT).show();
        }

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationOrLogin.this, TelephoneRegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });
        auth_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationOrLogin.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
