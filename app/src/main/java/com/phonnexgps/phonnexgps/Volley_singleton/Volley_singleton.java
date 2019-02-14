package com.phonnexgps.phonnexgps.Volley_singleton;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Volley_singleton {
    private static Volley_singleton instancia_volley;
    private RequestQueue request;
    private static Context context;

    private Volley_singleton(Context cntext) {
        context = cntext;
        request = getRecuestQueue();
    }

    private RequestQueue getRecuestQueue() {
        if (request == null) {
            request = Volley.newRequestQueue(context.getApplicationContext());
        }

        return request;
    }


    public static synchronized Volley_singleton getInstancia_volley(Context context) {

        if (instancia_volley == null) {
            instancia_volley = new Volley_singleton(context);
        }

        return instancia_volley;
    }

    public <T> void addToRequestQueue(Request<T> request) {

        getRecuestQueue().add(request);
    }


}
