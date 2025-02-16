package com.example.telestock.ui.applications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.telestock.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter implements Filterable {

    private List<String> mIds;
    private List<String> mServiceIds;
    private List<String> mTitles;
    private List<String> mNames;
    private List<String> mSurnames;
    private List<String> mPhones;
    private List<String> mDates;
    private List<String> mTimes;
/*    private List<String> mFios;*/

    private List<String> mProduct_quantitys;

    private List<String> mFilteredTitles; // Добавьте отфильтрованные заголовки
    private LayoutInflater mInflater;
    private ItemFilter mItemFilter = new ItemFilter();

    public ImageAdapter(Context mContext, List<String> mIds, List<String> mServiceIds, List<String> mTitles, List<String> mnames, List<String> msurnames, List<String> mphones, List<String> mdates, List<String> mtimes, List<String> mproduct_quantitys) {
        this.mIds = mIds;
        this.mServiceIds = mServiceIds;
        this.mTitles = mTitles;
        this.mNames = mnames;
        this.mSurnames = msurnames;
        this.mPhones = mphones;
        this.mDates = mdates;
        this.mTimes = mtimes;
/*        this.mFios = mfios;*/
        this.mProduct_quantitys = mproduct_quantitys;
        mFilteredTitles = new ArrayList<>(mTitles); // Инициализируйте отфильтрованные заголовки
        mInflater = LayoutInflater.from(mContext);
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
            convertView = mInflater.inflate(R.layout.grid_item_applications, parent, false);
        }

        //TextView user_idTextView = convertView.findViewById(R.id.user_id_text_view);
        //TextView service_idTextView = convertView.findViewById(R.id.service_id_text_view);
        TextView titleTextView = convertView.findViewById(R.id.TITLE_text_view);
        TextView nameTextView = convertView.findViewById(R.id.NAME_text_view);
        TextView surnameTextView = convertView.findViewById(R.id.SURNAME_text_view);
        TextView phoneTextView = convertView.findViewById(R.id.PHONE_text_view);
        TextView datesTextView = convertView.findViewById(R.id.DATE_text_view);
        TextView timesTextView = convertView.findViewById(R.id.TIME_text_view);
/*        TextView fiosTextView = convertView.findViewById(R.id.FIO_text_view);*/
        TextView product_quantitysTextView = convertView.findViewById(R.id.PRODUCT_QUANTITY_text_view);


        // Устанавливаем данные для каждого представления
        String title = mFilteredTitles.get(position);
        int originalPosition = mTitles.indexOf(title); // Получаем позицию в оригинальном списке

        //String user_id = mIds.get(originalPosition);
        //String service_id = mServiceIds.get(originalPosition);
        String name = mNames.get(position);
        String surname = mSurnames.get(position);
        String phone = mPhones.get(position);
        String date = mDates.get(position);
        String time = mTimes.get(position);
/*        String fio = mFios.get(originalPosition);*/
        String product_quantity = mProduct_quantitys.get(position);



        // Устанавливаем текст для текстовых представлений
        //user_idTextView.setText("Id юзера " + user_id);
        //service_idTextView.setText("Id услуги " + service_id);
        titleTextView.setText("Продукт: " + title);
        nameTextView.setText("Имя заказчика: " + name);
        surnameTextView.setText("Фамилия заказчика: " + surname);
        phoneTextView.setText("Телефон заказчика: " + phone);
        datesTextView.setText("Дата заказа: " + date);
        timesTextView.setText("Время заказа: " + time);
/*        fiosTextView.setText("Фамилия специалиста: " + fio);*/
        product_quantitysTextView.setText("Количество: " + product_quantity);


        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mItemFilter;
    }
    public void removeItem(int position) {
        mIds.remove(position);
        mServiceIds.remove(position);
        mTitles.remove(position);
        mNames.remove(position);
        mSurnames.remove(position);
        mPhones.remove(position);
        mDates.remove(position);
        mTimes.remove(position);
/*        mFios.remove(position);*/
        mProduct_quantitys.remove(position);
        mFilteredTitles.remove(position); // Remove from the filtered list as well
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
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
