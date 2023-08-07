package com.example.ecuvigila;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Informacion_de_emergencia extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_de_emergencia);

        // Obtener referencia al botón de regreso
        Button btnRegresar = findViewById(R.id.btnRegresar);

        // Implementar el evento onClick para el botón de regreso
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finalizar la actividad actual y regresar al MainActivity
                finish();
            }
        });
    }
}
