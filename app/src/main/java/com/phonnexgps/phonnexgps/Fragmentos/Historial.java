package com.phonnexgps.phonnexgps.Fragmentos;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.phonnexgps.phonnexgps.R;
import com.phonnexgps.phonnexgps.presentador.Presentador_historial;
import com.phonnexgps.phonnexgps.presentador.Presentador_spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Historial extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, View.OnClickListener, GoogleMap.OnInfoWindowClickListener, AdapterView.OnItemSelectedListener {
    Spinner Sp_Vehiculos, Sp_flota;
    private ImageView Img_buscar;
    Presentador_spinner presentador_spinner;
    Presentador_historial presentador_historial;
    int dia, mes, ano, hora, minutos;
    TextView Edit_inicio_fecha, Edit_inicio_hora, Edit_fin_fecha, Edit_fin_hora;
    private GoogleMap mMap;
    private static final int COLOR_BLACK_ARGB = 0xffF9A825;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    List<LatLng> cood = new ArrayList<>();
    String token="";
    Polyline polyline1;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int mMapTypes[] = {
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN
    };
    public
    LinearLayout PARTE00;
    Spinner Sp_Ve;
    public Historial() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment View view
        View view = inflater.inflate(R.layout.fragment_historial, container, false);
        Sp_Ve= (Spinner) view.findViewById(R.id.Sp_Ve);
        Sp_Ve.setOnItemSelectedListener(this);
        PARTE00 = (LinearLayout) view.findViewById(R.id.PARTE00);
 presentador_historial= new Presentador_historial(getActivity());
        sharedPreferences = getActivity().getSharedPreferences("Datos", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        token= sharedPreferences.getString("token", "");//Token de la sesion

        Img_buscar = (ImageView) view.findViewById(R.id.Img_buscar);
        Img_buscar.setOnClickListener(this);
        Edit_inicio_fecha = (TextView) view.findViewById(R.id.Edit_inicio_fecha);
        Edit_inicio_fecha.setOnClickListener(this);

        Edit_inicio_hora = (TextView) view.findViewById(R.id.Edit_inicio_hora);
        Edit_inicio_hora.setOnClickListener(this);

        Edit_fin_fecha = (TextView) view.findViewById(R.id.Edit_fin_fecha);
        Edit_fin_fecha.setOnClickListener(this);

        Edit_fin_hora = (TextView) view.findViewById(R.id.Edit_fin_hora);
        Edit_fin_hora.setOnClickListener(this);
        presentador_spinner = new Presentador_spinner(getActivity());
        String[] lista = {"Todos", "KJNDJHF7", "CR44CCC"};

        Sp_Vehiculos = (Spinner) view.findViewById(R.id.Sp_Vehiculos);
        presentador_spinner.Cargar_spiner_vehiculos(Sp_Vehiculos);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        Sp_flota = (Spinner) view.findViewById(R.id.Sp_Flotas);
        presentador_spinner.Cargar_spiner(Sp_flota);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        verificar_conexion();

        Fecha_hora();

        return view;
    }

    public void Tomar_fecha(final TextView edit) {

        final Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        ano = c.get(Calendar.YEAR);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
              String me=""+i1;
              String di=""+i2;



                if (i2<10){
                    di="0"+i2;

                }

               if (i1<10){
                     me="0"+ (i1+1);
                }


                edit.setText("" + i + "-" + me + "-" + di);





            }
        }, ano, mes, dia);


        datePickerDialog.show();

    }


    public void Tomar_hora(final TextView edit_hora) {


        final Calendar c = Calendar.getInstance();
        hora = c.get(Calendar.HOUR_OF_DAY);
        minutos = c.get(Calendar.MINUTE);


        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {


            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {

                if(i1<=10){
                    edit_hora.setText(i + ":0" + i1+ ":00");
                }
                else {
                    edit_hora.setText(i + ":" + i1+ ":00" );
                }





            }
        }, hora, minutos, true);

        timePickerDialog.show();

    }

    public  void Fecha_hora(){

        Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH) + 1;
        ano = c.get(Calendar.YEAR);

        String me=""+mes;
        String di=""+dia;


        if (dia<10){
            di="0"+dia;

        }

        if (mes<10){
            me="0"+ mes;
        }


        Edit_inicio_fecha.setText((ano-1)+"-"+me+"-"+di);
        Edit_fin_fecha.setText(ano+"-"+me+"-"+di);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Edit_inicio_fecha:
                Tomar_fecha(Edit_inicio_fecha);

                break;

            case R.id.Edit_inicio_hora:
                Tomar_hora(Edit_inicio_hora);
                break;
            case R.id.Edit_fin_fecha:
                Tomar_fecha(Edit_fin_fecha);
                break;

            case R.id.Edit_fin_hora:
                Tomar_hora(Edit_fin_hora);
                break;
            case R.id.Img_buscar:

                String fron= Edit_inicio_fecha.getText().toString()+" " +Edit_inicio_hora.getText().toString();

                String to= Edit_fin_fecha.getText().toString()+" " +Edit_fin_hora.getText().toString();
                presentador_historial.Consultar_vehiculo(token(),"5963",mMap,fron,to);

                break;


        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

  /*
        cood.add(new LatLng(-18.000328, 130.473633));
        cood.add(new LatLng(-16.173880, 135.087891));
        cood.add(new LatLng(-19.663970, 137.724609));
        cood.add(new LatLng(-23.202307, 135.395508));
        cood.add(new L/*

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.argb(150, 0, 181, 247)).width(15);
        for (LatLng latLng : cood) {
            polylineOptions.add(latLng);
        }

        mMap.addPolyline(polylineOptions);

        Marker marker = mMap.addMarker(new MarkerOptions()//Crea un marcador al inicio de la  ruta
                .draggable(true)
                .position( new LatLng(cood.get(0).latitude,cood.get(0).longitude)   )
                .title("4") //+ "&" + lista.get(i).getEnt_id() + "&" + lista.get(i).getId() + "&" + lista.get(i).getStatus() + "&" + lista.get(i).getAddress())
                .snippet(""));
        marker.setTag("");

        CameraPosition cameraPosition = new CameraPosition.Builder()// fija la posicion de la camara  con un zoon de 15
                .target(new LatLng(cood.get(0).latitude,cood.get(0).longitude) )
                .zoom(5)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        */


        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomControlsEnabled(true);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMarkerDragListener(this);
    }



    public void Select_flota(){

        Sp_flota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                String msupplier=Sp_flota.getSelectedItem().toString();
                String idFlota=presentador_historial.Obtener_id_fota(msupplier,PARTE00);

                //Toast.makeText(getActivity(), ""+presentador_tiempo_real.Obtener_id_fota(msupplier), Toast.LENGTH_SHORT).show();

                presentador_historial.Consultar_flota(token(),idFlota,Sp_Vehiculos);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    public void Select_vehiculo(){

        Sp_Vehiculos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                String msupplier=Sp_Vehiculos.getSelectedItem().toString();


                //presentador_historial.Consultar_vehiculo(token(),"5963",mMap);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {


            }
        });

    }



    public Map<String, String> token() {

        Map<String, String> parametro = new HashMap<String, String>();
        parametro.put("Content-Type", "application/json; charset=utf-8");
        parametro.put("Authorization","Bearer "+ token);

        return parametro;
    }// esta funsion devuelve un Mapa de datos que se le pasa como argumento al metodo regitrar que  esta en el presentador de registros

    public void verificar_conexion(){

        if (presentador_spinner.isOnline(getActivity()) == true) {

            Select_flota();
            Select_vehiculo();

        }
        else{
            Toast.makeText(getActivity(), "No tienes conexion a internet activa los datos o conecta a un wifi.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mMap.setMapType(mMapTypes[i]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
