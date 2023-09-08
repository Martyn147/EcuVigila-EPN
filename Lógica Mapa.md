# Código de Mapa con Firebase y Google Maps

...
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
...

Este Segmento explica un fragmento de código de una aplicación Android que utiliza Firebase y Google Maps para mostrar la ubicación en tiempo real de usuarios y eventos en un mapa interactivo. El código está escrito en Java y se encuentra en una clase llamada `Mapa`. A continuación, se describen las principales funcionalidades y componentes del código:

## Descripción General

El código implementa un fragmento de mapa que muestra la ubicación en tiempo real de usuarios y eventos en un mapa de Google Maps. Utiliza Firebase Realtime Database para almacenar y obtener datos de ubicación de usuarios y eventos. Además, utiliza la API de ubicación de Google para obtener y mostrar la ubicación del usuario actual.

## Componentes Principales

### Variables miembro

- `mMap`: Una instancia de `GoogleMap` para interactuar con el mapa.
- `fusedLocationProviderClient`: Un cliente de proveedor de ubicación fusionado para obtener la ubicación del usuario.
- `eventosRef`: Una referencia a la base de datos de Firebase que almacena información sobre eventos.
- `ubicacionUsuarioRef`: Una referencia a la base de datos de Firebase que almacena la ubicación de los usuarios.
- `usuarioActualId`: El ID del usuario actualmente autenticado.
- `markerEventoMap`: Un mapa que asocia marcadores en el mapa con IDs de eventos.
- `usuariosMarkers`: Un mapa que asocia marcadores en el mapa con IDs de usuarios.

### Métodos

#### `onCreateView`

Este método se llama cuando se crea la vista del fragmento. En él, se inicializan las referencias a Firebase, se obtiene el ID del usuario actual y se configura el mapa.

#### `onMapReady`

Este método se llama cuando el mapa de Google está listo para su uso. Aquí se configura el mapa, se agregan marcadores de eventos y se inicia la obtención de ubicación del usuario.

#### `getInfoWindow` y `getInfoContents`

Estos métodos personalizan las ventanas de información que aparecen cuando se hace clic en un marcador en el mapa. En `getInfoContents`, se muestra el correo asociado al marcador de usuario.

#### `onInfoWindowClick`

Este método se llama cuando se hace clic en la ventana de información de un marcador en el mapa. Puedes implementar acciones adicionales aquí.

#### Otros métodos

- `obtenerIdUsuarioActual`: Este método utiliza Firebase Authentication para obtener el ID del usuario actualmente autenticado.

- `obtenerUbicacionActual`: Este método verifica los permisos de ubicación y obtiene la ubicación actual del usuario.

- `guardarUbicacionUsuario`: Este método guarda la ubicación del usuario actual en Firebase Realtime Database junto con su correo.

## Uso

Para utilizar este código en tu propia aplicación Android, debes seguir estos pasos:

1. Configura un proyecto en Firebase y configura la base de datos en tiempo real y la autenticación según tus necesidades.

2. Asegúrate de tener las dependencias necesarias en tu archivo `build.gradle` para Firebase, Google Maps y Location Services.

3. Implementa el método `obtenerIdUsuarioActual` para obtener el ID del usuario actual basado en tu sistema de autenticación.

4. Personaliza la lógica según tus necesidades, como la forma en que se muestran los marcadores y cómo se manejan las interacciones de los usuarios.

5. Asegúrate de que tu aplicación tenga los permisos necesarios para acceder a la ubicación del dispositivo.

6. Agrega un diseño XML para la ventana de información personalizada si deseas personalizarla.

7. Integra este fragmento en tu actividad principal o en la parte de la aplicación donde deseas mostrar el mapa.

Este código proporciona una base sólida para desarrollar una aplicación de seguimiento de ubicación en tiempo real utilizando Firebase y Google Maps en Android. Asegúrate de comprender y personalizar el código según tus necesidades específicas.
