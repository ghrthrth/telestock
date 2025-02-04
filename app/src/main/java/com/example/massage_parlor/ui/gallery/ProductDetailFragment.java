package com.example.massage_parlor.ui.gallery;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.massage_parlor.R;
import com.example.massage_parlor.ui.cart.Product;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.massage_parlor.ui.cart.CartManager;

public class ProductDetailFragment extends BottomSheetDialogFragment {
    private int id;
    private String title, description, fio;
    private double price;
    private Context mContext;
    public CartManager cartManager;

    public ProductDetailFragment(Context context, int id, String title, String description, double price, String fio) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.fio = fio;
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        TextView titleTextView = view.findViewById(R.id.title);
        TextView descriptionTextView = view.findViewById(R.id.description);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView fioTextView = view.findViewById(R.id.fio);
        Button addToCartButton = view.findViewById(R.id.button_appointment);

        titleTextView.setText("Название услуги: " + title);
        descriptionTextView.setText("Описание услуги: " + description);
        priceTextView.setText("Цена услуги: " + price);
        fioTextView.setText("Фио специалиста: " + fio);

        cartManager = CartManager.getInstance(mContext);

        addToCartButton.setOnClickListener(v -> {
            Product product = new Product(id, title, price, 1);
            cartManager.addToCart(product);
            Toast.makeText(mContext, "Товар добавлен в корзину!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
