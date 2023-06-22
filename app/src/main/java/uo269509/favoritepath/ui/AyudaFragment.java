package uo269509.favoritepath.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import uo269509.favoritepath.R;

public class AyudaFragment extends Fragment {

    public AyudaFragment() {
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
        View vista = inflater.inflate(R.layout.fragment_ayuda, container, false);
        TextView ayuda = vista.findViewById(R.id.textViewAyuda);
        String textoCompleto ="Navegación y cierre de sesión:\n\n Ya dentro de la app, para poder navegar por ella, deberemos abrir el menú, situado en la esquina superior izquierda de la pantalla o deslizando la pantalla de izquierda a derecha, siendo solamente accesible desde estas opciones, en el caso de que no se pueda acceder al menú, solo bastará con volver atrás desde su dispositivo.\n" +
                "Dentro se puede observar las diferentes opciones que ofrece la app, que podemos visitar clicando cada una de ellas. También se puede observar que encima de las opciones se encuentra el botón para cerrar sesión y volver al formulario de inicio de sesión.\n\n" +
                "Edición del perfil:\n\n Ya dentro de esta opción, podremos editar nuestro nombre de usuario al igual que una descripción propia que los demás usuarios podrán visualizar, solo haría falta rellenar el formulario y darle al botón de guardar.\n\n" +
                "Gestión de colecciones y rutas:\n\n Ya dentro de esta opción, podremos visualizar todas las colecciones que tenemos, acompañadas de un botón de borrado aquellas colecciones creadas por el usuario, ya que no se podrán borrar las colecciones creadas por defecto. Hay que tener en cuenta que el borrado de una colección implica el borrado de todas las rutas de su interior. Además, en la parte inferior encontrarás un formulario donde podrás crear diferentes colecciones y un botón para crear rutas que se explicará en el siguiente punto.\n" +
                "También podremos visualizar nuestras rutas creadas de una colección concreta clicando en el nombre de la misma. Estas estarán acompañadas de un botón de borrado para eliminarla de dicha colección. Además, tendremos las mismas opciones comentadas anteriormente en la parte inferior.\n\n" +
                "Creación y edición de rutas:\n\n Este apartado ya no cuenta con el menú lateral a su disposición, para volver a él, deberá clicar el botón de retroceder de su dispositivo. Para poder crear una ruta, deberá clicar el botón comentado en el anterior punto, el cual, si se presiona desde el listado general de colecciones creará la ruta en la colección de rutas propias por defecto, pero si se clica desde una colección concreta también aparecerá la ruta creada dentro, mientras que, para editar una ruta, tendrá que seleccionar una ruta ya creada dentro de una colección.\n" +
                "Estas funciones comparten el mismo formulario donde podrá escribir un título y descripción, elegir si quiere que la ruta sea pública a todos los usuarios o en cambio, privada, al igual que elegir si quiere que la ruta sea circular y te lleve al mismo punto de partida al acabar o que sea lineal, la posibilidad de añadir imágenes para rememorar momentos del viaje y un mapa donde poder elegir que sitios visitar, siempre situado en el centro de su ubicación. En el caso de querer editar los puntos en el mapa, estos solo se podrán borrar desde el último seleccionado hacía el más antiguo. Como final, en la parte inferior se puede observar el botón de guardado y el botón de iniciar ruta que se explica en el siguiente punto.\n\n" +
                "Compartir rutas e iniciarlas:\n\n Este apartado ya no cuenta con el menú lateral a su disposición, para volver a él, deberá clicar el botón de retroceder de su dispositivo. Para acceder a este apartado, deberá clicar el botón comentado en el anterior punto, donde podremos visualizar todos los amigos que tenemos, acompañados de un botón para poder compartirles la ruta y en la parte inferior hay un botón que da por comenzada la ruta.\n" +
                "Después de clicar, abrirá un mapa interactivo donde se visualizará el trayecto más eficiente para realizar el viaje con todas las indicaciones necesarias.\n\n" +
                "Gestión de amigos:\n\n Ya dentro de esta opción, podremos visualizar todos los amigos que tenemos, acompañados de un botón de borrado en el caso de que quieras eliminar a algún usuario de tu lista. Además, en la parte inferior encontrarás un formulario donde podrás enviar peticiones de amistad a otros usuarios existentes de la app, solo tendrás que escribir su correo electrónico y esperar a que el otro usuario acepte la invitación.\n\n" +
                "Gestión de solicitudes:\n\n Ya dentro de esta opción, podremos visualizar todas las solicitudes que tenemos, contado con dos tipos de solicitudes, las primeras serían las de amistad, que son aquellas que otros usuarios te han enviado para pasar a ser amigos en la app, las otras serían solicitudes donde un amigo te ha compartido una ruta suya. Todas ellas vienen acompañadas de dos botones, siendo uno para aceptar y otro para denegar respectivamente.";

        ayuda.setText(textoCompleto);
        return vista;
    }
}