package com.example.telestock.ui.slideshow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.telestock.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapters extends BaseAdapter implements Filterable {

    private Context mContext;
    private List<String> mPhotoUrls;
    private List<String> mTitles;
    private List<String> mDescriptions;
    private List<String> mFilteredTitles; // Добавьте отфильтрованные заголовки
    private LayoutInflater mInflater;
    private ItemFilter mItemFilter = new ItemFilter();

    public ImageAdapters(Context context, List<String> photoUrls, List<String> titles, List<String> descriptions) {
        mContext = context;
        mPhotoUrls = photoUrls;
        mTitles = titles;
        mDescriptions = descriptions;
        mFilteredTitles = new ArrayList<>(titles); // Инициализируйте отфильтрованные заголовки
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mFilteredTitles.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_item_layouts, parent, false);
        }

        // Находим представления внутри макета элемента сетки
        ImageView imageView = convertView.findViewById(R.id.grid_image);
        TextView titleTextView = convertView.findViewById(R.id.title_text_view);
        TextView descriptionTextView = convertView.findViewById(R.id.description_text_view);

        // Устанавливаем данные для каждого представления
        String title = mFilteredTitles.get(position);
        int originalPosition = mTitles.indexOf(title); // Получаем позицию в оригинальном списке

        String photoUrl = mPhotoUrls.get(originalPosition);
        String description = mDescriptions.get(originalPosition);

        // Загружаем изображение с помощью библиотеки Picasso или Glide
        Picasso.get().load(photoUrl).into(imageView);

        // Устанавливаем текст для текстовых представлений
        titleTextView.setText(title);
        descriptionTextView.setText(description);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mItemFilter;
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            List<String> filteredList = new ArrayList<>();

            for (String title : mTitles) {
                if (title.toLowerCase().contains(filterString)) {
                    filteredList.add(title);
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredTitles = (List<String>) results.values;
            notifyDataSetChanged();
        }
    }
}
