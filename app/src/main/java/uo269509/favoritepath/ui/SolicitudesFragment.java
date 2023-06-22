package uo269509.favoritepath.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import uo269509.favoritepath.R;
import uo269509.favoritepath.adapter.ListSolicitudesAdapter;
import uo269509.favoritepath.modelos.Solicitud;

public class SolicitudesFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    DocumentReference reference;
    RecyclerView solicitudListView;
    ArrayList<Solicitud> solicitudes = new ArrayList<>();

    public SolicitudesFragment() {}

    /**
     * Método para generar la actividad.
     * @param savedInstanceState El estado de la instancia.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Método para crear el fragmento y añadírselo a la actividad correspondiente.
     * @param inflater Parámetro para inflar el fragmento en la vista
     * @param container El contenedor de la vista
     * @param savedInstanceState El estado de la instancia
     * @return La vista
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_solicitudes, container, false);
        init(vista);
        initializeSolicitudes();
        return vista;
    }

    /**
     * Método para instanciar todos los elementos necesarios en la clase.
     */
    private void init(View vista){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        reference = db.collection("usuarios").document(mAuth.getCurrentUser().getEmail());
        solicitudListView = vista.findViewById(R.id.recyclerView);
    }

    /**
     * Método encargado de aceptar la solicitud de la lista del usuario.
     * @param solicitud La solicitud que será aceptada
     */
    private void aceptarSolicitud(Solicitud solicitud) {
        if(solicitud.getRutaId() == null) { //Aquí es la solicitud de amistad
            //añadimos a cada usuario al otro usuario como un amigo
            Map<String, Object> camposSolicitudAmigo1 = new HashMap<>();
            camposSolicitudAmigo1.put("email", solicitud.getTitulo());
            reference.collection("amigos").document(solicitud.getTitulo()).set(camposSolicitudAmigo1);
            Map<String, Object> camposSolicitudAmigo2 = new HashMap<>();
            camposSolicitudAmigo2.put("email", mAuth.getCurrentUser().getEmail());
            db.collection("usuarios").document(solicitud.getTitulo()).collection("amigos").document(mAuth.getCurrentUser().getEmail()).set(camposSolicitudAmigo2);
            //eliminamos la solicitud para que ya no aparezca
            reference.collection("solicitudes").document(solicitud.getTitulo()).delete();
            db.collection("usuarios").document(solicitud.getTitulo()).update("nombreSolicitudes", FieldValue.arrayRemove(mAuth.getCurrentUser().getEmail()));
            updateSolicitudes(solicitud);
            Toast.makeText(this.getContext(), "Solicitud aceptada",Toast.LENGTH_SHORT).show();
        }else{ // Aquí va la solicitud de ruta
            Map<String, Object> camposRuta = new HashMap<>();
            db.collection("usuarios").document(solicitud.getTitulo()).collection("colecciones").document("Rutas propias")
                    .collection("rutas").document(solicitud.getRutaId()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            camposRuta.put("titulo",documentSnapshot.getString("titulo"));
                            camposRuta.put("descripcion",documentSnapshot.getString("descripcion"));
                            camposRuta.put("publico",documentSnapshot.getBoolean("publico"));
                            camposRuta.put("circular",documentSnapshot.getBoolean("circular"));
                            camposRuta.put("imagenes",documentSnapshot.get("imagenes"));
                            camposRuta.put("puntos",documentSnapshot.get("puntos"));
                            reference.collection("colecciones").document("Rutas propias").collection("rutas").document(solicitud.getRutaId()).set(camposRuta);
                            reference.collection("solicitudes").document(solicitud.getRutaId()).delete();
                            updateSolicitudes(solicitud);
                            Toast.makeText(this.getContext(), "Solicitud aceptada",Toast.LENGTH_SHORT).show();
                        } else {
                            updateSolicitudes(solicitud);
                            Toast.makeText(this.getContext(), "El usuario ha eliminado ya esa ruta",Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    /**
     * Método encargado de borrar la solicitud de la lista del usuario.
     * @param solicitud La solicitud que será borrada
     */
    private void borrarSolicitud(Solicitud solicitud) {
        if(solicitud.getRutaId() == null) {
            reference.collection("solicitudes").document(solicitud.getTitulo()).delete();
            db.collection("usuarios").document(solicitud.getTitulo()).update("nombreSolicitudes", FieldValue.arrayRemove(mAuth.getCurrentUser().getEmail()));
        }else{
            reference.collection("solicitudes").document(solicitud.getRutaId()).delete();
        }
        updateSolicitudes(solicitud);
        Toast.makeText(this.getContext(), "Solicitud eliminada",Toast.LENGTH_SHORT).show();
    }

    /**
     * Método para configurar el elemento donde se listan los modelos.
     */
    private void configureView() {
        solicitudListView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        solicitudListView.setLayoutManager(layoutManager);
    }

    /**
     * Método para añadirle todas las solicitudes al elemento que los mostrará.
     * @param list La lista de solicitudes
     */
    private void addAdapter(ArrayList<Solicitud> list) {
        ListSolicitudesAdapter lsAdapter = new ListSolicitudesAdapter(list, (solicitud, aceptar) -> {
            if(aceptar){
                aceptarSolicitud(solicitud);
            }else {
                borrarSolicitud(solicitud);
            }
        });
        solicitudListView.setAdapter(lsAdapter);
    }

    /**
     * Método para cargar todas las solicitudes del usuario desde Firebase
     */
    private void cargarDatos() {
        reference.collection("solicitudes").get().addOnSuccessListener(queryDocumentSnapshots -> {
                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                    solicitudes.add(new Solicitud(doc.getString("email"), doc.getString("rutaId")));
                }
        });
    }

    /**
     * Método para eliminar la solicitud de la lista una vez eliminada.
     * @param solicitud El modelo que será actualizado
     */
    public void updateSolicitudes(Solicitud solicitud) {
        solicitudes.remove(solicitud);
        solicitudListView.getAdapter().notifyDataSetChanged();
    }

    /**
     * Método encargado de cargar las solicitudes y asignárselos al elemento de la vista.
     * Llama a los métodos cargarDatos(), configureView() y addAdapter(solicitudes).
     */
    public void initializeSolicitudes() {
        final Executor EXECUTOR = Executors.newSingleThreadExecutor();
        final Handler HANDLER = new Handler(Looper.getMainLooper());
        EXECUTOR.execute(() -> {
            cargarDatos();
            HANDLER.post(() -> {
                db.collection("usuarios").document(mAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(
                        documentSnapshot -> {
                            if(documentSnapshot.exists()){
                                configureView();
                                addAdapter(solicitudes);
                            }
                        }
                );
            });

        });
    }
}