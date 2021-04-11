package com.example.mojSpasilacAndroid;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class PutanjaVozilaActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;
    private float lokacijaX;
    private float lokacijaY;

    private double mojaLokacijaX;
    private double mojaLokacijaY;
    private boolean postojiPrijava;
    private int id_prijave;

    LatLngBounds.Builder builder = new LatLngBounds.Builder();

    public SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;

    Button spasen;
    Marker mojMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_putanja_vozila);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        spasen = findViewById(R.id.button);
        if(!postojiPrijava)spasen.setEnabled(false);
        spasen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendIdPrijave();
            }
        });

    }
    private void sendIdPrijave()
    {
        AsyncHttpClient client = new AsyncHttpClient(true,80,443);
        JSONObject bodyJSON=new JSONObject();
        StringEntity bodySE = null;
        client.setAuthenticationPreemptive(true);
        SharedPreferences sharedPreferences;
        sharedPreferences = getApplicationContext().getSharedPreferences("KorisniciDB", MODE_PRIVATE);
        String token= sharedPreferences.getString("Token", "");
        client.addHeader("Authorization", "Bearer " + token);
        try {
            bodyJSON.put("id_prijave",id_prijave);
            bodySE = new StringEntity(bodyJSON.toString());
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
        client.post(null, "https://mojspasilac.asprogram.com/api/prijave/spasi", bodySE, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                Toast.makeText(getApplicationContext(), "Spasen "+id_prijave, Toast.LENGTH_SHORT).show();
                postojiPrijava=false;
                spasen.setEnabled(false);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(getApplicationContext(), "Nije Spasen!!!!! "+statusCode, Toast.LENGTH_SHORT).show();
                Log.i("ws", "---->>onFailure : " + statusCode);
            }

        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    private void getMojaLokacija()
    {
        locationTrack = new LocationTrack(PutanjaVozilaActivity.this);
        if (locationTrack.canGetLocation()) {

            mojaLokacijaX = locationTrack.getLatitude();
            mojaLokacijaY = locationTrack.getLongitude();

            //String ispis = String.valueOf(mojaLokacijaY) + " " + String.valueOf(mojaLokacijaX);
            //Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(mojaLokacijaY) + "\nLatitude:" + Double.toString(mojaLokacijaX), Toast.LENGTH_SHORT).show();
        } else {
            locationTrack.showSettingsAlert();
        }
    }
    private void sendMyLocation()
    {
        AsyncHttpClient client = new AsyncHttpClient(true,80,443);
        JSONObject bodyJSON=new JSONObject();
        StringEntity bodySE = null;
        client.setAuthenticationPreemptive(true);
        SharedPreferences sharedPreferences;
        sharedPreferences = getApplicationContext().getSharedPreferences("KorisniciDB", MODE_PRIVATE);
        String token= sharedPreferences.getString("Token", "");
        client.addHeader("Authorization", "Bearer " + token);
        try {
            bodyJSON.put("lokacija_vozila_x",mojaLokacijaX);
            bodyJSON.put("lokacija_vozila_y",mojaLokacijaY);
            bodySE = new StringEntity(bodyJSON.toString());
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
        client.post(null, "https://mojspasilac.asprogram.com/api/vozila/lokacija", bodySE, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"

                Toast.makeText(getApplicationContext(), "SVE OK!!!!", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)

                Log.i("ws", "---->>onFailure : " + statusCode);
            }

        });
    }

    private void drawMyLocation()
    {
        LatLng mojaLokacija = new LatLng(mojaLokacijaX,mojaLokacijaY);
        mMap.clear();
        if(postojiPrijava)crtaj(mMap);
        mojMarker = mMap.addMarker(new MarkerOptions()
                .position(mojaLokacija)
                .title("Moja Lokacija"));
        /*mojMarker=mMap.addMarker(new MarkerOptions()
                .position(mojaLokacija)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.persons)));*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    ///////////////////////// Za GPS LOKACIJU
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        getMojaLokacija();

        builder.include(new LatLng(mojaLokacijaX,mojaLokacijaY));

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                PutanjaVozilaActivity.this.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        //Toast.makeText(PutanjaVozilaActivity.this, "KURAC RADI", Toast.LENGTH_SHORT).show();
                        if(!postojiPrijava)getPrijavu(mMap);
                        getMojaLokacija();
                        //Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(mojaLokacijaY) + "\nLatitude:" + Double.toString(mojaLokacijaX), Toast.LENGTH_SHORT).show();
                        sendMyLocation();
                        drawMyLocation();
                    }
                });
            }
        }, 2000,5000);
        //getPrijavu(mMap);

    }
    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String) perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(PutanjaVozilaActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }
    private void crtaj(GoogleMap map)
    {

        LatLng lokacijaCilj = new LatLng(lokacijaX,lokacijaY);
        //Toast.makeText(PutanjaVozilaActivity.this, lokacijaX+" "+lokacijaY, Toast.LENGTH_SHORT).show();

        //TODO
        int brojOsoba = getIntent().getIntExtra("kapacitet", 1);
        if(brojOsoba ==2) {
            mMap.addMarker(new MarkerOptions()
                    .position(lokacijaCilj)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
        }
        else{
            mMap.addMarker(new MarkerOptions()
                    .position(lokacijaCilj)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.persons)));
        }

        builder.include(lokacijaCilj);
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void getPrijavu(GoogleMap map)
    {
        AsyncHttpClient client = new AsyncHttpClient(true,80,443);

        client.setAuthenticationPreemptive(true);
        SharedPreferences sharedPreferences;
        sharedPreferences = getApplicationContext().getSharedPreferences("KorisniciDB", MODE_PRIVATE);
        String token= sharedPreferences.getString("Token", "");
        client.addHeader("Authorization", "Bearer " + token);
        client.get("https://mojspasilac.asprogram.com/api/prijave/zaSpasioca",new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                try {
                    //Jrecepti =new JSONArray(new String(response));
                    JSONObject j = new JSONObject(new String(response));
                    Log.i("url ",j.toString());
                    lokacijaX= (float) j.getJSONObject("prijava").getDouble("lokacija_prijave_x");
                    lokacijaY= (float) j.getJSONObject("prijava").getDouble("lokacija_prijave_y");
                    id_prijave=j.getInt("id_prijave");
                   // Toast.makeText(PutanjaVozilaActivity.this, j.toString(), Toast.LENGTH_SHORT).show();
                    postojiPrijava=true;
                    spasen.setEnabled(true);
                    //crtaj(mMap);

                } catch (JSONException e) {
                    Log.i("rec","############## greska");
                    Log.i("rec","############ greska" + e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("fail","############ greska" + error);

            }
        });

    }

}