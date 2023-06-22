package uo269509.favoritepath.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {

    private final List<String> mImages;

    public ImagePagerAdapter(List<String> images) {
        mImages = images;
    }

    /**
     * Método para devolver el número de imágenes.
     * @return Número de imágenes
     */
    @Override
    public int getCount() {
        return mImages.size();
    }

    /**
     * Método que comprueba si hay vista que contenga el objeto.
     * @param view La vista
     * @param object El objeto
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * Método que instancia la imagen en el contenedor.
     * @param container El contenedor de las imágenes
     * @param position La posición de la imagen
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.get().load(mImages.get(position)).into(imageView);
        container.addView(imageView);
        return imageView;
    }

    /**
     * Método para destruir la imagen.
     * @param container El contenedor de las imágenes
     * @param position La posición de la imagen
     * @param object La imagen
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}

