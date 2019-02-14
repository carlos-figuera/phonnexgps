package com.phonnexgps.phonnexgps.Google_maps;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.phonnexgps.phonnexgps.R;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static final String TAG = "CustomInfoWindowAdapter";
    private LayoutInflater inflater;
    Context context;

    public CustomInfoWindowAdapter(LayoutInflater inflater, Context context) {
        this.inflater = inflater;
        this.context = context;
    }

    @Override
    public View getInfoWindow(final Marker marker) {

        return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {

        View v = inflater.inflate(R.layout.car_marcador, null);
        String[] info = marker.getTitle().split("&");
        String url = marker.getSnippet();
        ((TextView)v.findViewById(R.id.Txt_placa)).setText( info[0]);

        ((TextView)v.findViewById(R.id.Txt_velocidad)).setText(info[2]);
        ((TextView)v.findViewById(R.id.Txt_horometro)).setText(info[3] );
        ((TextView)v.findViewById(R.id.Txt_chofer)).setText(info[4] );
        ((TextView)v.findViewById(R.id.Txt_chofer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        try {


            if(info[1].equals("1") ){


                ((TextView)v.findViewById(R.id.Txt_estado)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.encendido, 0);;
            }
            else {
                ((TextView)v.findViewById(R.id.Txt_estado)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apagado, 0);;
            }
        }catch (Exception e){
            ((TextView)v.findViewById(R.id.Txt_estado)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apagado, 0);;
        }




        try {
            double valor=Double.parseDouble(info[2] );

            if(valor>0){
                ((TextView)v.findViewById(R.id.txt_etiqueta_tiempo)).setText("Tiempo recorrido:" );
                ((TextView)v.findViewById(R.id.Txt_tiempo_reco)).setText(info[5] );
            }
            else {
                ((TextView)v.findViewById(R.id.txt_etiqueta_tiempo)).setText("Tiempo detenido: " );
                ((TextView)v.findViewById(R.id.Txt_tiempo_reco)).setText(info[5] );
            }
        }catch (Exception e){
            ((TextView)v.findViewById(R.id.txt_etiqueta_tiempo)).setText("Tiempo detenido: " );
            ((TextView)v.findViewById(R.id.Txt_tiempo_reco)).setText(info[5] );
        }




        ((TextView)v.findViewById(R.id.Txt_direccion)).setText(info[6] );

        if(info[7].equals("1")){
            ((TextView)v.findViewById(R.id.Txt_bloqueo)).setText( "ON");
        }
        else {
            ((TextView)v.findViewById(R.id.Txt_bloqueo)).setText( "OFF");
        }

        ((TextView)v.findViewById(R.id.Txt_voltio)).setText(info[8] );



        return v;
    }
}
