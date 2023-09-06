package com.example.ecuvigila;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Mapa extends Fragment implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseReference eventosRef;
    private DatabaseReference ubicacionUsuarioRef;
    private String usuarioActualId;
    private Map<Marker, String> markerEventoMap = new HashMap<>();
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private LocationCallback locationCallback;
    private ValueEventListener ubicacionesUsuariosListener;
    private Map<String, Marker> usuariosMarkers = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapa, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        eventosRef = FirebaseDatabase.getInstance().getReference("eventos");
        ubicacionUsuarioRef = FirebaseDatabase.getInstance().getReference("ubicaciones_usuarios");

        usuarioActualId = obtenerIdUsuarioActual(); // Implementa este método para obtener el ID del usuario actual

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Agrega un ValueEventListener para escuchar las ubicaciones de los usuarios en Firebase
        ubicacionesUsuariosListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Itera a través de los datos y actualiza o agrega los marcadores de usuarios en el mapa
                for (DataSnapshot usuarioSnapshot : dataSnapshot.getChildren()) {
                    String usuarioId = usuarioSnapshot.getKey();
                    Double latitud = usuarioSnapshot.child("latitud").getValue(Double.class);
                    Double longitud = usuarioSnapshot.child("longitud").getValue(Double.class);
                    String correo = usuarioSnapshot.child("correo").getValue(String.class);

                    if (usuarioId != null && latitud != null && longitud != null) {
                        // Verifica si el marcador ya existe
                        if (usuariosMarkers.containsKey(usuarioId)) {
                            // Si existe, actualiza su posición
                            Marker marker = usuariosMarkers.get(usuarioId);
                            LatLng nuevaUbicacion = new LatLng(latitud, longitud);
                            marker.setPosition(nuevaUbicacion);
                        } else {
                            // Si no existe, crea un nuevo marcador
                            LatLng ubicacionUsuario = new LatLng(latitud, longitud);
                            Marker marker = mMap.addMarker(new MarkerOptions().position(ubicacionUsuario).title("Usuario " + usuarioId));

                            // Asocia el correo del usuario con el marcador
                            marker.setTag(correo);

                            usuariosMarkers.put(usuarioId, marker);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de Firebase
            }
        };



        // Agrega el ValueEventListener a la ubicación de los usuarios en Firebase
        ubicacionUsuarioRef.addValueEventListener(ubicacionesUsuariosListener);


        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng currentLocation = new LatLng(latitude, longitude);

                    float zoomLevel = 19f;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));

                    // Iniciar la obtención de ubicación del usuario
                    guardarUbicacionUsuario(latitude, longitude);
                }
            });
        }

        // Configurar un listener de ubicación para seguir la ubicación en tiempo real
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location newLocation = locationResult.getLastLocation();
                    LatLng newLatLng = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());

                    // Actualiza la ubicación del usuario en Firebase
                    guardarUbicacionUsuario(newLatLng.latitude, newLatLng.longitude);
                }
            }
        };

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // Intervalo en milisegundos para obtener actualizaciones de ubicación
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);


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
                        markerEventoMap.put(marker, eventoSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de Firebase
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

        // Obtén el correo asociado al marcador
        String correo = (String) marker.getTag();

        if (correo != null) {
            TextView tvCorreo = view.findViewById(R.id.tvCorreo);
            tvCorreo.setText("Usuario: " + correo);
        }

        return view;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        // Aquí puedes implementar alguna acción al hacer clic en la ventana de información del marcador
    }

    // Agrega la función para obtener el ID del usuario actual según tu sistema de autenticación
    private String obtenerIdUsuarioActual() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            // Si el usuario no está autenticado, devuelve null o realiza alguna otra acción
            return null;
        }
    }
    private void obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si los permisos no están otorgados, solicitarlos al usuario
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            // Si los permisos ya están otorgados, puedes iniciar la obtención de ubicación
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng currentLocation = new LatLng(latitude, longitude);

                    float zoomLevel = 19f;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));

                    // Resto del código para configurar la ubicación en tiempo real
                }
            });
        }
    }
    private void guardarUbicacionUsuario(double latitud, double longitud) {
        if (usuarioActualId != null) {
            // Obtener el correo del usuario actualmente autenticado
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser usuarioActual = auth.getCurrentUser();

            if (usuarioActual != null) {
                String correoUsuario = usuarioActual.getEmail();

                // Crear un mapa con los datos de ubicación, incluyendo el correo
                Map<String, Object> ubicacion = new HashMap<>();
                ubicacion.put("latitud", latitud);
                ubicacion.put("longitud", longitud);
                ubicacion.put("timestamp", ServerValue.TIMESTAMP);
                ubicacion.put("correo", correoUsuario);

                // Guardar los datos de ubicación en Firebase Realtime Database
                ubicacionUsuarioRef.child(usuarioActualId).setValue(ubicacion);
            }
        }
    }

}
