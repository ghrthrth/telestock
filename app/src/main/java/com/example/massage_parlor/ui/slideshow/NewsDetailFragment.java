package com.example.massage_parlor.ui.slideshow;

import android.content.Context;
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

import com.example.massage_parlor.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewsDetailFragment extends BottomSheetDialogFragment {
    private int id;
    private String title, description, imageUrl;
    private Context mContext;

    private OnNewsDeletedListener onNewsDeletedListener;

    public void setOnNewsDeletedListener(OnNewsDeletedListener listener) {
        this.onNewsDeletedListener = listener;
    }


    public NewsDetailFragment(Context context, int id, String title, String description, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;  // Добавляем изображение
        this.mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_detail, container, false);

        ImageView imageView = view.findViewById(R.id.news_image);
        TextView titleTextView = view.findViewById(R.id.news_title);
        TextView descriptionTextView = view.findViewById(R.id.news_description);

        Button deleteButton = view.findViewById(R.id.button_delete_news);

        Picasso.get().load(imageUrl).into(imageView);
        titleTextView.setText("Название " + title);
        descriptionTextView.setText("Описание " + description);

        deleteButton.setOnClickListener(v -> {
            new HttpRequestTask(getContext(), onNewsDeletedListener, id).execute(String.valueOf(id));
            dismiss(); // Закрываем фрагмент после удаления
        });

        return view;
    }


    private static class HttpRequestTask extends AsyncTask<String, Void, String> {
        private Context context;
        private OnNewsDeletedListener listener;
        private int newsId;

        public HttpRequestTask(Context context, OnNewsDeletedListener listener, int newsId) {
            this.context = context;
            this.listener = listener;
            this.newsId = newsId;
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
                        .url("https://claimbes.store/massage_parlor/api/delete_news.php")
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
                    listener.onNewsDeleted(newsId);
                }
                Toast.makeText(context, "Успешно: " + result, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, result != null ? result : "Неизвестная ошибка", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface OnNewsDeletedListener {
        void onNewsDeleted(int newsId);
    }
}
