package uo269509.favoritepath.ui;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import uo269509.favoritepath.R;
import uo269509.favoritepath.activity.RutaActivity;
import uo269509.favoritepath.adapter.ListColeccionesAdapter;
import uo269509.favoritepath.adapter.ListRutasAdapter;
import uo269509.favoritepath.modelos.Coleccion;
import uo269509.favoritepath.modelos.Ruta;

public class ColeccionesFragment extends Fragment {

    View vista;
    EditText coleccion;
    Button coleccionBtn;
    Button rutaBtn;
    String titulo;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    DocumentReference reference;
    RecyclerView recyclerListView;
    ArrayList<Ruta> rutas = new ArrayList<>();
    ArrayList<Coleccion> colecciones = new ArrayList<>();
    public static final int RUTA = 1;

    public ColeccionesFragment() {}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RUTA && resultCode == RESULT_OK) {
            titulo = data.getStringExtra("coleccionBack");
            initializeRutas();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_colecciones, container, false);
        init(vista);
        initializeColecciones();
        crearRuta();
        crearColeccion();
        return vista;
    }

    private void crearRuta(){
        rutaBtn.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), RutaActivity.class);
            if(titulo != null)
                i.putExtra("coleccion",titulo);
            else
                i.putExtra("coleccion","Rutas propias");
            startActivityForResult(i, RUTA);
        });
    }

    private void crearColeccion(){
        coleccionBtn.setOnClickListener(v -> {
            String newColeccion = coleccion.getText().toString().trim();
            if(newColeccion != null && !newColeccion.isEmpty()){
                reference.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> nombreColecciones = (ArrayList<String>) documentSnapshot.get("nombreColecciones");
                        if(nombreColecciones.contains(newColeccion)){
                            Toast.makeText(this.getContext(), "Esa colección ya existe",Toast.LENGTH_SHORT).show();
                        }else{
                            nombreColecciones.add(newColeccion);
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("nombreColecciones", nombreColecciones);
                            reference.update(updates);
                            coleccion.setText("");
                            initializeColecciones();
                            Toast.makeText(this.getContext(), "Colección añadida correctamente",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                Toast.makeText(this.getContext(), "Escriba un nombre válido",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void borrarColeccion(Coleccion coleccion) {
        reference.collection("colecciones").document(coleccion.getTitulo()).collection("rutas").get().addOnSuccessListener(
                querySnapshot -> {
                    WriteBatch batch = db.batch();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        for(String url: (ArrayList<String>)document.get("imagenes")){
                            StorageReference fileRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                            fileRef.delete();
                        }
                        reference.collection("colecciones").document("Rutas propias").collection("rutas").document(document.getId()).delete();
                        batch.delete(document.getReference());
                    }
                    batch.commit();
                }
        );
        reference.collection("colecciones").document(coleccion.getTitulo()).delete();
        reference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                ArrayList<String> nombreColecciones = (ArrayList<String>) documentSnapshot.get("nombreColecciones");
                nombreColecciones.remove(coleccion.getTitulo());
                Map<String, Object> updates = new HashMap<>();
                updates.put("nombreColecciones", nombreColecciones);
                reference.update(updates);
            }
        });
        updateColecciones(coleccion);
        Toast.makeText(this.getContext(), "Colección borrada con éxito",Toast.LENGTH_SHORT).show();
    }

    private void abrirColeccion(Coleccion coleccion) {
        titulo = coleccion.getTitulo();
        initializeRutas();
    }

    private void init(View vista){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        reference = db.collection("usuarios").document(mAuth.getCurrentUser().getEmail());
        coleccion = vista.findViewById(R.id.editTextColeccion);
        coleccionBtn = vista.findViewById(R.id.coleccionBtn);
        rutaBtn = vista.findViewById(R.id.rutaBtn);
        recyclerListView = vista.findViewById(R.id.recyclerView);
    }

    private void configureView() {
        recyclerListView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerListView.setLayoutManager(layoutManager);
    }

    private void addColeccionAdapter(ArrayList<Coleccion> list) {
        ListColeccionesAdapter lcAdapter = new ListColeccionesAdapter(list, (coleccion, delete) -> {
            if(delete){
                borrarColeccion(coleccion);
            }else {
                abrirColeccion(coleccion);
            }
        });
        recyclerListView.setAdapter(lcAdapter);
    }

    private void cargarDatosColecciones() {
        reference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                ArrayList<String> nombreColecciones = (ArrayList<String>) documentSnapshot.get("nombreColecciones");
                colecciones = new ArrayList<>();
                for (String col: nombreColecciones) {
                    colecciones.add(new Coleccion(col));
                }
            }
        });
    }

    public void updateColecciones(Coleccion coleccion) {
        colecciones.remove(coleccion);
        recyclerListView.getAdapter().notifyDataSetChanged();
    }

    public void initializeColecciones() {
        final Executor EXECUTOR = Executors.newSingleThreadExecutor();
        final Handler HANDLER = new Handler(Looper.getMainLooper());
        EXECUTOR.execute(() -> {
            cargarDatosColecciones();
            HANDLER.post(() -> {
                db.collection("usuarios").document(mAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(
                        documentSnapshot -> {
                            if(documentSnapshot.exists()){
                                configureView();
                                addColeccionAdapter(colecciones);
                            }
                        }
                );
            });
            rutas.clear();
        });
    }

    private void borrarRuta(Ruta ruta) {
        for(String url : ruta.getImagenes()) {
            StorageReference fileRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
            fileRef.delete();
        }
        reference.collection("colecciones").document(titulo).collection("rutas").document(ruta.getId()).delete();
        if(!titulo.equals("Rutas propias")){
            reference.collection("colecciones").document("Rutas propias").collection("rutas").document(ruta.getId()).delete();
        }else{
            if(ruta.getOrigen() !=null)
                reference.collection("colecciones").document(ruta.getOrigen()).collection("rutas").document(ruta.getId()).delete();
        }
        updateRutas(ruta);
        Toast.makeText(this.getContext(), "Ruta borrada con éxito",Toast.LENGTH_SHORT).show();
    }

    private void abrirRuta(Ruta ruta) {
        Intent i = new Intent(getActivity(), RutaActivity.class);
        i.putExtra("coleccion",titulo);
        i.putExtra("rutaId", ruta.getId());
        startActivityForResult(i, RUTA);
    }

    public void updateRutas(Ruta ruta) {
        rutas.remove(ruta);
        recyclerListView.getAdapter().notifyDataSetChanged();
    }

    private void addRutaAdapter(ArrayList<Ruta> list) {
        ListRutasAdapter lrAdapter = new ListRutasAdapter(list, (ruta, delete) -> {
            if(delete){
                borrarRuta(ruta);
            }else {
                abrirRuta(ruta);
            }
        });
        recyclerListView.setAdapter(lrAdapter);
    }

    public void initializeRutas() {
        final Executor EXECUTOR = Executors.newSingleThreadExecutor();
        final Handler HANDLER = new Handler(Looper.getMainLooper());
        EXECUTOR.execute(() -> {
            rutas.clear();
            HANDLER.post(() -> {
                db.collection("usuarios").document(mAuth.getCurrentUser().getEmail()).collection("colecciones").document(titulo).collection("rutas").get().addOnSuccessListener(
                        queryDocumentSnapshots -> {
                            for(DocumentSnapshot d : queryDocumentSnapshots.getDocuments()){
                                Ruta ruta = new Ruta();
                                ruta.setId(d.getId());
                                ruta.setTitulo(d.getString("titulo"));
                                ruta.setDescripcion(d.getString("descripcion"));
                                ruta.setOrigen(d.getString("origen"));
                                ruta.setImagenes((ArrayList<String>) d.get("imagenes"));
                                rutas.add(ruta);
                            }
                            configureView();
                            addRutaAdapter(rutas);
                        }
                );
            });
            colecciones.clear();
        });
    }

    public boolean onBackPressed() {
        if(colecciones.isEmpty()){
            titulo = null;
            initializeColecciones();
            return false;
        }
        return true;
    }
}