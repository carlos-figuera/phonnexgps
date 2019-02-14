package com.phonnexgps.phonnexgps.Vistas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.phonnexgps.phonnexgps.R;

public class calle extends Activity implements OnStreetViewPanoramaReadyCallback,View.OnClickListener{
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    Double longitud=0.0;
    Double latitud=0.0;
    ImageView Img_atrast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calle);
        Img_atrast=(ImageView)findViewById(R.id.Img_atrast);
        Img_atrast.setOnClickListener(this);

        sharedPreferences = getSharedPreferences("Datos", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        try {
            longitud =Double.parseDouble(sharedPreferences.getString("Longitud", "")) ;//Token de la sesion
            latitud =Double.parseDouble(sharedPreferences.getString("latitud", "")) ;//Token de la sesion
            if (longitud!=null)
                streetViewPanorama.setPosition(new LatLng(latitud, longitud));
        } catch (Exception e){

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Img_atrast:

                Intent i = new Intent( this,Menus.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                break;
        }
    }
}
