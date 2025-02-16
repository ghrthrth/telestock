package com.example.telestock.ui.create_news;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.telestock.R;
import com.example.telestock.databinding.FragmentCreateNewsBinding;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.airbnb.lottie.LottieAnimationView;

public class CreateNewsFragment extends Fragment {

    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private static final int REQUEST_CODE_PERMISSION = 2;

    private OkHttpClient client = new OkHttpClient();

    private Uri selectedImageUri;

    private FragmentCreateNewsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CreateNewsViewModel createNewsViewModel = new ViewModelProvider(this).get(CreateNewsViewModel.class);
        binding = FragmentCreateNewsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView title = binding.title;
        final TextView description = binding.description;
        final Button send = binding.send;
        final Button selectPhoto = binding.selectPhoto;
        LottieAnimationView lottieProgress = binding.lottieProgress; // Инициализируем LottieAnimationView

        selectPhoto.setOnClickListener(v -> requestStoragePermission());

        send.setOnClickListener(v -> {
            send.setEnabled(false); // Отключить кнопку, чтобы пользователь не нажал повторно
            String titles = title.getText().toString();
            String descriptions = description.getText().toString();
            new HttpRequestTask(lottieProgress).execute(titles, descriptions); // Передаем lottieProgress
        });


        return root;
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        } else {
            launchGallery();
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 и выше
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_CODE_PERMISSION);
            } else {
                launchGallery();
            }
        } else {
            // Android 12 и ниже
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSION);
            } else {
                launchGallery();
            }
        }
    }

    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchGallery();
            } else {
                Toast.makeText(getContext(), "Разрешение на доступ к хранилищу не предоставлено", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && requestCode == REQUEST_CODE_PICK_IMAGE) {
            selectedImageUri = data.getData();
            ImageView imageView = binding.imagePreview;
            imageView.setImageURI(selectedImageUri);
        }
    }

    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        try (Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    private class HttpRequestTask extends AsyncTask<String, Integer, String> {

        private LottieAnimationView lottieProgress;

        public HttpRequestTask(LottieAnimationView lottieProgress) {
            this.lottieProgress = lottieProgress;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.loadingOverlay.setVisibility(View.VISIBLE); // Показываем экран загрузки
            lottieProgress.setVisibility(View.VISIBLE);  // Показываем анимацию
            lottieProgress.playAnimation(); // Запускаем анимацию
            binding.send.setEnabled(false);  // Отключаем кнопку отправки
        }

        @Override
        protected String doInBackground(String... params) {
            String title = params[0];
            String description = params[1];

            try {
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("title", title)
                        .addFormDataPart("description", description);

                if (selectedImageUri != null) {
                    String filePath = getRealPathFromUri(selectedImageUri);
                    File file = new File(filePath);
                    builder.addFormDataPart("photo", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));

                }

                RequestBody requestBody = builder.build();
                Request request = new Request.Builder()
                        .url("https://claimbes.store/telestock/api/add.php")
                        .post(requestBody)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    return "Ошибка: " + response.message();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Ошибка: " + e.getMessage();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            binding.loadingOverlay.setVisibility(View.GONE);  // Скрываем экран загрузки
            lottieProgress.setVisibility(View.GONE);  // Скрываем анимацию после завершения отправки
            lottieProgress.cancelAnimation();  // Останавливаем анимацию
            binding.send.setEnabled(true);  // Включаем кнопку отправки

            if (result != null) {
                Toast.makeText(getContext(), "Ошибка: " + result, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Данные успешно записаны", Toast.LENGTH_SHORT).show();
                binding.title.setText("");
                binding.description.setText("");
                binding.imagePreview.setImageResource(R.drawable.ic_menu_camera);
                selectedImageUri = null;
            }
        }
    }

}
