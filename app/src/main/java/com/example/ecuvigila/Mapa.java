package com.example.ecuvigila;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Mapa extends Fragment implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseReference eventosRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapa, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        eventosRef = FirebaseDatabase.getInstance().getReference("eventos");

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng currentLocation = new LatLng(latitude, longitude);

                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("Mi Ubicación"));

                    float zoomLevel = 15f;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));
                }
            });
        }

        mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowClickListener(this);

        eventosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot eventoSnapshot : dataSnapshot.getChildren()) {
                    Double latitud = eventoSnapshot.child("latitud").getValue(Double.class);
                    Double longitud = eventoSnapshot.child("longitud").getValue(Double.class);
                    String detallesEvento = eventoSnapshot.child("detalles_evento").getValue(String.class);

                    if (latitud != null && longitud != null) {
                        LatLng eventLocation = new LatLng(latitud, longitud);
                        Marker marker = mMap.addMarker(new MarkerOptions().position(eventLocation).title(detallesEvento));
                        marker.setTag(eventoSnapshot.getKey()); // Guardar el ID del evento como etiqueta

                        Log.d("Mapa", "Marcador agregado con título: " + detallesEvento);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error al obtener datos: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null; // No usaremos ventanas personalizadas, solo el contenido predeterminado
    }


    @Override
    public View getInfoContents(Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.info_window_layout, null);

        String eventoId = (String) marker.getTag();
        if (eventoId != null) {
            DatabaseReference eventoRef = eventosRef.child(eventoId);
            eventoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    TextView tvNivelPeligro = view.findViewById(R.id.tvNivelPeligro);
                    TextView tvDetallesEvento = view.findViewById(R.id.tvDetallesEvento);
                    TextView tvComentario = view.findViewById(R.id.tvComentario);

                    String nivelPeligro = dataSnapshot.child("nivel_peligro").getValue(String.class);
                    String detallesEvento = dataSnapshot.child("detalles_evento").getValue(String.class);
                    String comentario = dataSnapshot.child("comentario").getValue(String.class);

                    tvNivelPeligro.setText("Nivel de peligro: " + nivelPeligro);
                    tvDetallesEvento.setText("Detalles del evento: " + detallesEvento);
                    tvComentario.setText("Comentario: " + comentario);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "Error al obtener detalles del evento: " + databaseError.getMessage());
                }
            });
        }

        return view;
    }






    @Override
    public void onInfoWindowClick(Marker marker) {
        // Aquí puedes implementar alguna acción al hacer clic en la ventana de información del marcador
    }
}
