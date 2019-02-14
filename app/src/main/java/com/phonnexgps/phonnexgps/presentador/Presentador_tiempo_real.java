package com.phonnexgps.phonnexgps.presentador;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
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

public class Presentador_tiempo_real {
    Context context;
    TextView Txt_Ubicacion,Txt_Chofer,Txt_ult_act;
    int  con=0;

    Bitmap ima=null;
    String url_marker="";
    public Presentador_tiempo_real(Context context, TextView txt_Ubicacion, TextView txt_Chofer, TextView txt_ult_act) {
        this.context = context;
        Txt_Ubicacion = txt_Ubicacion;
        Txt_Chofer = txt_Chofer;
        Txt_ult_act = txt_ult_act;
    }

    List<Mo_recorridos> lista = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    //A partir  de un nombre de flota obtiene su Id que es  retornado  como una cadena  en esta funsion
    public String Obtener_id_fota(final String nombre, LinearLayout   PARTE00 ) {

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

                        //Toast.makeText(context, data.getJSONArray("vehiculos").toString(), Toast.LENGTH_SHORT).show();

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
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 30000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 30000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

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
        JSONArray jArray = null;
        try {

            jArray = new JSONArray(vehiculos);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject flota = jArray.getJSONObject(i);


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




    public void Consultar_vehiculo(final Map<String, String> token, final String idVehiculo,final  GoogleMap mMap) {
        sharedPreferences = context.getSharedPreferences("Datos", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("cargando...");
        progressDialog.setCancelable(false);
        progressDialog.show();


      //  http://arepadevs.website/phonexx/public/api/vehiculos/5963/?historico=0
        String url = "http://arepadevs.website/phonexx/public/api/vehiculos/"+idVehiculo+"/?historico=0";
      //  String url = "http://arepadevs.website/phonexx/public/api/vehiculos/"+idVehiculo;
        final StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String id_reco ="";
                String co_coordx ="";    String co_coordy ="";   String velocidad ="";    String placa ="";    String estado ="";    String horometro ="";   String chofer ="";
                String tiempo_recorrido ="";  String direccion =""; String bloque ="";  String voltaje ="";
                String act="";
                JSONObject res = null;
                final JSONObject data;
                JSONArray Vehiculos_list = null;
                List<Mo_Vehiculos> lista_vehiculos= new ArrayList<>();
                List<Mo_recorridos> lista_Recorrido= new ArrayList<>();




                if (response != null) {
                    try {
                        res = new JSONObject(response.toString());//Guardo la respuesta en String y la convierto a  un JsonObject
                        data = new JSONObject(res.optString("data"));//Tomo del atributo data la informacion que me interesa
                        placa= data.getString("placa");
                        velocidad= data.getString("velocidad");
                        horometro= data.getString("ho_utiles");
                        id_reco= data.getString("ho_utiles");
                        chofer= data.getString("id_chofer");

                        url_marker= data.getString("url");

                      //Toast.makeText(context, response.toString() + "  ", Toast.LENGTH_SHORT).show();
                        Vehiculos_list = data.getJSONArray("ultimo_recorrido");
                        for (int i = 0; i < Vehiculos_list.length(); i++) {
                            JSONObject flota = Vehiculos_list.getJSONObject(i);
                           // Toast.makeText(context,flota.getString("co_coordx")+  "  "+i+ " - "+ flota.getString("co_coordy")   , Toast.LENGTH_SHORT).show();
                            //Toast.makeText(context, Vehiculos_list.toString() + "  ", Toast.LENGTH_SHORT).show();
                        //lista.add(new Mo_recorridos(idVehiculo,flota.getString("co_coordx"),flota.getString("co_coordy"),"",""));
                            co_coordx= flota.getString("co_coordx");
                            co_coordy=flota.getString("co_coordy");
                            estado=flota.getString("din1");
                            tiempo_recorrido=flota.getString("re_tiempo");
                            direccion=flota.getString("location");
                            bloque=flota.getString("out1");
                            voltaje=flota.getString("ext_voltaje");
                            id_reco=flota.getString("id_reco");
                            act=flota.getString("fe_actualiza_coord");
                        }

                    lista.add(new Mo_recorridos( id_reco,co_coordx,co_coordy,velocidad,placa,estado,horometro,chofer,tiempo_recorrido,direccion,bloque,voltaje,act));

                        editor.putString("VehiculoReco", data.toString());//Guardo  en el shared  vehiculos el  Json que contiene  la lista  con los vehiculo del usuario
                        editor.commit();
                        editor.apply();


                        Imagen_marcador(data.getString("url"),mMap);



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

        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 30000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 30000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });



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


    public void Crear_marcador(GoogleMap mMap,Bitmap ic) {
        con ++;
        LatLng dato;
        for (int i = 0; i < lista.size(); i++) {//  Mediante este for recorro la lista y se crea los marcadores con sus respectivo parametros

            dato = new LatLng(Double.parseDouble(lista.get(i).getCo_coordx()), Double.parseDouble(lista.get(i).getCo_coordy()));
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .draggable(true).position(dato)
                    .icon(BitmapDescriptorFactory.fromBitmap( ic  ))
                    .title(lista.get(i).getPlaca()+"&"+lista.get(i).getEstado()+"&"+lista.get(i).getVelocidad()+"&"+lista.get(i).getHorometro()+"&"+lista.get(i).getChofer()+"&"+lista.get(i).getTiempo_recorrido()+"&"+lista.get(i).getDireccion()+"&"+lista.get(i).getBloque()+"&"+lista.get(i).getVoltaje()+"&"+lista.get(i).getCo_coordx()+"&"+lista.get(i).getCo_coordy()   ) //+ "&" + lista.get(i).getEnt_id() + "&" + lista.get(i).getId() + "&" + lista.get(i).getStatus() + "&" + lista.get(i).getAddress())
                    .snippet(lista.get(i).getVelocidad()));
            marker.setTag(lista.get(i).getId_reco());
            editor.putString("Longitud", lista.get(i).getCo_coordy());
            editor.putString("latitud",  lista.get(i).getCo_coordx());

            editor.commit();
            editor.apply();

        }
        Actualizar_Texto(  ""+con,""+con,""+con);
        LatLng dato1 = new LatLng(Double.parseDouble(lista.get(0).getCo_coordx()), Double.parseDouble(lista.get(0).getCo_coordy()));
        CameraPosition cameraPosition = new CameraPosition.Builder()// fija la posicion de la camara  con un zoon de 15
                .target(dato1)
                .zoom(10)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        if (lista.get(0).getDireccion().equals("na")){
            Txt_Ubicacion.setText("Ubicacion: " );
        }
        else {
            Txt_Ubicacion.setText("Ubicacion: "+lista.get(0).getDireccion());
        }


        Txt_Chofer.setText("Chofer: "+lista.get(0).getChofer());
        Txt_ult_act.setText("Ult Act: "+ lista.get(0).getAct());

    }// Con este  metodo  se crean los marcadores

    public void Imagen_marcador( String url,final  GoogleMap mMap ){

       final  Matrix matrix = new Matrix();
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("cargando...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        // Initialize a new ImageRequest
        ImageRequest imageRequest = new ImageRequest(
                url, // Image URL
                new Response.Listener<Bitmap>() { // Bitmap listener
                    @Override
                    public void onResponse(Bitmap response) {

                        ima=response;
                        // Do something with response
                        //mImageView.setImageBitmap(response);

                        // Save this downloaded bitmap to internal storage
                      //  Uri uri = saveImageToInternalStorage(response);


                       // mImageViewInternal.setImageURI(uri);
                        progressDialog.dismiss();
                        Crear_marcador( mMap, redimensionarImagenMaximo( response, 64,  64  ));



                    }
                },
                100, // Image width
                100, // Image height
                //ImageView.ScaleType.CENTER_CROP, // Image scale type
                Bitmap.Config.RGB_565, //Image decode configuration
                new Response.ErrorListener() { // Error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something with error response
                        error.printStackTrace();
                        //  Snackbar.make(context,"Error",Snackbar.LENGTH_LONG).show();
                        progressDialog.dismiss();

                        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.mar);
                  Crear_marcador( mMap,bitmap);
                    }
                }
        );

        Volley_singleton.getInstancia_volley(context).addToRequestQueue(imageRequest);

    }

    public Bitmap redimensionarImagenMaximo(Bitmap mBitmap, float newWidth, float newHeigth){
        //Redimensionamos
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeigth) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        return Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);
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


    public void Actualizar_Texto(String ubicacion,String chofer,String ult){//Actualiza los TextView Ubicacion,nombre del chofer,

        Txt_Ubicacion.setText("Ubicacion:   "+ubicacion);
                Txt_Chofer.setText("Chofer: " +chofer);
        Txt_ult_act.setText( "Ult  Act:" +ult);

    }

}
