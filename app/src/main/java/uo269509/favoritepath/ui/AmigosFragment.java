package uo269509.favoritepath.ui;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import uo269509.favoritepath.R;
import uo269509.favoritepath.adapter.ListAmigosAdapter;
import uo269509.favoritepath.modelos.Amigo;

public class AmigosFragment extends Fragment {

    EditText solicitud;
    Button solicitudBtn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    DocumentReference reference;
    RecyclerView amigoListView;
    ArrayList<Amigo> amigos = new ArrayList<>();

    public AmigosFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_amigos, container, false);
        init(vista);
        initializeAmigos();
        mandarSolicitud();
        return vista;
    }

    private void mandarSolicitud(){
        solicitudBtn.setOnClickListener(v -> {
            String newSolicitud = solicitud.getText().toString().trim();
            if(newSolicitud != null && !newSolicitud.isEmpty() && !newSolicitud.equals(mAuth.getCurrentUser().getEmail())){
                reference.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> nombreSolicitudes = (ArrayList<String>) documentSnapshot.get("nombreSolicitudes");
                        if(nombreSolicitudes != null && nombreSolicitudes.contains(newSolicitud)){
                            Toast.makeText(this.getContext(), "Ya has mandado una solicitud a este usuario",Toast.LENGTH_SHORT).show();
                        }else{
                            reference.collection("amigos").document(newSolicitud).get().addOnSuccessListener(documentSnapshot2 -> {
                                if(documentSnapshot2.exists()){
                                    Toast.makeText(this.getContext(), "Este usuario ya es tu amigo",Toast.LENGTH_SHORT).show();
                                }else{
                                    existeUsuarioSolicitud(newSolicitud);
                                }
                            });
                        }
                    }
                });
            }else{
                Toast.makeText(this.getContext(), "Escriba un usuario existente",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void existeUsuarioSolicitud(String newSolicitud){
       DocumentReference auxReference = db.collection("usuarios").document(newSolicitud);
        auxReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> camposSolicitud = new HashMap<>();
                camposSolicitud.put("email",mAuth.getCurrentUser().getEmail());
                auxReference.collection("solicitudes").document(mAuth.getCurrentUser().getEmail()).set(camposSolicitud);
                reference.get().addOnSuccessListener(documentSnapshot2 -> {
                    if (documentSnapshot2.exists()) {
                        ArrayList<String> nombreSolicitudes = (ArrayList<String>) documentSnapshot.get("solicitudesMandadas");
                        if (nombreSolicitudes == null)
                            nombreSolicitudes = new ArrayList<String>();
                        nombreSolicitudes.add(newSolicitud);
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("nombreSolicitudes", nombreSolicitudes);
                        reference.update(updates);
                        solicitud.setText("");
                        Toast.makeText(this.getContext(), "Solicitud enviada correctamente", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(this.getContext(), "El usuario no existe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void borrarAmigo(Amigo amigo) {
        reference.collection("amigos").document(amigo.getNombre()).delete();
        db.collection("usuarios").document(amigo.getNombre()).collection("amigos").document(mAuth.getCurrentUser().getEmail()).delete();
        updateAmigos(amigo);
        Toast.makeText(this.getContext(), "Amigo borrado con éxito",Toast.LENGTH_SHORT).show();
    }

    private void init(View vista){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        reference = db.collection("usuarios").document(mAuth.getCurrentUser().getEmail());
        solicitud = vista.findViewById(R.id.editTextSolicitud);
        solicitudBtn = vista.findViewById(R.id.solicitudBtn);
        amigoListView = vista.findViewById(R.id.recyclerView);
    }

    private void configureView() {
        amigoListView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        amigoListView.setLayoutManager(layoutManager);
    }

    private void addAdapter(ArrayList<Amigo> list) {
        ListAmigosAdapter laAdapter = new ListAmigosAdapter(list, amigo -> {
                borrarAmigo(amigo);
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
}