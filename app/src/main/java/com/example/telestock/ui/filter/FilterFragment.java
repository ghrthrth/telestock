package com.example.telestock.ui.filter;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.telestock.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class FilterFragment extends BottomSheetDialogFragment {

    private FilterFragmentListener listener;
    private Spinner categorySpinner;

    private List<String> categorys;

    public interface FilterFragmentListener {
        void onFilterApplied(double minPrice, double maxPrice, String category);
    }

    public void setFilterFragmentListener(FilterFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        EditText minPriceEditText = view.findViewById(R.id.minPriceEditText);
        EditText maxPriceEditText = view.findViewById(R.id.maxPriceEditText);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        Button applyFilterButton = view.findViewById(R.id.applyFilterButton);

        // Заполняем Spinner категориями
        List<String> categories = new ArrayList<>();
        categories.add("Все категории"); // Добавляем опцию "Все категории"
        categories.addAll(getUniqueCategories()); // Добавляем уникальные категории

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        applyFilterButton.setOnClickListener(v -> {
            // Получаем значения из полей ввода
            String minPriceText = minPriceEditText.getText().toString();
            String maxPriceText = maxPriceEditText.getText().toString();

            // Устанавливаем значения по умолчанию, если поля пустые
            double minPrice = minPriceText.isEmpty() ? 0 : Double.parseDouble(minPriceText);
            double maxPrice = maxPriceText.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxPriceText);

            // Получаем выбранную категорию
            String selectedCategory = categorySpinner.getSelectedItem().toString();

            // Передаем выбранные значения обратно в GalleryFragment
            if (listener != null) {
                listener.onFilterApplied(minPrice, maxPrice, selectedCategory);
            }

            // Закрываем фрагмент
            dismiss();
        });

        return view;
    }
    public void setCategories(List<String> categories) {
        this.categorys = categories;
    }
    // Метод для получения уникальных категорий
    private List<String> getUniqueCategories() {
        List<String> uniqueCategories = new ArrayList<>();
        for (String category : categorys) {
            if (!uniqueCategories.contains(category)) {
                uniqueCategories.add(category);
            }
        }
        return uniqueCategories;
    }
}