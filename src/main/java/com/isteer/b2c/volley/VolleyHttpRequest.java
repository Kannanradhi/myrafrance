package com.isteer.b2c.volley;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.utility.Logger;

import org.json.JSONException;
import org.json.JSONObject;


public class VolleyHttpRequest {

    private static VolleyTaskListener volley;
    private static Context getcontext;


    public static void makeVolleyPostHeader(final Fragment context, String URL, final JSONObject jsonObject, String method_name) {

        Logger.LogError("URL String", "" + URL);
        Logger.LogError("Input String", "" + jsonObject.toString());
        volley = (VolleyTaskListener) context;
        getcontext = context.getActivity();
        startVollyTask(URL, jsonObject, method_name);

    }
    public static void makeVolleyPostHeaderActivity(final Activity context, String URL, final JSONObject jsonObject, String method_name) {

        Logger.LogError("URL String", "" + URL);
        Logger.LogError("Input String", "" + jsonObject.toString());
        volley = (VolleyTaskListener) context;
        getcontext = context;
        startVollyTask(URL, jsonObject, method_name);

    }

    public static void startVollyTask(String URL, final JSONObject jsonObject, final String method_name) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            try {
                                Logger.LogError("response ", "" + response.toString());
                                volley.handleResult(method_name, response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volley.handleError(error);
            }
        });

        jsonObjectRequest.setRetryPolicy((new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
        B2CApp.getINSTANCE().addRequestQueue(jsonObjectRequest);
        B2CApp.getINSTANCE().getRequestQueue().getCache().remove(URL);
        B2CApp.getINSTANCE().getRequestQueue().getCache().clear();

    }

}
