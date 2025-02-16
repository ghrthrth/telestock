package com.example.telestock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

public class RegistrationOrLogin extends AppCompatActivity {

    private Button reg_btn;
    private Button auth_btn;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_or_login);

        reg_btn = findViewById(R.id.registration);
        auth_btn = findViewById(R.id.auth);

        reg_btn.setVisibility(View.GONE);
        auth_btn.setVisibility(View.GONE);

        Map<String, String> userData = getUserData(this);
        String balance = userData.get("balance");

        updateUserData(balance);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationOrLogin.this, TelephoneRegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });
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

    public static Map<String, String> getUserData(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "");
        String name = sharedPreferences.getString("name", "");
        String surname = sharedPreferences.getString("surname", "");
        String address = sharedPreferences.getString("address", "");
        String login = sharedPreferences.getString("login", "");
        String phone = sharedPreferences.getString("phone", "");
        String balance = sharedPreferences.getString("balance", "");

        Map<String, String> userData = new HashMap<>();
        userData.put("id", id);
        userData.put("name", name);
        userData.put("surname", surname);
        userData.put("address", address);
        userData.put("login", login);
        userData.put("phone", phone);
        userData.put("balance", balance);

        return userData;
    }

    public void updateUserData(String oldBalance){
        Map<String, String> userData = getUserData(this);
        String id = userData.get("id");

        ProgressBar progressBar = findViewById(R.id.progressBar);

        OkHttpClient client = new OkHttpClient();

        // Создайте объект JSON с данными пользователя
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Request request = new Request.Builder()
                .url("https://claimbes.store/telestock/api/update.php") // Замените на URL вашего сервера
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

                            String newBalance = userInfo.getString("balance");
                            String id = userInfo.getString("id");
                            String name = userInfo.getString("name");
                            String surname = userInfo.getString("surname");
                            String address = userInfo.getString("address");
                            String login = userInfo.getString("login");
                            String phone = userInfo.getString("phone");
                            Log.d("Gergrfdsde", "fgergf" + newBalance + id + name + surname + address + login + phone);


                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                            Map<String, String> dataMap = new HashMap<>();
                            boolean isDataEmpty = false;
                            for (String value : dataMap.values()) {
                                if (value.isEmpty()) {
                                    isDataEmpty = true;
                                    dataMap.put("id", userInfo.getString("id"));
                                    dataMap.put("name", userInfo.getString("name"));
                                    dataMap.put("surname", userInfo.getString("surname"));
                                    dataMap.put("address", userInfo.getString("address"));
                                    dataMap.put("login", userInfo.getString("login"));
                                    dataMap.put("phone", userInfo.getString("phone"));
                                    break;
                                } else {
                                    dataMap.put("id", sharedPreferences.getString("id", ""));
                                    dataMap.put("name", sharedPreferences.getString("name", ""));
                                    dataMap.put("surname", sharedPreferences.getString("surname", ""));
                                    dataMap.put("address", sharedPreferences.getString("address", ""));
                                    dataMap.put("login", sharedPreferences.getString("login", ""));
                                    dataMap.put("phone", sharedPreferences.getString("phone", ""));
                                }
                            }
                            if (!isDataEmpty) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE); // скрываем ProgressBar
                                    }
                                });
                                Intent intent = new Intent(RegistrationOrLogin.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            if (!newBalance.equals(oldBalance)) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("balance", newBalance);
                                editor.apply();
                            }
                        }
                        if (responseJson.has("error_auth")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE); // скрываем ProgressBar
                                    reg_btn.setVisibility(View.VISIBLE);
                                    auth_btn.setVisibility(View.VISIBLE);
                                    Toast.makeText(RegistrationOrLogin.this, "Войдите в лк заного!", Toast.LENGTH_SHORT).show();
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

}
