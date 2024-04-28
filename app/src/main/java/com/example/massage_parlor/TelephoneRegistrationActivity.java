package com.example.massage_parlor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                String phoneNumber = editPhone.getText().toString();

                // Validate the phone number using a regular expression
                if (phoneNumber.matches("\\+375\\d{9}")) {
                    Intent intent = new Intent(TelephoneRegistrationActivity.this, RegistrationActivity.class);
                    intent.putExtra("phone", phoneNumber);
                    startActivity(intent);
                    finish();
                } else {
                    //Display an error message if the phone number is not in the correct format
                    Toast.makeText(TelephoneRegistrationActivity.this, "Invalid phone number format. Please enter in the format +375xxxxxxxxx", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}

