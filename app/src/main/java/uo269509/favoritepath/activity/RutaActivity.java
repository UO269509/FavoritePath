package uo269509.favoritepath.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import uo269509.favoritepath.R;
import uo269509.favoritepath.adapter.ImagePagerAdapter;

public class RutaActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseStorage storage;
    DocumentReference reference;
    FloatingActionButton saveBtn;
    String rutaId;
    String idAuxiliar;
    String origen;
    String nombreColeccion;
    EditText titulo;
    EditText descripcion;
    CheckBox publico;
    CheckBox circular;
    ViewPager viewPager;
    Button imageBtn;
    Button initRutaBtn;
    ArrayList<String> mImageUris = new ArrayList<>();
    List<String> imagenes = new ArrayList<>();
    MapView mapView;
    MapboxMap mapboxMap;
    ArrayList<GeoPoint> puntos = new ArrayList<>();

    /**
     * Método utilizado para volver a la pantalla anterior.
     * Se encarga de borrar las imágenes temporales del almacenamiento
     * en el caso de no guardar la ruta.
     */
    @Override
    public void onBackPressed() {
        if(rutaId == null && !imagenes.isEmpty()){
            for (String url: imagenes) {
                StorageReference fileRef = storage.getReferenceFromUrl(url);
                fileRef.delete();
            }
        }
        super.onBackPressed();
    }

    /**
     * Método para recibir la respuesta de la selección de la imagen desde la galería
     * @param requestCode El código de la petición
     * @param resultCode El código del resultado
     * @param data El intento con los datos
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            StorageReference imageRef = null;
            if (rutaId != null) {
                imageRef = storage.getReference().child("images").child(rutaId + "_imagen" + imagenes.size() + ".jpg");
            } else {
                imageRef = storage.getReference().child("images").child(idAuxiliar + "_imagen" + imagenes.size() + ".jpg");
            }
            UploadTask uploadTask = imageRef.putFile(selectedImageUri);
            StorageReference finalImageRef = imageRef;
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                finalImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    imagenes.add(downloadUrl);
                    mImageUris.add(downloadUrl);
                    ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(mImageUris);
                    viewPager.setAdapter(imagePagerAdapter);
                    imagePagerAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Imagen cargada, deslice para ver todas las imágenes.", Toast.LENGTH_SHORT).show();
                });
            });
        }
    }

    /**
     * Método para generar la actividad, obtiene las referencias de los elementos y coloca los listeners a los botones.
     * Llama a los métodos init(), initializeRuta(savedInstanceState), initMapa(savedInstanceState), guardarRuta(), guardarImagenes() y iniciarRuta().
     * @param savedInstanceState El estado de la instancia.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_ruta);
        init();
        if(rutaId != null)
            initializeRuta(savedInstanceState);
        else
            initMapa(savedInstanceState);
        guardarRuta();
        guardarImagenes();
        iniciarRuta();
    }

    /**
     * Método para asignarle el listener al botón de iniciar ruta con sus respectivas funciones.
     */
    private void iniciarRuta() {
        initRutaBtn.setOnClickListener(v -> {
            if(rutaId == null){
                Toast.makeText(this, "No se puede iniciar hasta que no se guarde la ruta.", Toast.LENGTH_SHORT).show();
            }else {
                if(puntos.isEmpty()){
                    Toast.makeText(this, "Eliga primero destinos para visitar.", Toast.LENGTH_SHORT).show();
                }else {
                    actualizarRuta(false);
                    Intent intent = new Intent(RutaActivity.this, ShareActivity.class);
                    intent.putExtra("rutaId", rutaId);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Método para asignarle el listener al botón de guardar imágenes y que abra un intento
     * para elegir las imágenes.
     */
    private void guardarImagenes() {
        imageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        });
    }

    /**
     * Método para asignarle el listener al botón de guardar ruta con sus respectivas funciones.
     */
    private void guardarRuta() {
        saveBtn.setOnClickListener(v -> {
            if(rutaId != null){
                actualizarRuta(true);
            }else{
                crearRuta();
            }
        });
    }

    /**
     * Método para la creación de una ruta, donde se recojen todos los datos proporcionados.
     */
    private void crearRuta(){
        String newTitulo = titulo.getText().toString().trim();
        if(newTitulo != null && !newTitulo.isEmpty()){
            String newDescripcion = descripcion.getText().toString();
            boolean newPublico = publico.isChecked();
            boolean newCircular = circular.isChecked();
            Map<String, Object> camposRuta = new HashMap<>();
            camposRuta.put("titulo",newTitulo);
            camposRuta.put("descripcion",newDescripcion);
            camposRuta.put("publico",newPublico);
            camposRuta.put("circular",newCircular);
            camposRuta.put("puntos",puntos);
            camposRuta.put("imagenes", imagenes);
            reference.collection("colecciones").document(nombreColeccion).collection("rutas").document(idAuxiliar).set(camposRuta);
            if(!nombreColeccion.equals("Rutas propias")){
                camposRuta.put("origen",nombreColeccion);
                reference.collection("colecciones").document("Rutas propias").collection("rutas").document(idAuxiliar).set(camposRuta);
            }
            Toast.makeText(this, "Ruta creada correctamente", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra("coleccionBack", nombreColeccion);
            setResult(RESULT_OK, intent);
            finish();
        }else{
            Toast.makeText(this, "Escriba un título",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Método para la actualización de una ruta, donde se recojen todos los datos proporcionados.
     */
    private void actualizarRuta(boolean finish){
        String newTitulo = titulo.getText().toString().trim();
        if(newTitulo != null && !newTitulo.isEmpty()){
            String newDescripcion = descripcion.getText().toString();
            boolean newPublico = publico.isChecked();
            boolean newCircular = circular.isChecked();
            DocumentReference auxReference = reference.collection("colecciones").document(nombreColeccion).collection("rutas").document(rutaId);
            auxReference.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("titulo", newTitulo);
                    updates.put("descripcion",newDescripcion);
                    updates.put("publico",newPublico);
                    updates.put("circular",newCircular);
                    updates.put("imagenes",imagenes);
                    updates.put("puntos",puntos);
                    auxReference.update(updates);
                    if(documentSnapshot.getString("origen") != null){
                        reference.collection("colecciones").document(documentSnapshot.getString("origen")).collection("rutas").document(rutaId).update(updates);
                    }
                    if(!nombreColeccion.equals("Rutas propias")){
                        reference.collection("colecciones").document("Rutas propias").collection("rutas").document(rutaId).update(updates);
                    }
                    if(finish) {
                        Toast.makeText(this, "Ruta actualizada correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("coleccionBack", nombreColeccion);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            });
        }else{
            Toast.makeText(this, "Escriba un título",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Método encargado de cargar la ruta y asignár los datos a los elementos de la vista.
     * @param savedInstanceState El estado de la instancia
     */
    public void initializeRuta(Bundle savedInstanceState) {
        final Executor EXECUTOR = Executors.newSingleThreadExecutor();
        final Handler HANDLER = new Handler(Looper.getMainLooper());
        EXECUTOR.execute(() -> {
            HANDLER.post(() -> {
                reference.collection("colecciones").document(nombreColeccion).collection("rutas").document(rutaId).get().addOnSuccessListener(
                        documentSnapshot -> {
                            origen = documentSnapshot.getString("origen");
                            titulo.setText(documentSnapshot.getString("titulo"));
                            descripcion.setText(documentSnapshot.getString("descripcion"));
                            publico.setChecked(documentSnapshot.getBoolean("publico"));
                            circular.setChecked(documentSnapshot.getBoolean("circular"));
                            imagenes = (ArrayList<String>) documentSnapshot.get("imagenes");
                            for (String s: imagenes) {
                                mImageUris.add(s);
                            }
                            ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(mImageUris);
                            viewPager.setAdapter(imagePagerAdapter);
                            puntos = (ArrayList<GeoPoint>) documentSnapshot.get("puntos");
                            initMapa(savedInstanceState);
                        }
                );
            });
        });
    }

    /**
     * Método encargado de cargar el mapa en la vista, donde el centro será la localización del usuario.
     * @param savedInstanceState El estado de la instancia
     */
    private void initMapa(Bundle savedInstanceState){
        final Executor EXECUTOR = Executors.newSingleThreadExecutor();
        final Handler HANDLER = new Handler(Looper.getMainLooper());
        EXECUTOR.execute(() -> {
            HANDLER.post(() -> {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                @SuppressLint("MissingPermission") Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                //Coordenadas de Oviedo si el usuario no tiene localización
                double latitude = 43.3657013;
                double longitude = -5.858561;
                if (lastKnownLocation != null) {
                    latitude = lastKnownLocation.getLatitude();
                    longitude = lastKnownLocation.getLongitude();
                }
                final GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                mapView = findViewById(R.id.mapView);
                mapView.onCreate(savedInstanceState);
                mapView.getMapAsync(mapboxMap -> {
                    this.mapboxMap = mapboxMap;
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()))
                                    .zoom(12)
                                    .build()
                    ));
                    mapboxMap.getUiSettings().setZoomGesturesEnabled(true);
                    mapboxMap.getUiSettings().setCompassEnabled(true);
                    mapboxMap.setStyle(Style.SATELLITE_STREETS);
                    mapboxMap.getUiSettings().setAttributionEnabled(false);
                    for (int i = 0; i < puntos.size(); i++) {
                        GeoPoint g = puntos.get(i);
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(g.getLatitude(),g.getLongitude()))
                                .title("Punto Nº" + (i+1)));
                    }
                    mapboxMap.addOnMapClickListener(point -> {
                        puntos.add(new GeoPoint(point.getLatitude(), point.getLongitude()));
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(point)
                                .title("Punto Nº" + puntos.size()));
                        return true;
                    });

                    mapboxMap.setOnMarkerClickListener(marker -> {
                        if(marker.getTitle().equals("Punto Nº" + puntos.size())) {
                            puntos.remove(puntos.size() -1);
                            mapboxMap.removeMarker(marker);
                        }else{
                            Toast.makeText(this, "Solo puede borrar el último punto marcado.", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    });
                });
            });
        });
    }

    /**
     * Método para instanciar todos los elementos necesarios en la clase.
     */
    private void init(){
        Intent i = getIntent();
        nombreColeccion = i.getStringExtra("coleccion");
        rutaId = i.getStringExtra("rutaId");
        idAuxiliar = UUID.randomUUID().toString();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        reference = db.collection("usuarios").document(mAuth.getCurrentUser().getEmail());
        saveBtn = findViewById(R.id.saveButton);
        titulo = findViewById(R.id.tituloTexto);
        descripcion = findViewById(R.id.descripcionTexto);
        publico = findViewById(R.id.checkBoxPublica);
        circular = findViewById(R.id.checkBoxCircular);
        viewPager = findViewById(R.id.viewPager);
        imageBtn = findViewById(R.id.addImageButton);
        initRutaBtn = findViewById(R.id.initRutaBtn);
    }

}