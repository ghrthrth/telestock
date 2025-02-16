package com.example.telestock.ui.applications;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.telestock.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ApplicationDetailFragment extends BottomSheetDialogFragment {

    private String user_id;
    private String service_id;
    private String title;
    private String name;
    private String surname;
    private String phone;
    private String date;
    private String time;
/*    private String fio;*/
    private String product_quantity;
    private Context mContext;
    private ImageAdapter adapter;
    private int position;

    public ApplicationDetailFragment(Context context, String userId, String serviceId, String titles, String names, String surnames, String phones, String dates, String times, String product_quantitys) {
        user_id = userId;
        service_id = serviceId;
        this.title = titles;
        this.name = names;
        this.surname = surnames;
        this.phone = phones;
        this.date = dates;
        this.time = times;
/*        this.fio = fios;*/
        this.product_quantity = product_quantitys;
        this.mContext = context;
    }

    public void setAdapter(ImageAdapter adapter) {
        this.adapter = adapter;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_application_detail, container, false);

        // Заполнение полей информацией о товаре
        //TextView user_idTextView = view.findViewById(R.id.user_id);
        //TextView service_idTextView = view.findViewById(R.id.service_id);
        TextView titleTextView = view.findViewById(R.id.title);
        TextView nameTextView = view.findViewById(R.id.name);
        TextView surnameTextView = view.findViewById(R.id.surname);
        TextView phoneTextView = view.findViewById(R.id.phone);
        TextView dateTextView = view.findViewById(R.id.date);
        TextView timeTextView = view.findViewById(R.id.time);
/*        TextView fioTextView = view.findViewById(R.id.fio);*/
        TextView product_quantityTextView = view.findViewById(R.id.product_quantity);
        Button send_data  = view.findViewById(R.id.button_appointment);


        //user_idTextView.setText(user_id);
        //service_idTextView.setText(service_id);
        titleTextView.setText("Продукт: " + title);
        nameTextView.setText("Имя заказчика: " + name);
        surnameTextView.setText("Фамилия заказчика: " + surname);
        phoneTextView.setText("Телефон: " + phone);
        dateTextView.setText("Дата заказа: " + date);
        timeTextView.setText("Время заказа: " + time);
/*        fioTextView.setText("К какому специалисту: " + fio);*/
        product_quantityTextView.setText("Количество: " + product_quantity);


        Map<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("service_id", service_id);
        params.put("product_quantity", product_quantity);
        params.put("date", date);
        params.put("time", time);


        send_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpRequestTask(mContext, "https://claimbes.store/telestock/api/add_application/delete.php", params).execute();
                dismiss();
                adapter.removeItem(position);
            }
        });

        return view;
    }

}

