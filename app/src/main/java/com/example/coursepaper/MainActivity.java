package com.example.coursepaper;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  включить, если надо будет изначально бургер меню включенным быть

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.home) {
                    Toast.makeText(MainActivity.this, "Ты уже дома", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.contact) {
                    Toast.makeText(MainActivity.this, "Скоро здесь будут контакты", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.settings) {
                    Toast.makeText(MainActivity.this, "Когда нибудь реализуются настройки", Toast.LENGTH_SHORT).show();
                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            EnterFragment enterFragment = new EnterFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, enterFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            MainFragment mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mainFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}
