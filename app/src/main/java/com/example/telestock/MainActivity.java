package com.example.telestock;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.example.telestock.ui.cart.CartManager;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.telestock.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Выход из личного кабинета");
                builder.setMessage("Вы действительно хотите выйти из личного кабинета? При выходе вам придется снова войти в свою учетную запись!");

                // Add the buttons
                builder.setPositiveButton("Выйти", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        exitAccount();
                        Intent intent = new Intent(MainActivity.this, RegistrationOrLogin.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("Остаться", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked No button
                        // Cancel the action or do nothing
                    }
                });

                // Create and show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        Menu menu = navigationView.getMenu();
        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderName = headerView.findViewById(R.id.header_name);
        TextView navHeaderEmail = headerView.findViewById(R.id.header_balance);

        SharedPreferences cartsharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String previousUser = cartsharedPreferences.getString("last_user", "");
        String currentUser = cartsharedPreferences.getString("login", ""); // Получаем текущего пользователя

        if (!previousUser.equals(currentUser)) {
            CartManager.getInstance(this).clearCart(); // Очищаем корзину, если пользователь сменился
        }

        cartsharedPreferences.edit().putString("last_user", currentUser).apply();


        // Получаем логин из SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String userLogin = sharedPreferences.getString("login", ""); // По умолчанию пустая строка
        String userName = sharedPreferences.getString("name", ""); // По умолчанию пустая строка
        String userSurname = sharedPreferences.getString("surname", ""); // По умолчанию пустая строка
        String userBalance = sharedPreferences.getString("balance", ""); // По умолчанию пустая строка

        // Если логин не "admin", скрываем пункты меню
        if (!userLogin.equals("admin")) {
            menu.findItem(R.id.nav_create_services).setVisible(false);
            menu.findItem(R.id.nav_create_news).setVisible(false);
            menu.findItem(R.id.nav_application).setVisible(false);
        }

        navHeaderName.setText("Привет - " + userName + " " + userSurname + "!");
        navHeaderEmail.setText("Ваш баланс: " + userBalance + "Br");

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_create_services, R.id.nav_create_news, R.id.nav_application)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    public void exitAccount() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear(); // This will remove all the data from SharedPreferences

        editor.apply(); // Apply the changes
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
