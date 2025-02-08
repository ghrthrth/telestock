package com.example.massage_parlor.ui.gallery;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.massage_parlor.R;
import com.example.massage_parlor.ui.cart.Product;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.massage_parlor.ui.cart.CartManager;
import com.squareup.picasso.Picasso;

public class ProductDetailFragment extends BottomSheetDialogFragment {
    private int id;
    private String title, description, imageUrl;
    private double price;
    private Context mContext;
    public CartManager cartManager;

    public ProductDetailFragment(Context context, int id, String title, String description, double price, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
/*        this.fio = fio;*/
        this.imageUrl = imageUrl;  // Добавляем изображение
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        ImageView imageView = view.findViewById(R.id.product_image);
        TextView titleTextView = view.findViewById(R.id.title);
        TextView descriptionTextView = view.findViewById(R.id.description);
        TextView priceTextView = view.findViewById(R.id.price);
/*        TextView fioTextView = view.findViewById(R.id.fio);*/
        Button addToCartButton = view.findViewById(R.id.button_appointment);

        Picasso.get().load(imageUrl).into(imageView);
        titleTextView.setText("Название товара: " + title);
        descriptionTextView.setText("Описание товара: " + description);
        priceTextView.setText("Цена товара: " + price);
/*        fioTextView.setText("Фио специалиста: " + fio);*/

        cartManager = CartManager.getInstance(mContext);

        addToCartButton.setOnClickListener(v -> {
            Product product = new Product(id, title, price, 1, imageUrl);  // Передаём изображение
            cartManager.addToCart(product);
            Toast.makeText(mContext, "Товар добавлен в корзину!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
