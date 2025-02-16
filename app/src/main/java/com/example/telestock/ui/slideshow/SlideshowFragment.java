package com.example.telestock.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.telestock.databinding.FragmentSlideshowBinding;

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

public class SlideshowFragment extends Fragment implements NewsDetailFragment.OnNewsDeletedListener {

    private FragmentSlideshowBinding binding;
    private List<String> ids = new ArrayList<>(); // Добавьте это поле
    private List<String> photoUrls = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private List<String> descriptions = new ArrayList<>();

    private OkHttpClient client = new OkHttpClient();


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
        String url = "https://claimbes.store/telestock/api/return_news.php"; // Замените на ваш URL-адрес сервера

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

                        // Очистите списки перед добавлением новых данных
                        ids.clear();
                        photoUrls.clear();
                        titles.clear();
                        descriptions.clear();

                        addItemsToList(idArray, ids);
                        addItemsToList(photoUrlsArray, photoUrls);
                        addItemsToList(titleArray, titles);
                        addItemsToList(descriptionArray, descriptions);

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
    public void onNewsDeleted(int newsId) {
        // Удаляем новость из списка по ID
        int index = ids.indexOf(String.valueOf(newsId));
        if (index != -1) {
            ids.remove(index);
            photoUrls.remove(index);
            titles.remove(index);
            descriptions.remove(index);
        }

        // Обновляем адаптер
        displayPhotosInGrid();
    }
    private void displayPhotosInGrid() {
        getActivity().runOnUiThread(() -> {
            if (binding == null) return; // Проверяем, что binding существует
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

            gridView.setOnItemClickListener((parent, view, position, id) -> {
                String selectedId = ids.get(position);
                String selectedTitle = titles.get(position);
                String selectedDescription = descriptions.get(position);
                String selectedImageUrl = photoUrls.get(position);

                int selectedIds = Integer.parseInt(selectedId);

                NewsDetailFragment newsDetailFragment = new NewsDetailFragment(getContext(), selectedIds, selectedTitle, selectedDescription, selectedImageUrl);
                newsDetailFragment.setOnNewsDeletedListener(SlideshowFragment.this); // Передаем слушатель
                newsDetailFragment.show(getParentFragmentManager(), "news_detail");
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