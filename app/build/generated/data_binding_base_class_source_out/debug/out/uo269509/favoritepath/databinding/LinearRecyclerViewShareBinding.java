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

public final class LinearRecyclerViewShareBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final ImageView imagenUsuario;

  @NonNull
  public final TextView nombreAmigo;

  @NonNull
  public final ImageView share;

  private LinearRecyclerViewShareBinding(@NonNull LinearLayout rootView,
      @NonNull ImageView imagenUsuario, @NonNull TextView nombreAmigo, @NonNull ImageView share) {
    this.rootView = rootView;
    this.imagenUsuario = imagenUsuario;
    this.nombreAmigo = nombreAmigo;
    this.share = share;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LinearRecyclerViewShareBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LinearRecyclerViewShareBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.linear_recycler_view_share, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LinearRecyclerViewShareBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.imagenUsuario;
      ImageView imagenUsuario = ViewBindings.findChildViewById(rootView, id);
      if (imagenUsuario == null) {
        break missingId;
      }

      id = R.id.nombreAmigo;
      TextView nombreAmigo = ViewBindings.findChildViewById(rootView, id);
      if (nombreAmigo == null) {
        break missingId;
      }

      id = R.id.share;
      ImageView share = ViewBindings.findChildViewById(rootView, id);
      if (share == null) {
        break missingId;
      }

      return new LinearRecyclerViewShareBinding((LinearLayout) rootView, imagenUsuario, nombreAmigo,
          share);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
