package com.example.massage_parlor.ui.gallery;

import android.os.Bundle;
import android.util.Log;
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

import com.example.massage_parlor.databinding.FragmentGalleryBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
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
        String url = "https://claimbes.store/massage_parlor/admin_api/return.php"; // Замените на ваш URL-адрес сервера

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
                        JSONArray priceArray = jsonObject.getJSONArray("price");
                        //JSONArray fioArray = jsonObject.getJSONArray("fio");


                        List<String> ids = new ArrayList<>();
                        List<String> photoUrls = new ArrayList<>();
                        List<String> titles = new ArrayList<>();
                        List<String> descriptions = new ArrayList<>();
                        List<String> prices = new ArrayList<>();
/*                        List<String> fio = new ArrayList<>();*/


                        addItemsToList(idArray, ids);
                        addItemsToList(photoUrlsArray, photoUrls);
                        addItemsToList(titleArray, titles);
                        addItemsToList(descriptionArray, descriptions);
                        addItemsToList(priceArray, prices);
/*                        addItemsToList(fioArray, fio);*/

                        displayPhotosInGrid(ids, photoUrls, titles, descriptions, prices);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void displayPhotosInGrid(List<String> ids, List<String> photoUrls, List<String> titles, List<String> descriptions, List<String> prices) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GridView gridView = binding.gridView;
                ImageAdapter adapter = new ImageAdapter(getContext(), photoUrls, titles, descriptions, prices);
                gridView.setAdapter(adapter);
                SearchView searchView = binding.searchView;
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.getFilter().filter(newText);
                        return true;
                    }

                });

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Получение выбранного товара
                        String selectedId = ids.get(position);
                        String selectedTitle = titles.get(position);
                        String selectedDescription = descriptions.get(position);
                        String selectedPrice = prices.get(position);
/*                        String selectedFio = fios.get(position);*/
                        String selectedImageUrl = photoUrls.get(position);  // Получаем URL изображения

                        int selectedIds = Integer.parseInt(selectedId);
                        double selectedPrices = Double.parseDouble(selectedPrice);

                        // Создание экземпляра ProductDetailFragment и его отображение
                        ProductDetailFragment detailFragment = new ProductDetailFragment(getContext(), selectedIds, selectedTitle, selectedDescription, selectedPrices, selectedImageUrl);
                        detailFragment.show(getFragmentManager(), "product_detail");
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