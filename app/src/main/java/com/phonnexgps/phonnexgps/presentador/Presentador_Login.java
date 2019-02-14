package com.phonnexgps.phonnexgps.presentador;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.phonnexgps.phonnexgps.Vistas.Menus;
import com.phonnexgps.phonnexgps.Volley_singleton.Volley_singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Presentador_Login {
    Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    public Presentador_Login(Context context) {
        this.context = context;
    }

    public void Login(final Map<String, String> datos) {
        sharedPreferences = context.getSharedPreferences("Datos", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Verificando...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url = "http://arepadevs.website/phonexx/public/api/login";
        final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String token = "";
                String flotas = "";
                JSONObject res = null;
                final JSONObject PRIMER;
                JSONArray jArray = null;
                if (response != null) {


                    try {
                        res = new JSONObject(response.toString());
                        PRIMER = new JSONObject(res.optString("user"));
                        token = PRIMER.getString("token"); //Obtenemos el array results
                      // Toast.makeText(context, res.optString("user") + "  ", Toast.LENGTH_SHORT).show();
                        jArray = PRIMER.getJSONArray("flotas");
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject flota = jArray.getJSONObject(i);
                            // Toast.makeText(context,flota.getString("id_flota")+  "  "+i+ " - "+ flota.getString("id_emp")+ " -- "+ flota.getString("no_descripcion_flota")   , Toast.LENGTH_SHORT).show();
                            //Toast.makeText(context, jArray.toString() + "  ", Toast.LENGTH_SHORT).show();
                        }
                        editor.putString("token", token);
                        editor.putString("flotas", jArray.toString());
                        editor.putString("can_flotas", ""+jArray.length());

                        editor.commit();
                        editor.apply();

                        Intent intt = new Intent(context, Menus.class);
                        intt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        context.startActivity(intt);

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
                String er= error.toString().trim();

                if (er.equals("com.android.volley.AuthFailureError")){

                    Toast.makeText(context, "Los  datos ingresados son incorrectos", Toast.LENGTH_SHORT).show();
                }
                if (er.equals("com.android.volley.TimeoutError")){

                    Toast.makeText(context, "Error de conexion, Verifica tu conexion a internet", Toast.LENGTH_SHORT).show();
                }

                progressDialog.dismiss();
                Log.v("Error___volley",error.toString());

            }

        }) {

            /** Passing some request headers* */
            @Override
            public Map getParams() throws AuthFailureError {

                return datos;
            }

        };

  /*
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 3000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 3000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

*/
       // request.setRetryPolicy(new DefaultRetryPolicy(5 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley_singleton.getInstancia_volley(context).addToRequestQueue(request);
    }


    public boolean verificacion(EditText EdtUsuario, EditText EdtContrasena) {

        //  RESETEO DE ERRORES
        EdtUsuario.setError(null);
        EdtContrasena.setError(null);

        boolean cancel = false;
        View focusView = null;

        // verificar si el nombre est vacio.
        if (TextUtils.isEmpty(EdtUsuario.getText())) {
            EdtUsuario.setError("Debes ingresar un usuario");
            focusView = EdtUsuario;
            cancel = true;
        }

        // verificar si el campo contraseña esta vacio.
        else if (TextUtils.isEmpty(EdtContrasena.getText())) {
            EdtContrasena.setError("Debes ingresar una  contraseña");
            focusView = EdtContrasena;
            cancel = true;

        } else {
            return false;
        }

        if (cancel) {

            focusView.requestFocus();
        }

        return true;
    }// Metodo para  Validar la entrada de datos del usuario






}
