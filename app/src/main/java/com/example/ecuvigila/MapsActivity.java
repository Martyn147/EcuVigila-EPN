package com.example.ecuvigila;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MapsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Mapa()).commit();
        }
    }
}
