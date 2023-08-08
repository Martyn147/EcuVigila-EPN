package com.example.ecuvigila;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReportarEvento extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Spinner spinnerNivelPeligro;
    private Spinner spinnerDetalles;
    private EditText editTextComentario;
    private Button btnGuardar;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reportar_evento, container, false);

        spinnerNivelPeligro = view.findViewById(R.id.spinner_nivel_peligro);
        spinnerDetalles = view.findViewById(R.id.spinner_detalles);
        editTextComentario = view.findViewById(R.id.edit_text_comentario);
        btnGuardar = view.findViewById(R.id.btn_guardar);

        databaseReference = FirebaseDatabase.getInstance().getReference("eventos");

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ArrayAdapter<CharSequence> nivelPeligroAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.nivel_peligro, android.R.layout.simple_spinner_item);
        nivelPeligroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNivelPeligro.setAdapter(nivelPeligroAdapter);

        ArrayAdapter<CharSequence> detallesAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.detalles_evento, android.R.layout.simple_spinner_item);
        detallesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDetalles.setAdapter(detallesAdapter);

        btnGuardar.setOnClickListener(v -> guardarEvento());

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Agrega el marcador en una ubicación por defecto
        LatLng ubicacionPorDefecto = new LatLng(-0.180525, -78.467834);
        mMap.addMarker(new MarkerOptions().position(ubicacionPorDefecto).title("Marcador por defecto"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionPorDefecto, 15f));

        // Configurar el evento de clic en el mapa para agregar un marcador
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear(); // Limpia los marcadores existentes
            mMap.addMarker(new MarkerOptions().position(latLng).title("Nuevo marcador"));
        });
    }

    private void guardarEvento() {
        String nivelPeligro = spinnerNivelPeligro.getSelectedItem().toString();
        String detallesEvento = spinnerDetalles.getSelectedItem().toString();
        String comentario = editTextComentario.getText().toString();
        LatLng ubicacionMarcador = mMap.getCameraPosition().target; // Obtener la ubicación del marcador

        DatabaseReference nuevoEventoReference = databaseReference.push();
        nuevoEventoReference.child("nivel_peligro").setValue(nivelPeligro);
        nuevoEventoReference.child("detalles_evento").setValue(detallesEvento);
        nuevoEventoReference.child("comentario").setValue(comentario);
        nuevoEventoReference.child("latitud").setValue(ubicacionMarcador.latitude);
        nuevoEventoReference.child("longitud").setValue(ubicacionMarcador.longitude);

        // Limpiar los campos después de guardar
        spinnerNivelPeligro.setSelection(0);
        spinnerDetalles.setSelection(0);
        editTextComentario.setText("");
        mMap.clear(); // Limpia el marcador del mapa
    }
}
