package uo269509.favoritepath.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uo269509.favoritepath.R;
import uo269509.favoritepath.modelos.Ruta;


public class ListRutasAdapter extends RecyclerView.Adapter<ListRutasAdapter.RutaViewHolder> {

    private List<Ruta> rutas;
    private final OnItemClickListener listener;

    public ListRutasAdapter(List<Ruta> rutas, OnItemClickListener listener) {
        this.rutas = rutas;
        this.listener = listener;
    }

    @Override
    public RutaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.linear_recycler_view_ruta, parent, false);
        return new RutaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RutaViewHolder holder, int position) {
        if(position % 2 == 0){
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.card_shape_yellow));
        }
        Ruta ruta = rutas.get(position);
        holder.bindUser(ruta, listener);
    }

    @Override
    public int getItemCount() {
        return rutas.size();
    }

    public interface OnItemClickListener{
        void onItemClick(Ruta item, boolean delete);
    }

    public static class RutaViewHolder extends RecyclerView.ViewHolder {

        private TextView titulo;
        private TextView descripcion;
        private ImageView delete;

        public RutaViewHolder(View itemView){
            super(itemView);
            titulo = itemView.findViewById(R.id.nombreLabel);
            descripcion = itemView.findViewById(R.id.descripcionLabel);
            delete = itemView.findViewById(R.id.imagenBorrar);
        }

        public void bindUser(final Ruta ruta, final OnItemClickListener listener){
            titulo.setText(ruta.getTitulo());
            descripcion.setText(ruta.getDescripcion());
            itemView.setOnClickListener((v) -> {
                listener.onItemClick(ruta, false);
            });
            delete.setOnClickListener((v) -> {
                listener.onItemClick(ruta, true);
            });
        }
    }
}
