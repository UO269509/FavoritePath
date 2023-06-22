package uo269509.favoritepath.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import uo269509.favoritepath.R;

public class InicioFragment extends Fragment {

    public InicioFragment() {
    }

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
        View vista = inflater.inflate(R.layout.fragment_inicio, container, false);
        return vista;
    }
}