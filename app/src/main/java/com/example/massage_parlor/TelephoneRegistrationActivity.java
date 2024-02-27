package com.example.massage_parlor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class TelephoneRegistrationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telephone_registration_activity);

        Button testButton = findViewById(R.id.test);
        EditText editPhone = findViewById(R.id.editTextPhone);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = editPhone.getText().toString(); // Get the text from the EditText
                Intent intent = new Intent(TelephoneRegistrationActivity.this, RegistrationActivity.class);
                intent.putExtra("phone", phoneNumber); // Pass the phone number as an extra with the intents
                startActivity(intent);
                finish();
            }
        });
    }

}

