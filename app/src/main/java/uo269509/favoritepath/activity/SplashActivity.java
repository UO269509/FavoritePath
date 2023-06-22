package uo269509.favoritepath.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import uo269509.favoritepath.R;

public class SplashActivity extends AppCompatActivity {

    /**
     * Método para generar la actividad y hacer una pequeña espera para mostrar el logo.
     * @param savedInstanceState El estado de la instancia.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }, 2500);
    }
}