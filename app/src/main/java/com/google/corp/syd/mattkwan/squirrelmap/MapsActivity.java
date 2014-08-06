package com.google.corp.syd.mattkwan.squirrelmap;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity {

    private SeekBar mSlider;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private final String ServerURL = "http://radiant-forest-9849.herokuapp.com/easteregg/pop?population=";
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        mSlider = (SeekBar) findViewById(R.id.slider);

        mSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    loadAndShowMarkers(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        loadAndShowMarkers(0);
    }

    private void loadAndShowMarkers(final int population) {
        if(isLoading) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                isLoading = true;
                final JSONObject markers = JsonLoader.load(ServerURL+population);
                isLoading = false;

                if (markers != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showMarkers(markers);
                        }
                    });
                }
            }
        }.start();
    }

    private void showMarkers(JSONObject markers) {
        JSONArray ja = markers.optJSONArray("features");

        mMap.clear();
        for (int i = 0; i < ja.length(); i++) {
            JSONObject obj = ja.optJSONObject(i);
            JSONArray coord = obj.optJSONObject("geometry").optJSONArray("coordinates");

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(coord.optDouble(1), coord.optDouble(0)))
                    .title(obj.optJSONObject("properties").optString("name")));
        }
    }
}
