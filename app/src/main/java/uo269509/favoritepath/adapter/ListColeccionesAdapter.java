package uo269509.favoritepath.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uo269509.favoritepath.R;
import uo269509.favoritepath.modelos.Coleccion;


public class ListColeccionesAdapter extends RecyclerView.Adapter<ListColeccionesAdapter.ColeccionViewHolder> {

    private ArrayList<Coleccion> colecciones;
    private final OnItemClickListener listener;

    public ListColeccionesAdapter(ArrayList<Coleccion> colecciones, OnItemClickListener listener) {
        this.colecciones = colecciones;
        this.listener = listener;
    }

    @Override
    public ColeccionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.linear_recycler_view_coleccion, parent, false);
        return new ColeccionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ColeccionViewHolder holder, int position) {
        if(position % 2 == 0){
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.card_shape_yellow));
        }
        Coleccion coleccion = colecciones.get(position);
        holder.bindUser(coleccion, listener);
    }

    @Override
    public int getItemCount() {
        return colecciones.size();
    }

    public interface OnItemClickListener{
        void onItemClick(Coleccion item, boolean delete);
    }

    public static class ColeccionViewHolder extends RecyclerView.ViewHolder {

        private TextView titulo;
        private ImageView delete;

        public ColeccionViewHolder(View itemView){
            super(itemView);
            titulo = itemView.findViewById(R.id.tituloLabel);
            delete = itemView.findViewById(R.id.share);
        }

        public void bindUser(final Coleccion coleccion, final OnItemClickListener listener){
            titulo.setText(coleccion.getTitulo());
            itemView.setOnClickListener((v) -> {
                listener.onItemClick(coleccion,false);
            });
            if(!(coleccion.getTitulo().equals("Rutas propias") || coleccion.getTitulo().equals("Rutas favoritas"))) {
                delete.setOnClickListener((v) -> {
                    listener.onItemClick(coleccion, true);
                });
            }else{
                delete.setVisibility(View.GONE);
            }
        }
    }
}
