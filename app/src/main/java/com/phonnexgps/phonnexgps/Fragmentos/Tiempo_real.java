package com.phonnexgps.phonnexgps.Fragmentos;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Marker;
import com.phonnexgps.phonnexgps.Google_maps.CustomInfoWindowAdapter;
import com.phonnexgps.phonnexgps.R;
import com.phonnexgps.phonnexgps.Vistas.calle;
import com.phonnexgps.phonnexgps.presentador.Presentador_spinner;
import com.phonnexgps.phonnexgps.presentador.Presentador_tiempo_real;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tiempo_real extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener, GoogleMap.OnInfoWindowClickListener, DialogInterface.OnClickListener, AdapterView.OnItemSelectedListener {
    private GoogleMap mMap;
    private Spinner Sp_Vehiculos, Sp_flota;
    private Presentador_spinner presentador_spinner;
    private TextView Txt_tiempo, Txt_Ubicacion, Txt_Chofer, Txt_ult_act;
    private Presentador_tiempo_real presentador_tiempo_real;// Instancia del presentado tiempo real
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private long periodo = 60000;
    LinearLayout PARTE00;
    View view;
    String token = "";
    Marker marcador_actual = null;
    Spinner Sp_Ve;
    ImageView Img_street;
    CountDownTimer countDownTimer;
    private int mMapTypes[] = {
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_HYBRID

    };

    public Tiempo_real() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tiempo_real, container, false);




        PARTE00 = (LinearLayout) view.findViewById(R.id.PARTE00);
        presentador_spinner = new Presentador_spinner(getActivity());
        Inializar_TextView();
        presentador_tiempo_real = new Presentador_tiempo_real(getActivity(), Txt_Ubicacion, Txt_Chofer, Txt_ult_act);
        sharedPreferences = getActivity().getSharedPreferences("Datos", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        token = sharedPreferences.getString("token", "");//Token de la sesion

        Sp_Vehiculos = (Spinner) view.findViewById(R.id.Sp_Vehiculos);

        presentador_spinner.Cargar_spiner(Sp_Vehiculos);

        Sp_flota = (Spinner) view.findViewById(R.id.Sp_flota);
        presentador_spinner.Cargar_spiner(Sp_flota);

        contador();
        verificar_conexion();//verifica la conexion a internet, si esta  activa ejecuta los metos para cargar flotas y  vehiculos

        Sp_Ve = (Spinner) view.findViewById(R.id.Sp_Ve);
        Sp_Ve.setOnItemSelectedListener(this);

        Img_street = (ImageView) view.findViewById(R.id.Img_street);
        Img_street.setOnClickListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;

    }

    public void contador() {
        countDownTimer = new CountDownTimer(periodo, 1000) {
            @Override
            public void onTick(long l) {
                periodo = l;
                actulizar_texto();
            }

            @Override
            public void onFinish() {
                presentador_tiempo_real.Consultar_vehiculo(token(), "5963", mMap);
                if (marcador_actual != null) {
                    marcador_actual.hideInfoWindow();
                }
                comenzar();

            }
        }.start();
    }// cronometro de  15 segundos  para  actualizar mapa

    public void comenzar() {
        countDownTimer.start();
    }



    public void actulizar_texto() {
        int minutes = (int) (periodo / 1000) / 60;
        int segundos = (int) (periodo / 1000) % 60;
        String tiempo_forma = String.format(Locale.getDefault(), "%02d:%02d", minutes, segundos);
        Txt_tiempo.setText("" + tiempo_forma);
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Img_street:

                Intent i = new Intent(getActivity(), calle.class);

                startActivity(i);

                break;
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        String[] info = marker.getTitle().split("&");

        String ur = "https://www.google.com/maps/place/" + info[9] + "+" + info[10];
        // Toast.makeText(getContext(), ""+ info[9], Toast.LENGTH_SHORT).show();
        // String ur= "https://www.google.com/maps/place/25°39'31.1\"N+80°25'03.1\"W";

        Uri uri = Uri.parse(ur);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        marcador_actual = marker;

        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomControlsEnabled(true);


        googleMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(getActivity()), getActivity()));
        mMap.setOnInfoWindowClickListener(this);


    }


    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }


    public void Select_flota() {

        Sp_flota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                String msupplier = Sp_flota.getSelectedItem().toString();
                String idFlota = presentador_tiempo_real.Obtener_id_fota(msupplier, PARTE00);

                //Toast.makeText(getActivity(), ""+presentador_tiempo_real.Obtener_id_fota(msupplier), Toast.LENGTH_SHORT).show();


                presentador_tiempo_real.Consultar_flota(token(), idFlota, Sp_Vehiculos);
                if (marcador_actual != null) {
                    marcador_actual.hideInfoWindow();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    public void Select_vehiculo() {

        Sp_Vehiculos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                String msupplier = Sp_Vehiculos.getSelectedItem().toString();

                //Toast.makeText(getActivity(), "vehiculo "+presentador_tiempo_real.Obtener_id_vehiculo(msupplier), Toast.LENGTH_SHORT).show();
                presentador_tiempo_real.Consultar_vehiculo(token(), "5963", mMap);
                if (marcador_actual != null) {
                    marcador_actual.hideInfoWindow();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {


            }
        });

    }


    public Map<String, String> token() {

        Map<String, String> parametro = new HashMap<String, String>();
        parametro.put("Content-Type", "application/json; charset=utf-8");
        parametro.put("Authorization", "Bearer " + token);

        return parametro;
    }// esta funsion devuelve un Mapa de datos que se le pasa como argumento al metodo regitrar que  esta en el presentador de registros


    public void Inializar_TextView() {

        Txt_tiempo = (TextView) view.findViewById(R.id.Txt_tiempo);
        Txt_Ubicacion = (TextView) view.findViewById(R.id.Txt_Ubicacion);
        Txt_Chofer = (TextView) view.findViewById(R.id.Txt_Chofer);
        Txt_ult_act = (TextView) view.findViewById(R.id.Txt_ult_act);


    }

    public void verificar_conexion() {

        if (presentador_spinner.isOnline(getActivity()) == true) {

            Select_flota();
            Select_vehiculo();

        } else {
            Toast.makeText(getActivity(), "No tienes conexion a internet activa los datos o conecta a un wifi.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}


