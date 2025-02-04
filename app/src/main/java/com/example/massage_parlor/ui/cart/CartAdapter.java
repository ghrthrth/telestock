package com.example.massage_parlor.ui.cart;

import android.content.Context;
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
import java.util.List;

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
        Product product = cartItems.get(position);
        holder.txtProductName.setText(product.getName());
        holder.txtProductPrice.setText(product.getPrice() + " ₽");
        holder.txtProductQuantity.setText("Кол-во: " + product.getQuantity());

        // Загрузка изображения через Glide
        Glide.with(context)
                .load(product.getImageUrl())  // Берём URL из объекта Product
                .placeholder(R.drawable.ic_placeholder) // Заглушка
                .error(R.drawable.ic_placeholder) // Если ошибка
                .into(holder.imgProduct);

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
        Button btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            txtProductQuantity = itemView.findViewById(R.id.txtProductQuantity);
            imgProduct = itemView.findViewById(R.id.imgProduct); // Привязываем ImageView
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
