package com.example.telestock.ui.gallery;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telestock.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private List<String> mPhotoUrls;
    private List<String> mTitles;
    private List<String> mDescriptions;
    private List<String> mPrices;
    private List<String> mFilteredPhotoUrls;
    private List<String> mFilteredTitles;
    private List<String> mFilteredDescriptions;
    private List<String> categorys;
    private List<String> mFilteredPrices;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ImageAdapter(Context context, List<String> photoUrls, List<String> titles, List<String> descriptions, List<String> categorys, List<String> prices) {
        this.mContext = context;
        this.mPhotoUrls = photoUrls;
        this.mTitles = titles;
        this.mDescriptions = descriptions;
        this.mPrices = prices;
        this.mFilteredPhotoUrls = new ArrayList<>(photoUrls);
        this.mFilteredTitles = new ArrayList<>(titles);
        this.mFilteredDescriptions = new ArrayList<>(descriptions);
        this.categorys = categorys;
        this.mFilteredPrices = new ArrayList<>(prices);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String photoUrl = mFilteredPhotoUrls.get(position);
        String title = mFilteredTitles.get(position);
        String description = mFilteredDescriptions.get(position);
        String price = mFilteredPrices.get(position);

        Picasso.get().load(photoUrl).into(holder.imageView);
        holder.titleTextView.setText(title);
        //holder.descriptionTextView.setText(description);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                int originalPosition = mTitles.indexOf(title);
                listener.onItemClick(originalPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilteredTitles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        //TextView descriptionTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.grid_image);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            //descriptionTextView = itemView.findViewById(R.id.description_text_view);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = new ArrayList<>(mTitles);
                    results.count = mTitles.size();
                } else {
                    String filterString = constraint.toString().toLowerCase();
                    List<Integer> filteredIndexes = new ArrayList<>();

                    // Собираем индексы подходящих элементов
                    for (int i = 0; i < mTitles.size(); i++) {
                        String title = mTitles.get(i);
                        if (title != null && title.toLowerCase().contains(filterString)) {
                            filteredIndexes.add(i);
                        }
                    }

                    results.values = filteredIndexes;
                    results.count = filteredIndexes.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    List<Integer> filteredIndexes = (List<Integer>) results.values;

                    // Очищаем списки
                    mFilteredTitles.clear();
                    mFilteredPhotoUrls.clear();
                    mFilteredDescriptions.clear();
                    mFilteredPrices.clear();

                    // Добавляем элементы по правильным индексам
                    for (int index : filteredIndexes) {
                        mFilteredTitles.add(mTitles.get(index));
                        mFilteredPhotoUrls.add(mPhotoUrls.get(index));
                        mFilteredDescriptions.add(mDescriptions.get(index));
                        mFilteredPrices.add(mPrices.get(index));
                    }

                    notifyDataSetChanged();
                }
            }
        };
    }

    public void filterByPriceAndCategory(double minPrice, double maxPrice, String category) {
        mFilteredPhotoUrls.clear();
        mFilteredTitles.clear();
        mFilteredDescriptions.clear();
        mFilteredPrices.clear();

        // Проверка на пустые или некорректные данные
        if (mPrices.size() != categorys.size()) {
            return; // Если данные некорректны, просто выходим
        }

        for (int i = 0; i < mPrices.size(); i++) {
            try {
                String priceStr = mPrices.get(i);
                String itemCategory = categorys.get(i);

                // Преобразуем цену в число и проверяем соответствие
                double price = Double.parseDouble(priceStr);
                boolean matchesPrice = price >= minPrice && price <= maxPrice;
                boolean matchesCategory = category.equals("Все категории") || category.trim().equalsIgnoreCase(itemCategory.trim());

                // Если совпадают и цена, и категория - добавляем в фильтрованный список
                if (matchesPrice && matchesCategory) {
                    mFilteredPhotoUrls.add(mPhotoUrls.get(i));
                    mFilteredTitles.add(mTitles.get(i));
                    mFilteredDescriptions.add(mDescriptions.get(i));
                    mFilteredPrices.add(mPrices.get(i));
                }
            } catch (NumberFormatException e) {
                // Пропускаем элементы с некорректной ценой
                continue;
            }
        }

        notifyDataSetChanged();
    }


}