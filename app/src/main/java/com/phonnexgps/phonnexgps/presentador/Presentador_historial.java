package com.phonnexgps.phonnexgps.presentador;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.phonnexgps.phonnexgps.Modelos.Mo_Vehiculos;
import com.phonnexgps.phonnexgps.Modelos.Mo_recorridos;
import com.phonnexgps.phonnexgps.R;
import com.phonnexgps.phonnexgps.Volley_singleton.Volley_singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Presentador_historial {
    Context context;

    int con = 0;


    public Presentador_historial(Context context) {
        this.context = context;
    }

    private static final int COLOR_BLACK_ARGB = 0xffF9A825;
    private static final int POLYLINE_STROKE_WIDTH_PX = 8;
    Polyline polyline1;
    List<Mo_recorridos> lista = new ArrayList<>();
    List<LatLng> cood = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    //A partir  de un nombre de flota obtiene su Id que es  retornado  como una cadena  en esta funsion
    public String Obtener_id_fota(final String nombre, LinearLayout PARTE00) {

        sharedPreferences = context.getSharedPreferences("Datos", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String lista = sharedPreferences.getString("flotas", "");//Json con la lista de  flotas del usuario
        String id_flota = "";

        JSONArray jArray = null;
        try {

            jArray = new JSONArray(lista);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject flota = jArray.getJSONObject(i);
               // Toast.makeText(context,flota.getString("id_flota")+  "  "+i+ " - "+ flota.getString("id_emp")+ " -- "+ flota.getString("no_descripcion_flota")   , Toast.LENGTH_SHORT).show();

                if (nombre.equals(flota.getString("no_descripcion_flota").toString().trim())) {//verifico que  el nombre enviado como parametro sea  igual al  elemto iterado
                    id_flota = flota.getString("id_flota");
                }

            }

            if (jArray.length()<=1){
                PARTE00.setVisibility(View.GONE);
            }
            else if(jArray.length()>1){
                PARTE00.setVisibility(View.VISIBLE);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }


        return id_flota;
    }

    //Obtiene una lista con los vehiculos  disponibles y la carga en un arrayList  que se guarda en un shred
    public void Consultar_flota(final Map<String, String> token, String idFlota, final android.widget.Spinner Sp_listado) {
        sharedPreferences = context.getSharedPreferences("Datos", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("cargando...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url = "http://arepadevs.website/phonexx/public/api/flotas/" + idFlota;
        final StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                JSONObject res = null;
                final JSONObject data;
                JSONArray Vehiculos_list = null;
                if (response != null) {
                    try {
                        res = new JSONObject(response.toString());//Guardo la respuesta en String y la convierto a  un JsonObject
                        data = new JSONObject(res.optString("data"));//Tomo del atributo data la informacion que me interesa
                        Vehiculos_list = data.getJSONArray("vehiculos");
                        for (int i = 0; i < Vehiculos_list.length(); i++) {
                            JSONObject flota = Vehiculos_list.getJSONObject(i);

                            // Toast.makeText(context,flota.getString("id_flota")+  "  "+i+ " - "+ flota.getString("id_emp")+ " -- "+ flota.getString("no_descripcion_flota")   , Toast.LENGTH_SHORT).show();
                            //Toast.makeText(context, Vehiculos_list.toString() + "  ", Toast.LENGTH_SHORT).show();
                        }


                        editor.putString("vehiculos", Vehiculos_list.toString());//Guardo  en el shared  vehiculos el  Json que contiene  la lista  con los vehiculo del usuario
                        editor.commit();
                        editor.apply();
                        Cargar_spiner_vehiculos1(Sp_listado, Vehiculos_list.toString().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                    }


                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, "Ha ocurrido un error al cargar los datos, Selecciona una nueva flota para realizar una nueva búsqueda", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {//  Envio los parametros  para  la autenticacion
                return token;
            }

            {

            }
        };

        Volley_singleton.getInstancia_volley(context).addToRequestQueue(request);
    }


    //A partir  de una placa de  vehiculo se  obtiene su Id que es  retornado  como una cadena  en esta funsion
    public String Obtener_id_vehiculo(final String nombre) {

        sharedPreferences = context.getSharedPreferences("Datos", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String lista = sharedPreferences.getString("vehiculos", "");//Json con la lista de  flotas del usuario
        String id_vehiculo = "";

        JSONArray jArray = null;
        try {

            jArray = new JSONArray(lista);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject flota = jArray.getJSONObject(i);
                // Toast.makeText(context,flota.getString("id_flota")+  "  "+i+ " - "+ flota.getString("id_emp")+ " -- "+ flota.getString("no_descripcion_flota")   , Toast.LENGTH_SHORT).show();

                if (nombre.equals(flota.getString("no_vehi").toString().trim())) {//verifico que  el nombre enviado como parametro sea  igual al  elemto iterado
                    id_vehiculo = flota.getString("id_vehi");
                    //Toast.makeText(context, "id  "+flota.getString("id_vehi") , Toast.LENGTH_SHORT).show();
                    // Toast.makeText(context, "id  "+id_vehiculo , Toast.LENGTH_SHORT).show();
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return id_vehiculo;
    }

    // Obtiene una lista de los vehiculos relacionados con una  flota a partir de un IdFlota  y luego los carga en un spinner
    public void Cargar_spiner_vehiculos1(android.widget.Spinner Sp_listado, final String vehiculos) {

        sharedPreferences = context.getSharedPreferences("Datos", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        List<String> lis = new ArrayList<>();
        lis.add("Todos");
        JSONArray jArray = null;
        try {

            jArray = new JSONArray(vehiculos);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject flota = jArray.getJSONObject(i);
                // Toast.makeText(context,flota.getString("id_flota")+  "  "+i+ " - "+ flota.getString("id_emp")+ " -- "+ flota.getString("no_descripcion_flota")   , Toast.LENGTH_SHORT).show();

                lis.add(flota.getString("no_vehi"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ArrayAdapter<String> adapter_alacance;
        adapter_alacance = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, lis);
        Sp_listado.setAdapter(adapter_alacance);
        Sp_listado.setSelection(1);
    }


    // carga  de marcadores


    public void Consultar_vehiculo(final Map<String, String> token, final String idVehiculo, final GoogleMap mMap,String fron,String to) {
        sharedPreferences = context.getSharedPreferences("Datos", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("cargando...");
        progressDialog.setCancelable(false);
        progressDialog.show();
       //String url = "http://arepadevs.website/phonexx/public/api/recorridos?id_vehi="+idVehiculo+"&from="+""+"&to="+"";

        String url = "http://arepadevs.website/phonexx/public/api/recorridos?id_vehi="+idVehiculo+"&from=" +fron+"&to="+to;
       // http://arepadevs.website/phonexx/public/api/recorridos?id_vehi=5963&from=2016-11-24 10:16:57&to=2019-02-10 23:39:12

        final StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {


                final JSONObject data;
                JSONArray Vehiculos_list = null;
                List<Mo_Vehiculos> lista_vehiculos = new ArrayList<>();
                List<Mo_recorridos> lista_Recorrido = new ArrayList<>();


                if (response != null) {
                    try {
                        Vehiculos_list = new JSONArray(response.toString());//Guardo la respuesta en String y la convierto a  un JsonObject


                       // Toast.makeText(context, response.toString() + "  ", Toast.LENGTH_SHORT).show();


                        for (int i = 0; i < Vehiculos_list.length(); i++) {
                            JSONObject flota = Vehiculos_list.getJSONObject(i);
                            // Toast.makeText(context,flota.getString("co_coordx")+  "  "+i+ " - "+ flota.getString("co_coordy")   , Toast.LENGTH_SHORT).show();


                            //lista.add(new Mo_recorridos(idVehiculo,flota.getString("co_coordx"),flota.getString("co_coordy"),"",""));


                            cood.add(new LatLng(Double.parseDouble(flota.getString("co_coordx")), Double.parseDouble(flota.getString("co_coordy"))));

                            //Toast.makeText(context,   " COOD " + cood.size(), Toast.LENGTH_SHORT).show();
                        }

                         if (Vehiculos_list.length()>=1){
                             Crear_polyline(mMap);
                         }
                         else {
                             Toast.makeText(context, "La busqueda realizada no tuvo resultado, Intenta con otra fecha", Toast.LENGTH_SHORT).show();
                            
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                    }


                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, "Ha ocurrido un error al cargar los datos, Selecciona una nueva  vehiculo  para realizar una nueva búsqueda", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {//  Envio los parametros  para  la autenticacion
                return token;
            }

            {

            }
        };

        Volley_singleton.getInstancia_volley(context).addToRequestQueue(request);
    }
    //A partir  de un nombre de flota obtiene su Id que es  retornado  como una cadena  en esta funsion

    public String Mostrar_datos_vehiculo(final String nombre) {

        sharedPreferences = context.getSharedPreferences("Datos", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String Json = sharedPreferences.getString("VehiculoReco", "");//Json con la lista de  flotas del usuario

        String id_flota = "";

        JSONArray jArray = null;
        try {

            jArray = new JSONArray(Json);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject flota = jArray.getJSONObject(i);
                // Toast.makeText(context,flota.getString("id_flota")+  "  "+i+ " - "+ flota.getString("id_emp")+ " -- "+ flota.getString("no_descripcion_flota")   , Toast.LENGTH_SHORT).show();

                if (nombre.equals(flota.getString("no_descripcion_flota").toString().trim())) {//verifico que  el nombre enviado como parametro sea  igual al  elemto iterado
                    id_flota = flota.getString("id_flota");
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return id_flota;
    }


    public void Crear_marcador(GoogleMap mMap) {
        con++;


        LatLng dato;
        for (int i = 0; i < lista.size(); i++) {//  Mediante este for recorro la lista y se crea los marcadores con sus respectivo parametros

            dato = new LatLng(Double.parseDouble(lista.get(i).getCo_coordx()), Double.parseDouble(lista.get(i).getCo_coordy()));
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .draggable(true).position(dato)
                    .title(lista.get(i).getId_reco() + "&" + " 2" + "&" + " 3" + "&" + " 4") //+ "&" + lista.get(i).getEnt_id() + "&" + lista.get(i).getId() + "&" + lista.get(i).getStatus() + "&" + lista.get(i).getAddress())
                    .snippet(lista.get(i).getVelocidad()));
            marker.setTag(lista.get(i).getId_reco());




        }


        LatLng dato1 = new LatLng(Double.parseDouble(lista.get(0).getCo_coordx()), Double.parseDouble(lista.get(0).getCo_coordy()));

        CameraPosition cameraPosition = new CameraPosition.Builder()// fija la posicion de la camara  con un zoon de 15
                .target(dato1)
                .zoom(15)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }// Con este  metodo  se crean los marcadores

    public void Crear_polyline(GoogleMap mMap) {
        con++;




        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.argb(150, 0, 181, 247)).width(10);
        for (LatLng latLng : cood) {
            polylineOptions.add(latLng);
        }


        if (cood!=null){
            mMap.addPolyline(polylineOptions);


            Marker marker = mMap.addMarker(new MarkerOptions()//Crea un marcador al inicio de la  ruta
                    .draggable(true)
                    .position( new LatLng(cood.get(0).latitude,cood.get(0).longitude)   )
                    .title("Inicio") //+ "&" + lista.get(i).getEnt_id() + "&" + lista.get(i).getId() + "&" + lista.get(i).getStatus() + "&" + lista.get(i).getAddress())
                    .snippet(""));
            marker.setTag("");



            CameraPosition cameraPosition = new CameraPosition.Builder()// fija la posicion de la camara  con un zoon de 15
                    .target(new LatLng(cood.get(0).latitude,cood.get(0).longitude) )
                    .zoom(10)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }
        else {
            Toast.makeText(context, "La busqueda no tuvo resultados", Toast.LENGTH_SHORT).show();
        }





    }


    public void marker_defaul(Marker marker) {

        if (marker != null) {
            String[] info = marker.getTitle().split("&");


            if (info[3].equals("NEGATIVO")) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            } else if (info[3].equals("0-RECUPERADO")) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else if (info[3].equals("SIN GESTION")) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            } else if (info[3].equals("PACTADO")) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            } else if (info[3].equals("ELECTRONICO")) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
            }

        }


    }//Asigna el color  por defecto del marcador

    public void change_icon_marker(Marker marker) {

        if (marker != null) {
            String[] info = marker.getTitle().split("&");
            //Toast.makeText(getActivity(), "" +info[3], Toast.LENGTH_SHORT).show();


            if (info[3].equals("NEGATIVO")) {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.negativo));
            } else if (info[3].equals("0-RECUPERADO")) {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.negativo));
            } else if (info[3].equals("SIN GESTION")) {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.negativo));
            } else if (info[3].equals("PACTADO")) {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.negativo));
            } else if (info[3].equals("ELECTRONICO")) {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.negativo));
            }


        }

    }//Cambia el color del marcador  indicando que esta seleccionado

    public void Imagen_marcador( final  ImageView img){


        // Initialize a new ImageRequest
        ImageRequest imageRequest = new ImageRequest(
                "https://www.phonnexgps.com/GPSDEV/sistemagps/procesos/images/rotar/estado217.png", // Image URL
                new Response.Listener<Bitmap>() { // Bitmap listener
                    @Override
                    public void onResponse(Bitmap response) {
                        // Do something with response
                         img.setImageBitmap(response);

                        // Save this downloaded bitmap to internal storage
                        //  Uri uri = saveImageToInternalStorage(response);

                        // Display the internal storage saved image to image view
                        // mImageViewInternal.setImageURI(uri);
                    }
                },
                48, // Image width
                48, // Image height
                //ImageView.ScaleType.CENTER_CROP, // Image scale type
                Bitmap.Config.RGB_565, //Image decode configuration
                new Response.ErrorListener() { // Error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something with error response
                        error.printStackTrace();
                        //Snackbar.make(mCLayout,"Error",Snackbar.LENGTH_LONG).show();
                    }
                }
        );

        Volley_singleton.getInstancia_volley(context).addToRequestQueue(imageRequest);

    }
}
