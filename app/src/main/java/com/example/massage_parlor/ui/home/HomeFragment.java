package com.example.massage_parlor.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.massage_parlor.MainActivity;
import com.example.massage_parlor.databinding.FragmentHomeBinding;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        super.onCreate(savedInstanceState);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        final TextView text_balance = binding.textBalance;

        // Получение данных пользователя из метода getUserData
        Map<String, String> userData = getUserData(this.requireContext());
        String name = userData.get("name");
        String surname = userData.get("surname");
        String balance = userData.get("balance");

        textView.setText("Привет - " + name + "," + surname + "!");
        text_balance.setText("Ваш баланс - " + balance);

        return root;
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}