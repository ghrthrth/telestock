package com.example.telestock.ui.home;

import static com.example.telestock.RegistrationOrLogin.getUserData;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.telestock.databinding.FragmentHomeBinding;

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
        final TextView text_address = binding.textAddress;
        final TextView text_phone = binding.textPhone;
        final TextView text_id = binding.textId;


        // Получение данных пользователя из метода getUserData
        Map<String, String> userData = getUserData(this.requireContext());
        String name = userData.get("name");
        String surname = userData.get("surname");
        String balance = userData.get("balance");
        String address = userData.get("address");
        String id = userData.get("id");
        String phone = userData.get("phone");

        textView.setText("Привет - " + name + " " + surname + "!");
        text_balance.setText("Ваш баланс - " + balance + "Br");
        text_address.setText("Ваш адрес - " + address);
        text_id.setText("Ваш Id - " + id );
        text_phone.setText("Ваш телефон - " + phone);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}