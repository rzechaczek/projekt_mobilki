package com.cookbook.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cookbook.app.databinding.ActivityMainBinding;
import com.cookbook.app.ui.favorites.FavoritesFragment;
import com.cookbook.app.ui.home.HomeFragment;
import com.cookbook.app.ui.myrecipes.MyRecipesFragment;
import com.cookbook.app.ui.search.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (id == R.id.nav_search) {
                loadFragment(new SearchFragment());
                return true;
            } else if (id == R.id.nav_favorites) {
                loadFragment(new FavoritesFragment());
                return true;
            } else if (id == R.id.nav_my_recipes) {
                loadFragment(new MyRecipesFragment());
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}