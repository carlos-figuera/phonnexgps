package com.phonnexgps.phonnexgps.presentador;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Presentador_spinner {
    Context context ;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public Presentador_spinner(Context context) {
        this.context = context;
    }

    public void Cargar_spiner(android.widget.Spinner Sp_listado) {
        String can_flota="";
        String id_flota="";

        sharedPreferences = context.getSharedPreferences("Datos", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String lista=  sharedPreferences.getString("flotas","");
        List<String> lis= new ArrayList<>();
        JSONArray jArray = null;
        try {

            jArray = new JSONArray(lista);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject flota = jArray.getJSONObject(i);
                // Toast.makeText(context,flota.getString("id_flota")+  "  "+i+ " - "+ flota.getString("id_emp")+ " -- "+ flota.getString("no_descripcion_flota")   , Toast.LENGTH_SHORT).show();

                lis.add(flota.getString("no_descripcion_flota"));
                can_flota=flota.getString("no_descripcion_flota");
                id_flota=flota.getString("no_descripcion_flota");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }





        ArrayAdapter<String> adapter_alacance;
        adapter_alacance = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, lis);
        Sp_listado.setAdapter(adapter_alacance);
    }


    public void Cargar_spiner_vehiculos(android.widget.Spinner Sp_listado) {

        sharedPreferences = context.getSharedPreferences("Datos", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String lista=  sharedPreferences.getString("vehiculos","");
        List<String> lis= new ArrayList<>();
        JSONArray jArray = null;
        try {

            jArray = new JSONArray(lista);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject flota = jArray.getJSONObject(i);
                // Toast.makeText(context,flota.getString("id_flota")+  "  "+i+ " - "+ flota.getString("id_emp")+ " -- "+ flota.getString("no_descripcion_flota")   , Toast.LENGTH_SHORT).show();

                lis.add(flota.getString("no_descripcion_flota"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        ArrayAdapter<String> adapter_alacance;
        adapter_alacance = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, lis);
        Sp_listado.setAdapter(adapter_alacance);
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            return true;
        } else {


            return false;
        }
    }//  Metodo para verificar la conexion a internet
}
