package uo269509.favoritepath.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Solicitud implements Parcelable {

    String titulo;
    String rutaId;

    public Solicitud(String titulo, String rutaId){
        this.titulo = titulo;
        this.rutaId = rutaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getRutaId() {
        return rutaId;
    }

    protected Solicitud(Parcel in) {
        titulo = in.readString();
        rutaId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(titulo);
        dest.writeString(rutaId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Solicitud> CREATOR = new Creator<Solicitud>() {
        @Override
        public Solicitud createFromParcel(Parcel in) {
            return new Solicitud(in);
        }

        @Override
        public Solicitud[] newArray(int size) {
            return new Solicitud[size];
        }
    };
}
