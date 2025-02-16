package com.example.telestock.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.telestock.databinding.FragmentCartBinding;
import java.util.List;

import okhttp3.OkHttpClient;

public class CartFragment extends Fragment {
    private FragmentCartBinding binding;
    private CartAdapter cartAdapter;
    private CartManager cartManager;

    private OkHttpClient client = new OkHttpClient();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CartViewModel cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        cartManager = CartManager.getInstance(getContext());

        setupRecyclerView();
        loadCartItems();

        return root;
    }

    private void setupRecyclerView() {
        binding.recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadCartItems() {
        List<Product> cartItems = cartManager.getCartItems();
        cartAdapter = new CartAdapter(getContext(), cartItems, this::updateTotalPrice);
        binding.recyclerViewCart.setAdapter(cartAdapter);
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double total = 0;
        for (Product p : cartManager.getCartItems()) {
            total += p.getPrice() * p.getQuantity();
        }
        binding.txtTotalPrice.setText("Общая сумма: " + total + " ₽");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        // Отменяем все асинхронные запросы
        if (client != null) {
            client.dispatcher().cancelAll();
        }
    }
}
