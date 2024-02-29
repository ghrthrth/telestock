package com.example.massage_parlor.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.massage_parlor.databinding.FragmentGalleryBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getPhotoUrlsFromServer();
        return root;
    }
    private void addItemsToList(JSONArray jsonArray, List<String> list) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                String item = jsonArray.getString(i);
                list.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getPhotoUrlsFromServer() {
        String url = "https://claimbe.store/massage_parlor/admin_api/return.php"; // Замените на ваш URL-адрес сервера

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray photoUrlsArray = jsonObject.getJSONArray("photoUrls");
                        JSONArray titleArray = jsonObject.getJSONArray("title");
                        JSONArray descriptionArray = jsonObject.getJSONArray("description");
                        JSONArray priceArray = jsonObject.getJSONArray("price");

                        List<String> photoUrls = new ArrayList<>();
                        List<String> titles = new ArrayList<>();
                        List<String> descriptions = new ArrayList<>();
                        List<String> prices = new ArrayList<>();

                        addItemsToList(photoUrlsArray, photoUrls);
                        addItemsToList(titleArray, titles);
                        addItemsToList(descriptionArray, descriptions);
                        addItemsToList(priceArray, prices);


                        // Now you can use the parsed data as needed
                        for (int i = 0; i < photoUrls.size(); i++) {
                            String photoUrl = photoUrls.get(i);
                            String title = titles.get(i);
                            String description = descriptions.get(i);
                            String price = prices.get(i);
                        }

                        displayPhotosInGrid(photoUrls, titles, descriptions, prices);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void displayPhotosInGrid(List<String> photoUrls, List<String> titles, List<String> descriptions, List<String> prices) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GridView gridView = binding.gridView;
                ImageAdapter adapter = new ImageAdapter(getContext(), photoUrls, titles, descriptions, prices);
                gridView.setAdapter(adapter);
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}