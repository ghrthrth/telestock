package com.example.massage_parlor.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.massage_parlor.databinding.FragmentSlideshowBinding;
import com.example.massage_parlor.ui.gallery.ImageAdapter;
import com.example.massage_parlor.ui.gallery.ProductDetailFragment;

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

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
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
        String url = "https://claimbe.store/massage_parlor/api/return_news.php"; // Замените на ваш URL-адрес сервера

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
                        JSONArray idArray = jsonObject.getJSONArray("id");
                        JSONArray photoUrlsArray = jsonObject.getJSONArray("photoUrls");
                        JSONArray titleArray = jsonObject.getJSONArray("title");
                        JSONArray descriptionArray = jsonObject.getJSONArray("description");

                        List<String> ids = new ArrayList<>();
                        List<String> photoUrls = new ArrayList<>();
                        List<String> titles = new ArrayList<>();
                        List<String> descriptions = new ArrayList<>();


                        addItemsToList(idArray, ids);
                        addItemsToList(photoUrlsArray, photoUrls);
                        addItemsToList(titleArray, titles);
                        addItemsToList(descriptionArray, descriptions);

                        displayPhotosInGrid(ids, photoUrls, titles, descriptions);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void displayPhotosInGrid(List<String> ids, List<String> photoUrls, List<String> titles, List<String> descriptions) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GridView gridView = binding.gridView1;
                ImageAdapters adapters = new ImageAdapters(getContext(), photoUrls, titles, descriptions);
                gridView.setAdapter(adapters);
                SearchView searchView = binding.searchView;
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapters.getFilter().filter(newText);
                        return true;
                    }

                });
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}