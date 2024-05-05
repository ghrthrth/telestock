package com.example.massage_parlor.ui.gallery;

import static com.example.massage_parlor.RegistrationOrLogin.getUserData;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.massage_parlor.R;
import com.example.massage_parlor.RegistrationActivity;
import com.example.massage_parlor.RegistrationOrLogin;
import com.example.massage_parlor.ui.home.HomeFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProductDetailFragment extends BottomSheetDialogFragment {
    private String id;
    private String title;
    private String description;
    private String price;

    private String fio;
    private Context mContext;

    public ProductDetailFragment(Context context, String id, String title, String description, String price, String fio) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.id = id;
        this.fio = fio;
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
        TextView fioTextView = view.findViewById(R.id.fio);
        Button send_data  = view.findViewById(R.id.button_appointment);

        Map<String, String> userData = getUserData(mContext);


        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(Integer.parseInt(userData.get("id"))));
        params.put("service_id", String.valueOf(Integer.parseInt(id)));

        //Log.d("freg", "Gfre" + userData.get("id") + " " + id);

        titleTextView.setText("Название услуги: " + title);
        descriptionTextView.setText("Описание услуги: " + description);
        priceTextView.setText("Цена услуги: " + price);
        fioTextView.setText("Фио специалиста: " + fio);

        send_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Создаем экземпляр Calendar для получения текущей даты и времени
                Calendar calendar = Calendar.getInstance();

                // Создайте DatePickerDialog и установите в качестве начальной даты текущую дату
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Создать TimePickerDialog и установите начальное время на текущее время
                        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Форматируем дату и время
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

                                // Установите год, месяц и день в календаре
                                calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                                Date date = calendar.getTime();

                                String formattedDate = dateFormat.format(date);
                                String formattedTime = timeFormat.format(date);

                                // При необходимости используйте отформатированную дату и время
                                Log.d("efwgrt", "gfrwg" + formattedDate + " / " + formattedTime);

                                params.put("dates", formattedDate);
                                params.put("times", formattedTime);

                                new HttpRequestTask(mContext, "https://claimbes.store/massage_parlor/api/add_application/add.php", params).execute();
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

                        // Показ TimePickerDialog
                        timePickerDialog.show();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                // Показ DatePickerDialog
                datePickerDialog.show();
            }
        });


        return view;
    }
}

