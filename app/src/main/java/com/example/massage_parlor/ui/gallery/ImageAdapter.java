package com.example.massage_parlor.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.massage_parlor.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mPhotoUrls;
    private List<String> mTitles;
    private List<String> mDescriptions;
    private List<String> mPrices;

    public ImageAdapter(Context context, List<String> photoUrls, List<String> titles, List<String> descriptions, List<String> prices) {
        mContext = context;
        mPhotoUrls = photoUrls;
        mTitles = titles;
        mDescriptions = descriptions;
        mPrices = prices;
    }

    @Override
    public int getCount() {
        return mPhotoUrls.size();
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
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_item_layout, parent, false);
        }

        // Находим представления внутри макета элемента сетки
        ImageView imageView = convertView.findViewById(R.id.grid_image);
        TextView titleTextView = convertView.findViewById(R.id.title_text_view);
        TextView descriptionTextView = convertView.findViewById(R.id.description_text_view);
        TextView priceTextView = convertView.findViewById(R.id.price_text_view);

        // Устанавливаем данные для каждого представления
        String photoUrl = mPhotoUrls.get(position);
        String title = mTitles.get(position);
        String description = mDescriptions.get(position);
        String price = mPrices.get(position);

        // Загружаем изображение с помощью библиотеки Picasso или Glide
        Picasso.get().load(photoUrl).into(imageView);

        // Устанавливаем текст для текстовых представлений
        titleTextView.setText(title);
        descriptionTextView.setText(description);
        priceTextView.setText(price);

        return convertView;
    }
}
