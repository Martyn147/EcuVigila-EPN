package com.example.ecuvigila;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NumerosEmergenciaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numeros_emergencia);

        // Obtener referencia al botón "Regresar" en la actividad "Números de emergencia"
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
