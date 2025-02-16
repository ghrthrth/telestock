package com.example.massage_parlor.ui.cart;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CartManager {
    private static final String CART_PREFS = "cart_prefs";
    private static final String CART_ITEMS_PREFIX = "cart_items_"; // Добавляем префикс

    private static CartManager instance;
    private List<Product> cartItems;
    private SharedPreferences prefs;

    private CartManager(Context context) {
        prefs = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        loadCart();
    }

    public static CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    private void loadCart() {
        Gson gson = new Gson();
        String userId = getCurrentUserId(); // Получаем ID текущего пользователя
        String json = prefs.getString(CART_ITEMS_PREFIX + userId, null);
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        cartItems = (json == null || json.isEmpty()) ? new ArrayList<>() : gson.fromJson(json, type);
    }

    private void saveCart() {
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String userId = getCurrentUserId();
        editor.putString(CART_ITEMS_PREFIX + userId, gson.toJson(cartItems));
        editor.apply();
    }
    private String getCurrentUserId() {
        SharedPreferences sharedPreferences = prefs; // Используем те же SharedPreferences
        return sharedPreferences.getString("user_id", "guest"); // По умолчанию "guest"
    }
    public List<Product> getCartItems() {
        return new ArrayList<>(cartItems);  // Возвращаем копию списка, чтобы избежать изменения исходного
    }

    public void addToCart(Product product) {
        for (Product p : cartItems) {
            if (p.getId() == product.getId()) {
                p.setQuantity(p.getQuantity() + 1);
                saveCart();
                return;
            }
        }
        cartItems.add(product);
        saveCart();
    }

    public void removeFromCart(int productId) {
        for (Iterator<Product> iterator = cartItems.iterator(); iterator.hasNext();) {
            Product item = iterator.next();
            if (item.getId() == productId) {
                iterator.remove(); // Полностью удаляем товар
                break;
            }
        }
    }

    public void clearCart() {
        cartItems.clear(); // Очищаем список товаров
        saveCart(); // Сохраняем пустую корзину в SharedPreferences
    }

}
