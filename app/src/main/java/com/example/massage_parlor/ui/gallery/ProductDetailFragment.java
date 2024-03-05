package com.example.massage_parlor.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.massage_parlor.R;
import com.example.massage_parlor.RegistrationActivity;
import com.example.massage_parlor.RegistrationOrLogin;
import com.example.massage_parlor.ui.home.HomeFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ProductDetailFragment extends BottomSheetDialogFragment {
    private String id;
    private String title;
    private String description;
    private String price;
    private Context mContext;

    public ProductDetailFragment(Context context, String id, String title, String description, String price) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.id = id;
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        // Заполнение полей информацией о товаре
        TextView titleTextView = view.findViewById(R.id.title);
        TextView descriptionTextView = view.findViewById(R.id.description);
        TextView priceTextView = view.findViewById(R.id.price);
        Button send_data  = view.findViewById(R.id.button_appointment);

        Map<String, String> userData = HomeFragment.getUserData(mContext);


        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(Integer.parseInt(userData.get("id"))));
        params.put("service_id", String.valueOf(Integer.parseInt(id)));

        //Log.d("freg", "Gfre" + userData.get("id") + " " + id);

        titleTextView.setText(title);
        descriptionTextView.setText(description);
        priceTextView.setText(price);

        send_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpRequestTask(mContext, "https://claimbe.store/massage_parlor/api/add_application/add.php", params).execute();

            }
        });

        return view;
    }
}

