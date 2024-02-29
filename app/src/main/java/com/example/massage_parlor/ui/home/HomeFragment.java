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

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private String id;
    private String name;
    private String surname;
    private String address;
    private String login;
    private String phone;
    private String balance;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        super.onCreate(savedInstanceState);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");
        name = sharedPreferences.getString("name", "");
        surname = sharedPreferences.getString("surname", "");
        address = sharedPreferences.getString("address", "");
        login = sharedPreferences.getString("login", "");
        phone = sharedPreferences.getString("phone", "");
        balance = sharedPreferences.getString("balance", "");


        final TextView textView = binding.textHome;
        final TextView text_balance = binding.textBalance;

        textView.setText("Привет - " + name + "," + surname + "!");
        text_balance.setText("Ваш баланс - " + balance);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}