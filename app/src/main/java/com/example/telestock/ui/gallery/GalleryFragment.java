package com.example.telestock.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telestock.databinding.FragmentGalleryBinding;
import com.example.telestock.ui.filter.FilterFragment;

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

public class GalleryFragment extends Fragment implements ProductDetailFragment.OnProductDeletedListener, FilterFragment.FilterFragmentListener {

    private FragmentGalleryBinding binding;
    private List<String> ids = new ArrayList<>();
    private List<String> photoUrls = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private List<String> descriptions = new ArrayList<>();
    private List<String> categorys = new ArrayList<>();
    private List<String> prices = new ArrayList<>();

    private OkHttpClient client = new OkHttpClient();
    private ImageAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getPhotoUrlsFromServer();  // Получаем данные с сервера

        // Кнопка для открытия фильтра
        binding.filterButton.setOnClickListener(v -> {
            FilterFragment filterFragment = new FilterFragment();
            filterFragment.setFilterFragmentListener(this);
            filterFragment.setCategories(categorys); // Передаем список категорий
            filterFragment.show(getParentFragmentManager(), "filter_fragment");
        });

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
        String url = "https://claimbes.store/telestock/admin_api/return.php"; // Замените на ваш URL-адрес сервера

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
                        JSONArray categoryArray = jsonObject.getJSONArray("category");
                        JSONArray priceArray = jsonObject.getJSONArray("price");

                        // Логируем данные для отладки
                        Log.d("ServerData", "Categories: " + categoryArray.toString());
                        Log.d("ServerData", "Prices: " + priceArray.toString());

                        // Очищаем списки перед добавлением новых данных
                        ids.clear();
                        photoUrls.clear();
                        titles.clear();
                        descriptions.clear();
                        categorys.clear();
                        prices.clear();

                        // Добавляем данные в списки
                        addItemsToList(idArray, ids);
                        addItemsToList(photoUrlsArray, photoUrls);
                        addItemsToList(titleArray, titles);
                        addItemsToList(descriptionArray, descriptions);
                        addItemsToList(categoryArray, categorys); // Категории
                        addItemsToList(priceArray, prices); // Цены

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
            categorys.remove(index);
            prices.remove(index);
        }

        // Обновляем адаптер
        displayPhotosInGrid();
    }

    private void displayPhotosInGrid() {
        if (!isAdded() || isDetached()) {
            return; // Проверка, что фрагмент все еще прикреплен к активности
        }

        getActivity().runOnUiThread(() -> {
            if (binding == null) return;

            RecyclerView recyclerView = binding.recyclerView;
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

            adapter = new ImageAdapter(getContext(), photoUrls, titles, descriptions, categorys, prices);
            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(position -> {
                if (position < 0 || position >= ids.size()) {
                    return; // Проверка на выход за пределы списка
                }

                String selectedId = ids.get(position);
                String selectedTitle = titles.get(position);
                String selectedDescription = descriptions.get(position);
                String selectedCategory = categorys.get(position);
                String selectedPrice = prices.get(position);
                String selectedImageUrl = photoUrls.get(position);


                int selectedIds = Integer.parseInt(selectedId);
                double selectedPrices = Double.parseDouble(selectedPrice);

                ProductDetailFragment detailFragment = new ProductDetailFragment(getContext(), selectedIds, selectedTitle, selectedDescription, selectedCategory, selectedPrices, selectedImageUrl);
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
    public void onFilterApplied(double minPrice, double maxPrice, String category) {
        // Применяем фильтр к адаптеру
        if (adapter != null) {
            adapter.filterByPriceAndCategory(minPrice, maxPrice, category);
        }
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