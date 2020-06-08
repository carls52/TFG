package com.example.pruebavision;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.pruebavision.R;

import java.io.PipedOutputStream;
import java.util.ArrayList;

public class Rectangulo {
    ArrayList<ImageView> view;
    ConstraintLayout layout;
    Context contexto;
    public Rectangulo (ConstraintLayout layout, Context context)
    {
        this.layout = layout;
        view = new ArrayList<>();
        this.contexto = context;
    }
    public void drawRectangulo(Point[] puntos)
    {
        ImageView v = new ImageView(contexto.getApplicationContext());
        v.setImageDrawable(contexto.getDrawable(R.drawable.rect));
        v.setBackground(contexto.getDrawable(R.drawable.rect));
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(puntos[2].x-puntos[3].x, puntos[3].y-puntos[0].y);
        v.setLayoutParams(lp);
        v.setVisibility(View.VISIBLE);
        v.setX(puntos[3].x);
        //v.setY(puntos[3].y+(puntos[3].y-puntos[0].y));
        v.setY(puntos[3].y);
        //v.setPadding(15,15,15,15);
        view.add(v);
      //  v.setX(x-300);
      //  v.setY(y-300);
        layout.addView(v);
    }
    public void clearRectangulo()
    {
        for(ImageView v : view)
        {
            v.setVisibility(View.GONE);
            layout.removeView(v);
        }
        view.clear();
    }
}
