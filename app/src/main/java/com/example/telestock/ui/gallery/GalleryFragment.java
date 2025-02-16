package com.example.massage_parlor.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class GalleryFragment extends Fragment implements ProductDetailFragment.OnProductDeletedListener{

    private FragmentGalleryBinding binding;
    private List<String> ids = new ArrayList<>();
    private List<String> photoUrls = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private List<String> descriptions = new ArrayList<>();
    private List<String> prices = new ArrayList<>();

    private OkHttpClient client = new OkHttpClient();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getPhotoUrlsFromServer();  // Получаем данные с сервера
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
                if (!isAdded()) return; // Проверяем, что фрагмент не уничтожен

                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray idArray = jsonObject.getJSONArray("id");
                        JSONArray photoUrlsArray = jsonObject.getJSONArray("photoUrls");
                        JSONArray titleArray = jsonObject.getJSONArray("title");
                        JSONArray descriptionArray = jsonObject.getJSONArray("description");
                        JSONArray priceArray = jsonObject.getJSONArray("price");

                        addItemsToList(idArray, ids);
                        addItemsToList(photoUrlsArray, photoUrls);
                        addItemsToList(titleArray, titles);
                        addItemsToList(descriptionArray, descriptions);
                        addItemsToList(priceArray, prices);

                        if (isAdded()) { // Еще раз проверяем перед вызовом UI
                            displayPhotosInGrid();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    @Override
    public void onProductDeleted(int productId) {
        // Удаляем товар из списка по ID
        int index = ids.indexOf(String.valueOf(productId));
        if (index != -1) {
            ids.remove(index);
            photoUrls.remove(index);
            titles.remove(index);
            descriptions.remove(index);
            prices.remove(index);
        }

        // Обновляем адаптер
        displayPhotosInGrid();
    }
    private void displayPhotosInGrid() {
        getActivity().runOnUiThread(() -> {
            if (binding == null) return;

            RecyclerView recyclerView = binding.recyclerView;
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

            ImageAdapter adapter = new ImageAdapter(getContext(), photoUrls, titles, descriptions, prices);
            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(position -> {
                String selectedId = ids.get(position);
                String selectedTitle = titles.get(position);
                String selectedDescription = descriptions.get(position);
                String selectedPrice = prices.get(position);
                String selectedImageUrl = photoUrls.get(position);

                int selectedIds = Integer.parseInt(selectedId);
                double selectedPrices = Double.parseDouble(selectedPrice);

                ProductDetailFragment detailFragment = new ProductDetailFragment(getContext(), selectedIds, selectedTitle, selectedDescription, selectedPrices, selectedImageUrl);
                detailFragment.setOnProductDeletedListener(GalleryFragment.this);
                detailFragment.show(getParentFragmentManager(), "product_detail");
            });

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
        });
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
}
