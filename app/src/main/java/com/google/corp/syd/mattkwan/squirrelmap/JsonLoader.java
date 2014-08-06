package com.google.corp.syd.mattkwan.squirrelmap;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Loads JSON data from a remote URL.
 * Created by mattkwan on 8/6/14.
 */
public class JsonLoader {
    public static JSONObject load(String urlString) {
        URL url;

        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e("JsonLoader", e.getMessage());
            return null;
        }

        HttpURLConnection uc;

        try {
            uc = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            Log.e("JsonLoader", e.getMessage());
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            InputStream in = uc.getInputStream();
            byte[] buf = new byte[8192];

            for (;;) {
                int len = in.read(buf);

                if (len < 0)
                    break;

                baos.write(buf, 0, len);
            }
        } catch (IOException e) {
            Log.e("JsonLoader", e.getMessage());
            uc.disconnect();
            return null;
        }

        uc.disconnect();

        try {
            return new JSONObject(baos.toString());
        } catch (JSONException e) {
            Log.e("JsonLoader", e.getMessage());
            return null;
        }
    }
}
