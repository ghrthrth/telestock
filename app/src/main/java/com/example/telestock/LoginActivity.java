package com.example.telestock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity  extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        Button auth_btn_continue = findViewById(R.id.auth_btn_continue);

        EditText login = findViewById(R.id.login);
        EditText pass = findViewById(R.id.password);
        auth_btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = login.getText().toString();
                String password = pass.getText().toString();

                OkHttpClient client = new OkHttpClient();

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username", username);
                    jsonObject.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                Request request = new Request.Builder()
                        .url("https://claimbes.store/telestock/api/authentication.php") // Замените на URL вашего сервера
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            // Обработайте ответ от сервера
                            try {
                                JSONObject responseJson = new JSONObject(responseData);
                                if (responseJson.has("success")) {
                                    JSONObject userInfo = responseJson.getJSONObject("user_info");

                                    int id = userInfo.getInt("id");
                                    String name = userInfo.getString("name");
                                    String surname = userInfo.getString("surname");
                                    String address = userInfo.getString("address");
                                    String login = userInfo.getString("login");
                                    String password = userInfo.getString("password");
                                    String phone = userInfo.getString("phone");
                                    String balance = userInfo.getString("balance");

                                    // Use the parsed variables as needed
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this, "Nice!: ", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                    Map<String, String> values = new HashMap<>();
                                    values.put("id", String.valueOf(id));
                                    values.put("name", name);
                                    values.put("surname", surname);
                                    values.put("address", address);
                                    values.put("login", login);
                                    values.put("password", password);
                                    values.put("phone", phone);
                                    values.put("balance", balance);

                                    for (Map.Entry<String, String> entry : values.entrySet()) {
                                        if (!sharedPreferences.contains(entry.getKey())) {
                                            editor.putString(entry.getKey(), entry.getValue());
                                        }
                                    }
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    editor.apply();
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Handle server error
                                    String errorMessage = responseJson.getString("error");
                                    String errorMessages = responseJson.getString("pass");
                                    String errorMessagess = responseJson.getString("login");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this, "Error: " + errorMessage + errorMessages + errorMessagess, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
            }
        });

    }
}
