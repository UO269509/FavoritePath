package uo269509.favoritepath.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import uo269509.favoritepath.R;
import uo269509.favoritepath.ui.AmigosFragment;
import uo269509.favoritepath.ui.AyudaFragment;
import uo269509.favoritepath.ui.ColeccionesFragment;
import uo269509.favoritepath.ui.InicioFragment;
import uo269509.favoritepath.ui.PerfilFragment;
import uo269509.favoritepath.ui.SolicitudesFragment;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 1;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    TextView nombreView;
    TextView descripcionView;

    /**
     * Método para recibir las respuestas de las peticiones de permisos del usuario.
     * @param requestCode El código de la petición
     * @param permissions Los diferentes permisos
     * @param grantResults Los resultados obtenidos
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "No se ha podido abrir las colecciones, por favor, " +
                            "acepte los permisos necesarios.",Toast.LENGTH_SHORT).show();
                }else{
                    abrirColecciones();
                }
        }
    }

    /**
     * Método utilizado para volver a la pantalla anterior..
     */
    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if (fragment instanceof ColeccionesFragment) {
            boolean change = ((ColeccionesFragment) fragment).onBackPressed();
            if(change){
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Método para generar la actividad, obtiene las referencias de los elementos y coloca los listeners a los botones.
     * Llama a los métodos initialize(), navigation() y actualizarNavHeader().
     * @param savedInstanceState El estado de la instancia.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        navigation();
        actualizarNavHeader();
    }

    /**
     * Método para manejar la navegación de las opciones del menú.
     */
    private void navigation(){
        navigationView.setNavigationItemSelectedListener(item -> {
            // Aquí se maneja la selección del usuario
            switch (item.getItemId()) {
                case R.id.nav_item1: //perfil
                    cargarFragment(new PerfilFragment());
                    toolbar.setTitle(R.string.menu_perfil);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                case R.id.nav_item2: //colecciones
                    checkPermisos();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                case R.id.nav_item3: //amigos
                    cargarFragment(new AmigosFragment());
                    toolbar.setTitle(R.string.listado_amigos);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                case R.id.nav_item4: //solicitudes
                    cargarFragment(new SolicitudesFragment());
                    toolbar.setTitle(R.string.listado_solicitudes);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                case R.id.nav_item5: //ayuda
                    cargarFragment(new AyudaFragment());
                    toolbar.setTitle(R.string.menu_ayuda);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                default:
                    Toast.makeText(this, "Seleccione una opción válida",Toast.LENGTH_SHORT).show();
                    return false;
            }
        });
    }

    /**
     * Método para cerrar sesión en la app y volver al LoginActivity.
     * @param view La vista
     */
    public void logOut(View view){
        mAuth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Método encargado de asignar el fragmento elegido en la MainActivity y así que se pueda visualizar.
     * @param fragment El fragmento que hay que cargar
     */
    public void cargarFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Método para instanciar todos los elementos necesarios en la clase.
     */
    private void initialize(){
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();
        cargarFragment(new InicioFragment());
        toolbar.setTitle(R.string.app_name);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser().getPhotoUrl() != null)
            Picasso.get().load(mAuth.getCurrentUser().getPhotoUrl()).into((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imagenPerfil));
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Método encargado de cargar el nombre y la descripción del usuario en el NavHeader.
     */
    public void actualizarNavHeader() {
        final Executor EXECUTOR = Executors.newSingleThreadExecutor();
        final Handler HANDLER = new Handler(Looper.getMainLooper());
        EXECUTOR.execute(() -> {
            HANDLER.post(() -> {
                mAuth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();
            });
            HANDLER.post(() -> {
                db.collection("usuarios").document(mAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(
                        documentSnapshot -> {
                            if(documentSnapshot.exists()){
                                nombreView = findViewById(R.id.nombreUsuario);
                                descripcionView = findViewById(R.id.descripcionUsuario);
                                nombreView.setText((String) documentSnapshot.get("nombre"));
                                descripcionView.setText((String) documentSnapshot.get("descripcion"));
                            }
                        }
                );
            });

        });
    }

    /**
     * Método encargado de abrir el apartado de Colecciones
     */
    public void abrirColecciones(){
        cargarFragment(new ColeccionesFragment());
        toolbar.setTitle(R.string.listado_colecciones);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * Método encargado de comprobar si el dispositivo del usuario tiene activados los permisos
     * necesarios de la aplicación.
     */
    private void checkPermisos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(this)
                    .setTitle("Permiso requerido")
                    .setMessage("La aplicación necesita ciertos permisos para poder usar sus colecciones.")
                    .setPositiveButton("Aceptar", (dialogInterface, i) -> {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                                REQUEST_STORAGE_PERMISSION);
                    }).show();
        }else{
            abrirColecciones();
        }
    }

}