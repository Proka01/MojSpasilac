package com.example.mojSpasilacAndroid;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class PutanjaVozilaActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;
    private float lokacijaX;
    private float lokacijaY;

    private double mojaLokacijaX;
    private double mojaLokacijaY;

    private int brojOsoba;
    LatLngBounds.Builder builder = new LatLngBounds.Builder();

    public SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;

    long startTime = 0;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;



            timerHandler.postDelayed(this, 500);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_putanja_vozila);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



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

        /////////////////////DOBIJANJE LOKACIJE
        locationTrack = new LocationTrack(PutanjaVozilaActivity.this);
        if (locationTrack.canGetLocation()) {

            mojaLokacijaX = locationTrack.getLatitude();
            mojaLokacijaY = locationTrack.getLongitude();

            String ispis = String.valueOf(mojaLokacijaY) + " " + String.valueOf(mojaLokacijaX);
            Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(mojaLokacijaY) + "\nLatitude:" + Double.toString(mojaLokacijaX), Toast.LENGTH_SHORT).show();
        } else {

            locationTrack.showSettingsAlert();
        }
        LatLng mojaLokacija = new LatLng(mojaLokacijaX,mojaLokacijaY);
        mMap.addMarker(new MarkerOptions()
                .position(mojaLokacija)
                .title("Marker in Sydney"));


        builder.include(mojaLokacija);

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
        //GET LOKACIJA
        /*sharedPreferences = getApplicationContext().getSharedPreferences("KorisniciDB", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        mojaLokacijaX=sharedPreferences.getFloat("lokacija_vozila_x",15);
        mojaLokacijaY=sharedPreferences.getFloat("lokacija_vozila_y",0);*/

        //Toast.makeText(PutanjaVozilaActivity.this, mojaLokacijaX+" "+mojaLokacijaY, Toast.LENGTH_SHORT).show();


        LatLng lokacijaCilj = new LatLng(lokacijaX,lokacijaY);
        //Toast.makeText(PutanjaVozilaActivity.this, lokacijaX+" "+lokacijaY, Toast.LENGTH_SHORT).show();

        //TODO
        brojOsoba = getIntent().getIntExtra("kapacitet",1);
        if(brojOsoba==1) {
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
                   // Toast.makeText(PutanjaVozilaActivity.this, j.toString(), Toast.LENGTH_SHORT).show();
                    crtaj(mMap);

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