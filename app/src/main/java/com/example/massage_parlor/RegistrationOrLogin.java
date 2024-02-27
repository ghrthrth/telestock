package com.example.massage_parlor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrationOrLogin extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_or_login);

        Button reg_btn = findViewById(R.id.registration);
        Button auth_btn = findViewById(R.id.auth);
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
