package uo269509.favoritepath.modelos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class Ruta implements Parcelable {

    String id;
    String titulo;
    String origen;
    String descripcion;
    List<GeoPoint> puntos;
    List<String> imagenes;
    boolean circular;
    boolean publica;

    public Ruta(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<String> imagenes) {
        this.imagenes = imagenes;
    }

    protected Ruta(Parcel in) {
        id = in.readString();
        titulo = in.readString();
        descripcion = in.readString();
        origen = in.readString();
        circular = in.readByte() != 0;
        publica = in.readByte() != 0;
        puntos = new ArrayList<>();
        in.readList(puntos, null);
        imagenes = new ArrayList<>();
        in.readList(imagenes, null);
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(id);
        dest.writeString(titulo);
        dest.writeString(descripcion);
        dest.writeString(origen);
        dest.writeByte((byte) (circular ? 1 : 0));
        dest.writeByte((byte) (publica ? 1 : 0));
        dest.writeList(puntos);
        dest.writeList(imagenes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Ruta> CREATOR = new Creator<Ruta>() {
        @Override
        public Ruta createFromParcel(Parcel in) {
            return new Ruta(in);
        }

        @Override
        public Ruta[] newArray(int size) {
            return new Ruta[size];
        }
    };
}
