package uo269509.favoritepath.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Coleccion implements Parcelable {

    private String titulo;

    public Coleccion(String titulo){
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    protected Coleccion(Parcel in) {
        titulo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(titulo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Coleccion> CREATOR = new Creator<Coleccion>() {
        @Override
        public Coleccion createFromParcel(Parcel in) {
            return new Coleccion(in);
        }

        @Override
        public Coleccion[] newArray(int size) {
            return new Coleccion[size];
        }
    };
}
