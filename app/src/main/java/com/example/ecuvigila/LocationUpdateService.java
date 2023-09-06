package com.example.ecuvigila;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import com.google.firebase.database.ServerValue;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LocationUpdateService extends Service {
    private static final String TAG = "LocationUpdateService";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private static final long INTERVALO_DE_ACTUALIZACION = 5000; // Intervalo de actualización en milisegundos (1 segundo)
    private static final int NOTIFICATION_ID = 123;
    private static final String NOTIFICATION_CHANNEL_ID = "location_service_channel";

    private DatabaseReference ubicacionUsuarioRef;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        ubicacionUsuarioRef = FirebaseDatabase.getInstance().getReference("ubicaciones_usuarios");

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        // Aquí puedes procesar la ubicación actualizada y enviarla a Firebase
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.d(TAG, "Latitud: " + latitude + ", Longitud: " + longitude);

                        // Envía la ubicación a Firebase
                        enviarUbicacionFirebase(latitude, longitude);
                    }
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Configura una notificación para el servicio en primer plano
        createNotificationChannel();
        Notification notification = buildNotification();

        // Inicia el servicio en primer plano
        startForeground(NOTIFICATION_ID, notification);

        requestLocationUpdates(); // Inicia la actualización de ubicación cuando se inicia el servicio.
        return START_STICKY; // Para que el servicio se reinicie si se detiene.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeLocationUpdates();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void requestLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(INTERVALO_DE_ACTUALIZACION);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permiso de ubicación denegado.");
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void removeLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Location Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_simple)
                .setContentTitle("Mi Aplicación")
                .setContentText("Servicio en primer plano activo");

        return builder.build();
    }

    private void enviarUbicacionFirebase(double latitud, double longitud) {
        // Asegúrate de que el usuarioActualId tenga el ID del usuario actual
        String usuarioActualId = obtenerIdUsuarioActual();

        if (usuarioActualId != null) {
            // Obtener el correo del usuario actualmente autenticado
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser usuarioActual = auth.getCurrentUser();

            if (usuarioActual != null) {
                String correoUsuario = "xd";

                // Crear un mapa con los datos de ubicación, incluyendo el correo
                Map<String, Object> ubicacion = new HashMap<>();
                ubicacion.put("latitud", latitud);
                ubicacion.put("longitud", longitud);
                ubicacion.put("timestamp", ServerValue.TIMESTAMP);
                ubicacion.put("correo", correoUsuario);

                // Guardar los datos de ubicación en Firebase Realtime Database
                DatabaseReference ubicacionUsuarioActualRef = ubicacionUsuarioRef.child(usuarioActualId);
                ubicacionUsuarioActualRef.setValue(ubicacion);
            }
        }
    }

    private String obtenerIdUsuarioActual() {
        // Implementa la lógica para obtener el ID del usuario actual
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser usuarioActual = auth.getCurrentUser();
        if (usuarioActual != null) {
            // Si el usuario actual no es nulo, devuelve su UID (identificador único)
            return usuarioActual.getUid();
        } else {
            // Si no hay usuario autenticado, devuelve null o una cadena vacía, según lo prefieras
            return null;
        }
    }
}

