package com.example.telestock.ui.applications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.telestock.databinding.FragmentApplicationsBinding;

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

public class ApplicationsFragment extends Fragment {

    private FragmentApplicationsBinding binding;

    private OkHttpClient client = new OkHttpClient();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ApplicationsViewModel applicationsViewModel =
                new ViewModelProvider(this).get(ApplicationsViewModel.class);

        binding = FragmentApplicationsBinding.inflate(inflater, container, false);
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

        if (!isAdded()) return; // Проверяем, не был ли фрагмент уничтожен

        String url = "https://claimbes.store/telestock/api/add_application/return.php"; // Замените на ваш URL-адрес сервера

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
                        JSONArray user_idArray = jsonObject.getJSONArray("user_id");
                        JSONArray service_idArray = jsonObject.getJSONArray("id_service");
                        JSONArray titleArray = jsonObject.getJSONArray("title");
                        JSONArray nameArray = jsonObject.getJSONArray("name");
                        JSONArray surnameArray = jsonObject.getJSONArray("surname");
                        JSONArray phoneArray = jsonObject.getJSONArray("phone");
                        JSONArray datesArray = jsonObject.getJSONArray("dates");
                        JSONArray timesArray = jsonObject.getJSONArray("times");
/*                        JSONArray fioArray = jsonObject.getJSONArray("fio");*/
                        JSONArray product_quantityArray = jsonObject.getJSONArray("product_quantity");

                        Log.d("erf", "ferf" + titleArray);

                        List<String> ids = new ArrayList<>();
                        List<String> service_ids = new ArrayList<>();
                        List<String> titles = new ArrayList<>();
                        List<String> names = new ArrayList<>();
                        List<String> surnames = new ArrayList<>();
                        List<String> phones = new ArrayList<>();
                        List<String> dates = new ArrayList<>();
                        List<String> times = new ArrayList<>();
/*                        List<String> fios = new ArrayList<>();*/
                        List<String> product_quantitys = new ArrayList<>();


                        addItemsToList(user_idArray, ids);
                        addItemsToList(service_idArray, service_ids);
                        addItemsToList(titleArray, titles);
                        addItemsToList(nameArray, names);
                        addItemsToList(surnameArray, surnames);
                        addItemsToList(phoneArray, phones);
                        addItemsToList(datesArray, dates);
                        addItemsToList(timesArray, times);
/*                        addItemsToList(fioArray, fios);*/
                        addItemsToList(product_quantityArray, product_quantitys);

                        displayPhotosInGrid(ids, service_ids, titles, names, surnames, phones, dates, times, product_quantitys);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void displayPhotosInGrid(List<String> ids, List<String> service_ids, List<String> titles, List<String> names, List<String> surnames, List<String> phones, List<String> dates, List<String> times, List<String> product_quantitys) {
        if (getActivity() == null || binding == null) {
            return; // Предотвращение краша, если фрагмент уничтожен
        }
        if (!isAdded()) return; // Проверяем, не был ли фрагмент уничтожен

        getActivity().runOnUiThread(() -> {
            if (binding == null) return; // Проверяем повторно

            GridView gridView = binding.gridView;
            ImageAdapter adapter = new ImageAdapter(getContext(), ids, service_ids, titles, names, surnames, phones, dates, times, product_quantitys);
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

            gridView.setOnItemClickListener((parent, view, position, id) -> {
                if (binding == null) return; // Проверяем, не уничтожен ли фрагмент

                String selectedUserId = ids.get(position);
                String selectedServiceId = service_ids.get(position);
                String selectedTitle = titles.get(position);
                String selectedNames = names.get(position);
                String selectedSurnames = surnames.get(position);
                String selectedPhones = phones.get(position);
                String selectedDates = dates.get(position);
                String selectedTimes = times.get(position);
                String selectedProduct_quantitys = product_quantitys.get(position);

                ApplicationDetailFragment detailFragment = new ApplicationDetailFragment(
                        getContext(), selectedUserId, selectedServiceId, selectedTitle,
                        selectedNames, selectedSurnames, selectedPhones, selectedDates,
                        selectedTimes, selectedProduct_quantitys
                );
                detailFragment.setAdapter(adapter);
                detailFragment.setPosition(position);

                if (getFragmentManager() != null) {
                    detailFragment.show(getFragmentManager(), "application_detail");
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
