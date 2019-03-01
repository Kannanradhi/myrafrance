package com.isteer.b2c.volley;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by anbu on 10/12/2016.
 */

public interface VolleyTaskListener {

    void handleResult(String method_name, JSONObject response) throws JSONException;

    void handleError(VolleyError e);
}
