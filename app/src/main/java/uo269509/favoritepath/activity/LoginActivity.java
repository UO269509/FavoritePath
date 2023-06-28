package uo269509.favoritepath.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uo269509.favoritepath.R;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final int PASSWORD_MIN = 6;
    private EditText emailText;
    private EditText passwordText;
    private Button loginButton;
    private Button registerButton;
    private SignInButton googleButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    GoogleSignInClient mGoogleSignInClient;

    /**
     * Método para generar la actividad, obtiene las referencias de los elementos y coloca los listeners a los botones.
     * Llama a los métodos init(), loginUser(), registerUser() y signInWithGoogle().
     * @param savedInstanceState El estado de la instancia.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> registerUser());
        googleButton.setOnClickListener(v -> signInWithGoogle());
    }

    /**
     * Método para recoger la información para iniciar en sesión al usuario cuando usa Google.
     * En caso de fallo, muestra un mensaje de inicio de sesión fallido.
     * Llama al método firebaseAuthWithGoogle(String idToken).
     * @param requestCode El código de la petición.
     * @param resultCode El resultado del intento.
     * @param data El intento de la pantalla.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, "Login fallido, por favor, vuelva a intentarlo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Método para autenticar al usuario si utiliza el inicio de sesión de Google.
     * En caso de fallo, muestra un mensaje de autenticación fallida.
     * Llama a los métodos createUserInFirebaseFirestore(String email) e inicia una nueva actividad.
     * @param idToken El tojen del usuario.
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        createUserInFirebaseFirestore(user.getEmail());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Autenticación fallida, por favor, vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Método para comprobar si el formulario de inicio de sesión es correcto.
     * En caso afirmatiivo, inicia una nueva actividad.
     * En caso de fallo, muestra un mensaje de inicio de sesión fallido.
     */
    private void loginUser() {
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(LoginActivity.this, "Rellene todos los campos", Toast.LENGTH_SHORT).show();
        }else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Login fallido, utilice datos correctos", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    /**
     * Método para comprobar si el formulario de registro es correcto.
     * En caso afirmatiivo, inicia una nueva actividad.
     * En caso de fallo, muestra un mensaje de registro fallido.
     * Llama al método createUserInFirebaseFirestore(String email).
     */
    private void registerUser() {
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        if(password.length() < PASSWORD_MIN){
            Toast.makeText(LoginActivity.this, "La contraseña debe tener 6 caracteres como mínimo", Toast.LENGTH_SHORT).show();
        }
        if(!email.isEmpty() && !password.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            createUserInFirebaseFirestore(email);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Registro fallido, por favor, vuelva a intentarlo",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(LoginActivity.this, "Rellene todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Método para la creación del usuario con los datos iniciales en FireBase.
     * @param email El email del usuario.
     */
    public void createUserInFirebaseFirestore(String email){
        CollectionReference usuarios = db.collection("usuarios");
        usuarios.document(email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    Map<String, Object> camposUsuario = new HashMap<>();
                    camposUsuario.put("nombre", email.split("@")[0].trim());
                    camposUsuario.put("descripcion", "");
                    ArrayList<String> nombreColecciones = new ArrayList<>();
                    nombreColecciones.add("Rutas propias");
                    nombreColecciones.add("Rutas favoritas");
                    camposUsuario.put("nombreColecciones",nombreColecciones);
                    usuarios.document(email).set(camposUsuario);
                }
            }
        });

    }

    /**
     * Método para llamar al servicio de Google y elegir que cuenta utilizar.
     */
    private void signInWithGoogle() {
        mGoogleSignInClient.signOut();
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    /**
     * Método para instanciar todos los elementos necesarios en la clase.
     */
    public void init(){
        emailText = findViewById(R.id.email_text);
        passwordText = findViewById(R.id.password_text);
        loginButton = findViewById(R.id.login_btn);
        registerButton = findViewById(R.id.register_btn);
        googleButton = findViewById(R.id.google_sign_in_button);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build());
    }
}