package com.example.mojSpasilacAndroid;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class PravljenjeVozilaActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText brojMesta;
    private Button prijaviVozilo;
    private float lokacijaVozilaX;
    private float lokacijaVozilaY;

    public SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pravljenje_vozila);

        brojMesta = findViewById(R.id.editTextNumber);
        prijaviVozilo = findViewById(R.id.button2);

        /////////////////////////////////   GPS

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        ///////////////////////// DOBIJANJE LOKACIJE PRITISKOM NA DUGME

        prijaviVozilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                locationTrack = new LocationTrack(PravljenjeVozilaActivity.this);


                if (locationTrack.canGetLocation()) {

                    lokacijaVozilaX = (float) locationTrack.getLatitude();
                    lokacijaVozilaY = (float) locationTrack.getLongitude();

                    String ispis = String.valueOf(lokacijaVozilaY) + " " + String.valueOf(lokacijaVozilaX);
                    ///textView.setText(ispis);
                    Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(lokacijaVozilaY) + "\nLatitude:" + Double.toString(lokacijaVozilaX), Toast.LENGTH_SHORT).show();
                    posaljiVozilo();

                    startActivity(new Intent(PravljenjeVozilaActivity.this, PutanjaVozilaActivity.class));
                    finish();

                } else {

                    locationTrack.showSettingsAlert();
                }

            }
        });






    }
    ////////////////////////////////////////////////////KRAJ DOBIJANJA LOKACIJE


    //////////////////////////////////////////////////////////////
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
        new AlertDialog.Builder(PravljenjeVozilaActivity.this)
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


    private void posaljiVozilo()
    {
        AsyncHttpClient client = new AsyncHttpClient(true,80,443);
        JSONObject bodyJSON=new JSONObject();
        StringEntity bodySE = null;
        sharedPreferences = getApplicationContext().getSharedPreferences("KorisniciDB", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        String token = sharedPreferences.getString("Token","");
        client.setAuthenticationPreemptive(true);
        client.addHeader("Authorization", "Bearer " + token);
        try {
            bodyJSON.put("kapacitet", Integer.parseInt(brojMesta.getText().toString()));
            bodyJSON.put("lokacija_vozila_x", lokacijaVozilaX);
            bodyJSON.put("lokacija_vozila_y", lokacijaVozilaY);
            //sharedPreferencesEditor.putFloat("lokacija_vozila_x",lokacijaVozilaX);///01:30am
           // sharedPreferencesEditor.putFloat("lokacija_vozila_y",lokacijaVozilaY);
            bodySE = new StringEntity(bodyJSON.toString());
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        client.post(null, "https://mojspasilac.asprogram.com/api/vozila", bodySE, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                Toast.makeText(PravljenjeVozilaActivity.this, "Vozilo uspesno prijavljeno!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PravljenjeVozilaActivity.this, PutanjaVozilaActivity.class));
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Toast.makeText(PravljenjeVozilaActivity.this, "Greska!!! "+statusCode, Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}