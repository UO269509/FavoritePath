// Generated by view binder compiler. Do not edit!
package uo269509.favoritepath.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import uo269509.favoritepath.R;

public final class LinearRecyclerViewSolicitudBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final ImageView imagenAceptar;

  @NonNull
  public final ImageView imagenBorrar;

  @NonNull
  public final TextView solicitudText;

  private LinearRecyclerViewSolicitudBinding(@NonNull LinearLayout rootView,
      @NonNull ImageView imagenAceptar, @NonNull ImageView imagenBorrar,
      @NonNull TextView solicitudText) {
    this.rootView = rootView;
    this.imagenAceptar = imagenAceptar;
    this.imagenBorrar = imagenBorrar;
    this.solicitudText = solicitudText;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LinearRecyclerViewSolicitudBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LinearRecyclerViewSolicitudBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.linear_recycler_view_solicitud, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LinearRecyclerViewSolicitudBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.imagenAceptar;
      ImageView imagenAceptar = ViewBindings.findChildViewById(rootView, id);
      if (imagenAceptar == null) {
        break missingId;
      }

      id = R.id.imagenBorrar;
      ImageView imagenBorrar = ViewBindings.findChildViewById(rootView, id);
      if (imagenBorrar == null) {
        break missingId;
      }

      id = R.id.solicitudText;
      TextView solicitudText = ViewBindings.findChildViewById(rootView, id);
      if (solicitudText == null) {
        break missingId;
      }

      return new LinearRecyclerViewSolicitudBinding((LinearLayout) rootView, imagenAceptar,
          imagenBorrar, solicitudText);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}