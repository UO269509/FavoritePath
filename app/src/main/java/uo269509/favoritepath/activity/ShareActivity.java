package uo269509.favoritepath.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uo269509.favoritepath.R;
import uo269509.favoritepath.adapter.ListShareAdapter;
import uo269509.favoritepath.modelos.Amigo;

public class ShareActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    DocumentReference reference;
    RecyclerView amigoListView;
    ArrayList<Amigo> amigos = new ArrayList<>();
    Button iniciarRutaBtn;
    String rutaId;
    boolean circular;
    ArrayList<GeoPoint> puntos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        init();
        initializeAmigos();
        getDatosRuta();
        iniciarRuta();
    }

    private void iniciarRuta(){
        iniciarRutaBtn.setOnClickListener(view -> {
            navigationRoute();
        });
    }

    private void shareRuta(Amigo amigo){
        Map<String, Object> camposSolicitud = new HashMap<>();
        camposSolicitud.put("email",mAuth.getCurrentUser().getEmail());
        camposSolicitud.put("rutaId",rutaId);
        db.collection("usuarios").document(amigo.getNombre()).collection("solicitudes").document(rutaId).set(camposSolicitud);
        updateAmigos(amigo);
        Toast.makeText(this, "Solicitud enviada con éxito",Toast.LENGTH_SHORT).show();
    }

    private void init(){
        Intent i = getIntent();
        rutaId = i.getStringExtra("rutaId");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        reference = db.collection("usuarios").document(mAuth.getCurrentUser().getEmail());
        amigoListView = findViewById(R.id.shareRecyclerView);
        iniciarRutaBtn = findViewById(R.id.iniciarRutaBtn);
    }

    private void configureView() {
        amigoListView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        amigoListView.setLayoutManager(layoutManager);
    }

    private void addAdapter(ArrayList<Amigo> list) {
        ListShareAdapter laAdapter = new ListShareAdapter(list, amigo -> {
            shareRuta(amigo);
        });
        amigoListView.setAdapter(laAdapter);
    }

    private void cargarDatos() {
        reference.collection("amigos").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                amigos.add(new Amigo(doc.getId()));
            }
        });
    }

    public void updateAmigos(Amigo amigo) {
        amigos.remove(amigo);
        amigoListView.getAdapter().notifyDataSetChanged();
    }

    public void getDatosRuta() {
        final Executor EXECUTOR = Executors.newSingleThreadExecutor();
        final Handler HANDLER = new Handler(Looper.getMainLooper());
        EXECUTOR.execute(() -> {
            HANDLER.post(() -> {
                reference.collection("colecciones").document("Rutas propias").collection("rutas").document(rutaId).get().addOnSuccessListener(
                        documentSnapshot -> {
                            circular = documentSnapshot.getBoolean("circular");
                            puntos = (ArrayList<GeoPoint>) documentSnapshot.get("puntos");
                        }
                );
            });
        });
    }

    public void initializeAmigos() {
        final Executor EXECUTOR = Executors.newSingleThreadExecutor();
        final Handler HANDLER = new Handler(Looper.getMainLooper());
        EXECUTOR.execute(() -> {
            cargarDatos();
            HANDLER.post(() -> {
                db.collection("usuarios").document(mAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(
                        documentSnapshot -> {
                            if(documentSnapshot.exists()){
                                configureView();
                                addAdapter(amigos);
                            }
                        }
                );
            });

        });
    }

    private void navigationRoute(){
        NavigationRoute.Builder nav = NavigationRoute.builder(this).accessToken(getString(R.string.mapbox_access_token));
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission") Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            nav.addWaypoint(Point.fromLngLat(lastKnownLocation.getLongitude(),lastKnownLocation.getLatitude()));
        }
        for (GeoPoint g: puntos) {
            nav.addWaypoint(Point.fromLngLat(g.getLongitude(),g.getLatitude()));
        }
        if(circular)
            nav.addWaypoint(Point.fromLngLat(puntos.get(0).getLongitude(),puntos.get(0).getLatitude()));

        nav.profile("walking").build().getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if(response.body() == null){
                    Toast.makeText(getApplicationContext(), "No se encontró ninguna ruta.", Toast.LENGTH_SHORT).show();
                    return;
                }else if(response.body().routes().size() < 1){
                    Toast.makeText(getApplicationContext(), "No se encontró ninguna ruta.", Toast.LENGTH_SHORT).show();
                    return;
                }
                DirectionsRoute route = response.body().routes().get(0);
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .directionsRoute(route)
                        .build();

                NavigationLauncher.startNavigation(ShareActivity.this, options);
            }
            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "No se encontró ninguna ruta, compruebe su conexión.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}