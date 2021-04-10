package com.example.mojSpasilacAndroid;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
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

public class PutanjaVozilaActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private float lokacijaX;
    private float lokacijaY;

    private double mojaLokacijaX;
    private double mojaLokacijaY;

    private int brojOsoba;

    public SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

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

        // Add a marker in Sydney and move the camera
      /*  LatLng sydney = new LatLng(-32.2, 151);
        LatLng drugi=new LatLng(-32, 151);
        mMap.addMarker(new MarkerOptions()
                    .position(sydney)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.persons)));

        mMap.addMarker(new MarkerOptions()
                .position(drugi)
                .title("Marker in Sydney"));

        /**/
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(sredinaLat,sredinaLng),rastojanje));

        crtaj(mMap);
    }
    private void crtaj(GoogleMap map)
    {
        //GET LOKACIJA
        /*sharedPreferences = getApplicationContext().getSharedPreferences("KorisniciDB", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        mojaLokacijaX=sharedPreferences.getFloat("lokacija_vozila_x",15);
        mojaLokacijaY=sharedPreferences.getFloat("lokacija_vozila_y",0);*/

        Toast.makeText(PutanjaVozilaActivity.this, mojaLokacijaX+" "+mojaLokacijaY, Toast.LENGTH_SHORT).show();


        LatLng mojaLokacija = new LatLng(mojaLokacijaX,mojaLokacijaY);
        LatLng lokacijaCilj = new LatLng(lokacijaX,lokacijaY);

        if(brojOsoba==1) {
            mMap.addMarker(new MarkerOptions()
                    .position(lokacijaCilj));
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
        }
        else{
            mMap.addMarker(new MarkerOptions()
                    .position(lokacijaCilj));
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.persons)));
        }
        mMap.addMarker(new MarkerOptions()
                .position(mojaLokacija)
                .title("Marker in Sydney"));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mojaLokacija);
        builder.include(lokacijaCilj);
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);


    }
}