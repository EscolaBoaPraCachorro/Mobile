package com.example.escolaboaparacachorro;

import android.os.Bundle;
import android.view.Menu;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.escolaboaparacachorro.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setItemIconTintList(null);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.boletim, R.id.home, R.id.aumigos)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        binding.navView.setOnItemSelectedListener(item -> {


            Menu menu = binding.navView.getMenu();
            menu.findItem(R.id.home).setIcon(R.drawable.home_off);
            menu.findItem(R.id.boletim).setIcon(R.drawable.boletim_off);
            menu.findItem(R.id.aumigos).setIcon(R.drawable.aumigos_off);


            int id = item.getItemId();
            if (id == R.id.home) {
                item.setIcon(R.drawable.home_on);
            } else if (id == R.id.boletim) {
                item.setIcon(R.drawable.boletim_on);
            } else if (id == R.id.aumigos) {
                item.setIcon(R.drawable.aumigos_on);
            }


            return NavigationUI.onNavDestinationSelected(item, navController)
                    || super.onOptionsItemSelected(item);
        });
    }
}