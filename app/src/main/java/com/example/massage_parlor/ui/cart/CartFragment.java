package com.example.massage_parlor.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.massage_parlor.databinding.FragmentCartBinding;
import java.util.List;

public class CartFragment extends Fragment {
    private FragmentCartBinding binding;
    private CartAdapter cartAdapter;
    private CartManager cartManager;

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
    }
}
