package uo269509.favoritepath.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import uo269509.favoritepath.R;
import uo269509.favoritepath.modelos.User;

public class PerfilFragment extends Fragment {

    EditText nombre;
    EditText descripcion;
    FloatingActionButton guardarBtn;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    DocumentReference reference;
    ImageView navView;
    ImageView perfilView;
    TextView nombreView;
    TextView descripcionView;

    public PerfilFragment() {}

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
        View vista = inflater.inflate(R.layout.fragment_perfil, container, false);
        init(vista);
        actualizarPerfil();
        return vista;
    }

    /**
     * Método para actualizar el perfil del usuario.
     */
    private void actualizarPerfil() {
        guardarBtn.setOnClickListener(v -> {
            String nuevoNombre = nombre.getText().toString().trim();
            String nuevaDesc = descripcion.getText().toString().trim();
            if (nuevoNombre != null || !nuevoNombre.isEmpty()) {
                if (nuevaDesc == null || nuevaDesc.isEmpty())
                    nuevaDesc = "";
                Map<String, Object> updates = new HashMap<>();
                updates.put("nombre", nuevoNombre);
                updates.put("descripcion", nuevaDesc);
                reference.update(updates);
                updateNavHeader(nuevoNombre, nuevaDesc);
                Toast.makeText(this.getContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.getContext(), "Escriba un nombre válido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Método para actualizar los elementos de la vista.
     * @param nuevoNombre El nombre nuevo del usuario
     * @param nuevaDesc La descripción nueva del usuario
     */
    private void updateNavHeader(String nuevoNombre, String nuevaDesc) {
        nombreView.setText(nuevoNombre);
        descripcionView.setText(nuevaDesc);
    }

    /**
     * Método encargado de guardar los atributos del usuario.
     */
    private void updateUI() {
        reference.get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            nombre.setText(user.getNombre());
            descripcion.setText(user.getDescripcion());
        });
    }

    /**
     * Método para instanciar todos los elementos necesarios en la clase.
     */
    public void init(View vista){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        reference = db.collection("usuarios").document(mAuth.getCurrentUser().getEmail());
        perfilView = vista.findViewById(R.id.perfilImage);
        nombre = vista.findViewById(R.id.editTextNombre);
        descripcion = vista.findViewById(R.id.editTextDescripcion);
        guardarBtn = vista.findViewById(R.id.perfilBtn);
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navView = headerView.findViewById(R.id.imagenPerfil);
        nombreView = headerView.findViewById(R.id.nombreUsuario);
        descripcionView = headerView.findViewById(R.id.descripcionUsuario);
        if(mAuth.getCurrentUser().getPhotoUrl() != null)
            Picasso.get().load(mAuth.getCurrentUser().getPhotoUrl()).into(perfilView);
        updateUI();
    }
}