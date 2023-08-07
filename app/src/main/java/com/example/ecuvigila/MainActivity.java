package com.example.ecuvigila;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtener referencias a los botones del menú
        Button btnMapa = findViewById(R.id.btnMapa);
        Button btnInfoEmergencia = findViewById(R.id.btnInfoEmergencia);
        Button btnCrearAviso = findViewById(R.id.btnCrearAviso);
        Button btnNumerosEmergencia = findViewById(R.id.btnNumerosEmergencia);
        Button btnAjustes = findViewById(R.id.btnAjustes);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Implementar eventos onClick para cada botón del menú
       btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Abrir la actividad "MapActivity"
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });



        btnInfoEmergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Abrir la actividad "Informacion_de_emergencia"
                startActivity(new Intent(MainActivity.this, Informacion_de_emergencia.class));
            }
        });

        btnCrearAviso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Abrir la actividad "CrearAvisoActivity"
                startActivity(new Intent(MainActivity.this, CrearAvisoActivity.class));
            }
        });

        btnNumerosEmergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Abrir la actividad "NumerosEmergenciaActivity"
                startActivity(new Intent(MainActivity.this, NumerosEmergenciaActivity.class));
            }
        });

        btnAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Abrir la actividad "AjustesActivity"
                startActivity(new Intent(MainActivity.this, AjustesActivity.class));
            }
        });

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implementar aquí la lógica para cerrar sesión si es necesario
            }
        });
    }
}
