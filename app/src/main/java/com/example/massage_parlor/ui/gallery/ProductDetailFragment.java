package com.example.massage_parlor.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.massage_parlor.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.Nullable;

public class ProductDetailFragment extends BottomSheetDialogFragment {
    private String title;
    private String description;
    private String price;

    public ProductDetailFragment(String title, String description, String price) {
        this.title = title;
        this.description = description;
        this.price = price;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        // Заполнение полей информацией о товаре
        TextView titleTextView = view.findViewById(R.id.title);
        TextView descriptionTextView = view.findViewById(R.id.description);
        TextView priceTextView = view.findViewById(R.id.price);

        titleTextView.setText(title);
        descriptionTextView.setText(description);
        priceTextView.setText(price);

        return view;
    }
}

