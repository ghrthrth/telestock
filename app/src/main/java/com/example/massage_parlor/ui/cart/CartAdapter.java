package com.example.massage_parlor.ui.cart;

import static android.content.Context.MODE_PRIVATE;
import static com.example.massage_parlor.RegistrationOrLogin.getUserData;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;  // Библиотека для загрузки картинок
import com.example.massage_parlor.R;
import com.example.massage_parlor.ui.applications.HttpRequestTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private Context context;
    private List<Product> cartItems;
    private CartManager cartManager;
    private Runnable updateTotalPriceCallback;

    public CartAdapter(Context context, List<Product> cartItems, Runnable updateTotalPriceCallback) {
        this.context = context;
        this.cartItems = cartItems;
        this.cartManager = CartManager.getInstance(context);
        this.updateTotalPriceCallback = updateTotalPriceCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String userLogin = sharedPreferences.getString("login", ""); // По умолчанию пустая строка

        // Скрываем кнопку удаления, если логин не "admin"
        if (!userLogin.equals("admin")) {
            holder.btnRemove.setVisibility(View.GONE);
        } else {
            holder.btnRemove.setVisibility(View.VISIBLE);
        }

        Product product = cartItems.get(position);
        holder.txtProductName.setText(product.getName());
        holder.txtProductPrice.setText(product.getPrice() + " р");
        holder.txtProductQuantity.setText("" + product.getQuantity());

        // Загрузка изображения через Glide
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(holder.imgProduct);

        // Обработчик для кнопки "Купить"
        holder.btnBuy.setOnClickListener(v -> {
            // Создаём Map<String, String> вместо Map<String, Object>
            Map<String, String> params = new HashMap<>();

            Map<String, String> userData = getUserData(context);

            params.put("user_id", String.valueOf(Integer.parseInt(userData.get("id"))));
            params.put("service_id", String.valueOf(product.getId()));
            params.put("product_name", product.getName());  // Приводим ID к строке
            params.put("product_price", String.valueOf(product.getPrice()));  // Приводим ID к строке
            params.put("product_quantity", String.valueOf(product.getQuantity()));  // Приводим ID к строке

            // Получаем текущее системное время
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            String formattedDate = dateFormat.format(calendar.getTime());
            String formattedTime = timeFormat.format(calendar.getTime());

            // Добавляем дату и время в параметры
            params.put("dates", formattedDate);
            params.put("times", formattedTime);

            // Отправка данных на сервер
            new HttpRequestTask(context, "https://claimbes.store/massage_parlor/api/add_application/add.php", params).execute();

            cartManager.removeFromCart(product.getId());
            cartItems.clear();
            cartItems.addAll(cartManager.getCartItems());
            notifyDataSetChanged();
            updateTotalPriceCallback.run();
        });


        holder.btnRemove.setOnClickListener(v -> {
            cartManager.removeFromCart(product.getId());
            cartItems.clear();
            cartItems.addAll(cartManager.getCartItems());
            notifyDataSetChanged();
            updateTotalPriceCallback.run();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtProductPrice, txtProductQuantity;
        ImageView imgProduct;  // Добавляем ImageView
        Button btnRemove, btnBuy; // Добавляем кнопку "Купить"

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            txtProductQuantity = itemView.findViewById(R.id.txtProductQuantity);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnBuy = itemView.findViewById(R.id.btnBuy);  // Привязываем кнопку "Купить"
        }
    }
}
