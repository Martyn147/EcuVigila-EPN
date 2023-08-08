package com.example.ecuvigila;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar1); //Ignore red line errors
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
                break;

            case R.id.nav_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Mapa()).commit();
                break;

            case R.id.nav_infEmergencia:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new InfoEmergencia()).commit();
                break;

            case R.id.nav_numeroEmergencia:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ConEmergencia()).commit();
                break;

            case R.id.nav_Reportar:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ReportarEvento()).commit();
                break;
            case R.id.btnAjustes:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Ajustes()).commit();
                break;

            case R.id.nav_logout:
                Toast.makeText(this, "Cerrar Sesión!", Toast.LENGTH_SHORT).show();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
