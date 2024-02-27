package com.example.massage_parlor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegistrationActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);

        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra("phone"); // Retrieve the phone number from

        Button send_reg_data = findViewById(R.id.send_reg_data);

        EditText inputName = findViewById(R.id.input_name);
        EditText inputSurname = findViewById(R.id.unput_surname); // Corrected variable name
        EditText inputAddress = findViewById(R.id.input_adress); // Corrected variable name
        EditText inputLogin = findViewById(R.id.input_login_reg); // Corrected variable name
        EditText inputPassword = findViewById(R.id.input_pass_reg); // Corrected variable name
        CheckBox agreement = findViewById(R.id.checkBox);

        send_reg_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputName.getText().toString();
                String surname = inputSurname.getText().toString();
                String address = inputAddress.getText().toString();
                String login = inputLogin.getText().toString();
                String password = inputPassword.getText().toString();
                boolean isChecked = agreement.isChecked();

                if (name.isEmpty() || surname.isEmpty() || address.isEmpty() || login.isEmpty() || password.isEmpty() || !isChecked) {
                    // Display an error message if any field is empty or the checkbox is not checked
                    Toast.makeText(RegistrationActivity.this, "Please fill in all the fields and check the agreement", Toast.LENGTH_SHORT).show();
                } else {
                    new HttpRequestTask().execute(name, surname, address, login, password, phoneNumber);
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }

    private class HttpRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String surname = params[1];
            String address = params[2];
            String login = params[3];
            String password = params[4];
            String phone = params[4];

            JSONObject json = new JSONObject();

            try {
                json.put("name", name);
                json.put("surname", surname);
                json.put("address", address);
                json.put("login", login);
                json.put("password", password);
                json.put("phone", phone);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, json.toString());
            Request request = new Request.Builder()
                    .url("https://claimbe.store/massage_parlor/registration.php")
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                assert response.body() != null;
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResponse = new JSONObject(result);
                if (jsonResponse.has("success")) {
                    // Display a success message
                    Toast.makeText(RegistrationActivity.this, jsonResponse.getString("success"), Toast.LENGTH_SHORT).show();
                } else if (jsonResponse.has("error")) {
                    // Display an error message
                    Toast.makeText(RegistrationActivity.this, "Ошибка: " + jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                } else {
                    // Handle other cases or unknown response
                    Toast.makeText(RegistrationActivity.this, "Неизвестный ответ от сервера", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                // Handle JSON parsing error
                Toast.makeText(RegistrationActivity.this, "Ошибка при обработке ответа от сервера" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
}
