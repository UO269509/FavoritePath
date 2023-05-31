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
import uo269509.favoritepath.modelos.Solicitud;


public class ListSolicitudesAdapter extends RecyclerView.Adapter<ListSolicitudesAdapter.SolicitudViewHolder> {

    private List<Solicitud> solicitudes;
    private final OnItemClickListener listener;

    public ListSolicitudesAdapter(List<Solicitud> solicitudes, OnItemClickListener listener) {
        this.solicitudes = solicitudes;
        this.listener = listener;
    }

    @Override
    public SolicitudViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.linear_recycler_view_solicitud, parent, false);
        return new SolicitudViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SolicitudViewHolder holder, int position) {
        if(position % 2 == 0){
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.card_shape_yellow));
        }
        Solicitud amigo = solicitudes.get(position);
        holder.bindUser(amigo, listener);
    }

    @Override
    public int getItemCount() {
        return solicitudes.size();
    }

    public interface OnItemClickListener{
        void onItemClick(Solicitud item, boolean aceptar);
    }

    public static class SolicitudViewHolder extends RecyclerView.ViewHolder {

        private TextView titulo;
        private ImageView aceptar;
        private ImageView borrar;
        private final String SOLICITUD_AMIGO = " envío una solicitud";
        private final String SOLICITUD_RUTA = " compartió una ruta";

        public SolicitudViewHolder(View itemView){
            super(itemView);
            titulo = itemView.findViewById(R.id.solicitudText);
            aceptar = itemView.findViewById(R.id.imagenAceptar);
            borrar = itemView.findViewById(R.id.imagenBorrar);
        }

        public void bindUser(final Solicitud solicitud, final OnItemClickListener listener){
            if(solicitud.getRutaId() == null)
                titulo.setText(solicitud.getTitulo() + SOLICITUD_AMIGO);
            else
                titulo.setText(solicitud.getTitulo() + SOLICITUD_RUTA);
            aceptar.setOnClickListener((v) -> {
                listener.onItemClick(solicitud, true);
            });
            borrar.setOnClickListener((v) -> {
                listener.onItemClick(solicitud, false);
            });
        }
    }
}
