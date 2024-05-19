package com.example.coursepaper;

import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    public ActionBarDrawerToggle toggle;
    private View headerView;
    private TextView usernameTextView, emailTextView;


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
        View headerView = navigationView.getHeaderView(0);

        TextView usernameTextView = headerView.findViewById(R.id.username);
        TextView emailTextView = headerView.findViewById(R.id.email);
        ImageView backgroundImageImageView = headerView.findViewById(R.id.backgroundImage);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String username = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);


                    usernameTextView.setText(username);
                    emailTextView.setText(email);
                    Glide.with(MainActivity.this)
                            .load(imageUrl)
                            .into(backgroundImageImageView);

                    // вызов метода updateHeader() для обновления заголовка навигационного меню

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // обработка ошибки
                }
            });
        }




        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.home) {
                    replaceFragment(new MainFragment());
                } else if (id == R.id.contact) {
                    replaceFragment(new ContactFragment());
                } else if (id == R.id.settings) {
                    replaceFragment(new SettingsFragment());
                } else if (id == R.id.exit){
                    FirebaseAuth.getInstance().signOut();
                    replaceFragment(new EnterFragment());
                }else if (id == R.id.about_us){
                    replaceFragment(new AboutUsFragment());
                }


                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            replaceFragment(new EnterFragment());

        } else {
            replaceFragment(new MainFragment());

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

    public DrawerLayout getDrawerLayout() {
        return drawer;
    }

    public ActionBarDrawerToggle getToggle() {
        return toggle;
    }

//    public void updateHeader() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());
//            databaseReference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    String username = snapshot.child("username").getValue(String.class);
//                    String email = snapshot.child("email").getValue(String.class);
//
//                    usernameTextView.setText(username);
//                    emailTextView.setText(email);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    // обработка ошибки
//                }
//            });
//        }
//    }
    @Override
    protected void onStart() {
        super.onStart();
//        updateHeader();
    }

}
