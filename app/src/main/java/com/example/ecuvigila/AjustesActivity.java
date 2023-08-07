package com.example.ecuvigila;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AjustesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        // Obtener referencia al botón "Regresar" en la actividad "Ajustes"
        Button btnRegresar = findViewById(R.id.btnRegresar);

        // Implementar evento onClick para el botón "Regresar"
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Volver al menú principal
                finish();
            }
        });
    }
}
