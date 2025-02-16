package com.example.massage_parlor.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.massage_parlor.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private List<String> mPhotoUrls;
    private List<String> mTitles;
    private List<String> mDescriptions;
    private List<String> mPrices;
    private List<String> mFilteredTitles;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ImageAdapter(Context context, List<String> photoUrls, List<String> titles, List<String> descriptions, List<String> prices) {
        this.mContext = context;
        this.mPhotoUrls = photoUrls;
        this.mTitles = titles;
        this.mDescriptions = descriptions;
        this.mPrices = prices;
        this.mFilteredTitles = new ArrayList<>(titles);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Получаем заголовок из отфильтрованного списка
        String title = mFilteredTitles.get(position);

        // Получаем индекс в оригинальном списке, чтобы правильно связать данные
        int originalPosition = mTitles.indexOf(title);
        String photoUrl = mPhotoUrls.get(originalPosition);
        String description = mDescriptions.get(originalPosition);
        String price = mPrices.get(originalPosition);

        // Загружаем изображение через Picasso
        Picasso.get().load(photoUrl).into(holder.imageView);

        // Устанавливаем текст для title и description
        holder.titleTextView.setText(title);
        //holder.descriptionTextView.setText(description);


        // Устанавливаем обработчик клика
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
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
        TextView descriptionTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.grid_image);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = constraint.toString().toLowerCase();
                List<String> filteredList = new ArrayList<>();

                for (String title : mTitles) {
                    if (title.toLowerCase().contains(filterString)) {
                        filteredList.add(title);
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredTitles = (List<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
