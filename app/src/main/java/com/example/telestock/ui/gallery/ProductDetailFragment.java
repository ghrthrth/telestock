package com.example.telestock.ui.gallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import com.example.telestock.R;
import com.example.telestock.ui.cart.Product;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.telestock.ui.cart.CartManager;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProductDetailFragment extends BottomSheetDialogFragment {
    private int id;
    private String title, description, category, imageUrl;
    private double price;
    private Context mContext;
    public CartManager cartManager;

    private OnProductDeletedListener onProductDeletedListener;


    public ProductDetailFragment(Context context, int id, String title, String description, String category, double price, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        /*        this.fio = fio;*/
        this.imageUrl = imageUrl;  // Добавляем изображение
        this.mContext = context;
    }

    public void setOnProductDeletedListener(OnProductDeletedListener listener) {
        this.onProductDeletedListener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userLogin = sharedPreferences.getString("login", ""); // Получаем логин пользователя

        ImageView imageView = view.findViewById(R.id.product_image);
        TextView titleTextView = view.findViewById(R.id.title);
        TextView descriptionTextView = view.findViewById(R.id.description);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView categoryTextView = view.findViewById(R.id.category);
        Button addToCartButton = view.findViewById(R.id.button_appointment);
        Button deleteButton = view.findViewById(R.id.button_delete);

        // Скрываем кнопку удаления, если логин не "admin"
        if (!userLogin.equals("admin")) {
            deleteButton.setVisibility(View.GONE);
        }

        Picasso.get().load(imageUrl).into(imageView);
        titleTextView.setText("Название товара: " + title);
        descriptionTextView.setText("Описание товара: " + description);
        categoryTextView.setText("Категория товара " + category);
        priceTextView.setText("Цена товара: " + price);

        cartManager = CartManager.getInstance(mContext);

        addToCartButton.setOnClickListener(v -> {
            Product product = new Product(id, title, price, 1, imageUrl);  // Передаём изображение
            cartManager.addToCart(product);
            Toast.makeText(mContext, "Товар добавлен в корзину!", Toast.LENGTH_SHORT).show();
        });

        deleteButton.setOnClickListener(v -> {
            new HttpRequestTask(getContext(), onProductDeletedListener, id).execute(String.valueOf(id));
            dismiss(); // Закрываем фрагмент после удаления
        });

        return view;
    }

    private static class HttpRequestTask extends AsyncTask<String, Void, String> {
        private Context context;
        private OnProductDeletedListener listener;
        private int productId;

        public HttpRequestTask(Context context, OnProductDeletedListener listener, int productId) {
            this.context = context;
            this.listener = listener;
            this.productId = productId;
        }

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 0 || params[0] == null || params[0].isEmpty()) {
                return "Ошибка: ID не указан";
            }
            String id = params[0];

            try {
                // Создаем JSON-строку для передачи
                String json = "{\"id\": \"" + id + "\"}";

                // Создаем тело запроса с типом application/json
                RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json"));

                // Создаем запрос
                Request request = new Request.Builder()
                        .url("https://claimbes.store/telestock/admin_api/delete.php")
                        .post(requestBody) // Указываем метод POST
                        .build();

                // Выполняем запрос
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();

                // Проверяем успешность запроса
                if (!response.isSuccessful()) {
                    return "Ошибка: " + response.message();
                }

                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return "Ошибка: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.startsWith("Ошибка:")) {
                if (listener != null) {
                    listener.onProductDeleted(productId);
                }
                Toast.makeText(context, "Успешно: " + result, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, result != null ? result : "Неизвестная ошибка", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public interface OnProductDeletedListener {
        void onProductDeleted(int productId);
    }
}

