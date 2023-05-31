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
import uo269509.favoritepath.modelos.Amigo;


public class ListAmigosAdapter extends RecyclerView.Adapter<ListAmigosAdapter.AmigoViewHolder> {

    private List<Amigo> amigos;
    private final OnItemClickListener listener;

    public ListAmigosAdapter(List<Amigo> amigos, OnItemClickListener listener) {
        this.amigos = amigos;
        this.listener = listener;
    }

    @Override
    public AmigoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.linear_recycler_view_amigo, parent, false);
        return new AmigoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AmigoViewHolder holder, int position) {
        if(position % 2 == 0){
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.card_shape_yellow));
        }
        Amigo amigo = amigos.get(position);
        holder.bindUser(amigo, listener);
    }

    @Override
    public int getItemCount() {
        return amigos.size();
    }

    public interface OnItemClickListener{
        void onItemClick(Amigo item);
    }

    public static class AmigoViewHolder extends RecyclerView.ViewHolder {

        private TextView nombre;
        private ImageView delete;

        public AmigoViewHolder(View itemView){
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreAmigo);
            delete = itemView.findViewById(R.id.share);
        }

        public void bindUser(final Amigo amigo, final OnItemClickListener listener){
            nombre.setText(amigo.getNombre());
            delete.setOnClickListener((v) -> {
                listener.onItemClick(amigo);
            });
        }
    }
}
