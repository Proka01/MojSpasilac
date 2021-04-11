package com.example.mojSpasilacAndroid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

public class GlavniActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{



    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    Button btn;
    TextView textView;
    EditText brojgodina;
    Spinner spinner;
    Button dodaj_sledecu;
    Button potvrdi;
    TextView tv_broj_osoba;
    String[] stanja={"zdrav","bolestan"};
    private int broj_dodatih_osoba;
    private int br_godina;
    private String zdravstveno_stanje;
    private ArrayList<Osoba> osobe = new ArrayList<>();
    private String pomocni = "";
    double latitude;
    double longitude;

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glavni);

        btn = (Button) findViewById(R.id.btn);
        textView = (TextView) findViewById(R.id.lokacija);
        dodaj_sledecu = (Button) findViewById(R.id.btn_sledeca_osoba);
        potvrdi = (Button) findViewById(R.id.btn_potvrda);
        brojgodina = (EditText) findViewById(R.id.editText_broj_godina);
        tv_broj_osoba = (TextView) findViewById(R.id.tv_broj_osoba);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        broj_dodatih_osoba = 0;
        zdravstveno_stanje = stanja[0];
        String pom = brojgodina.getText().toString();
        br_godina = Integer.parseInt(pom);
        latitude = -1;
        longitude = -1;

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,stanja);  //simple_spinner_item
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(aa);

        /////////////////////////////////CITANJE PRIJAVE/////////////////////////////////////////////////////////////////////////////////

        dodaj_sledecu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Osoba osoba;
                br_godina = Integer.valueOf(String.valueOf(brojgodina.getText()));
                if(zdravstveno_stanje == stanja[0]) osoba = new Osoba(br_godina,1);  //zdrav
                else osoba = new Osoba(br_godina,2);

                osobe.add(osoba);
                broj_dodatih_osoba++;
                tv_broj_osoba.setText("   "+String.valueOf(broj_dodatih_osoba));
            }
        });

        ////////////////////////////////////////KRAJ CITANJA PRIJAVE///////////////////////////////////////////////////////////////////////


        /////////////////////////////////////////////POTVRDI///////////////////////////////////////////////////////////////
        potvrdi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {         //TODO TESTIRATI OVO SRANJE
                /*for(Osoba o : osobe)
                {
                    pomocni+= String.valueOf(o.getZdravstvenoStanje()) + "-"+String.valueOf(o.getBrGodina())+" ; ";
                }*/

                if(osobe.size()!=0 && latitude!= -1 && longitude!= -1)
                {
                    validate(osobe,latitude,longitude);
                    Toast.makeText(GlavniActivity.this, "Vaša prijava je poslata", Toast.LENGTH_LONG).show();
                }

                //String ispis = formatString(osobe);
                //tv_broj_osoba.setText(ispis);
                /*Toast.makeText(GlavniActivity.this, pomocni, Toast.LENGTH_LONG).show();
                */
            }
        });

        /////////////////////////////////////////////////////////ZA GPS LOKACIJU//////////////////////////////////////////////

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }



//////////////////////////////////////////////DOBIJANJE LOKACIJE PRITISKOM NA DUGME////////////////////////////////////////////////////

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                locationTrack = new LocationTrack(GlavniActivity.this);


                if (locationTrack.canGetLocation()) {

                    latitude = locationTrack.getLatitude();
                    longitude = locationTrack.getLongitude();

                    String ispis = String.valueOf(longitude) + " " + String.valueOf(latitude);
                    textView.setText(ispis);
                    Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
                } else {

                    locationTrack.showSettingsAlert();
                }

            }
        });

    }
///////////////////////////////////////////KRAJ DOBIJANJA LOKACIJE/////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////REGULISANJE GPS-A///////////////////////////////////////////////////////
    ///NPR ako je isklucen, ako ne radi, da se otvori i da se ukljuci gps itd //////////////////////////////////////
    ////////////////Sve ispod je zaduzeno za to/////////////////////////////////////////////////////////////////////

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
        new AlertDialog.Builder(GlavniActivity.this)
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
    //////////////////////////////////////////////KRAJ GPSA////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), stanja[position], Toast.LENGTH_LONG).show();
        zdravstveno_stanje = stanja[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    ///////////////////////////////////////POST ZAHTEV METODA/////////////////////////////////////////////

    private void validate(ArrayList<Osoba> osobe,double latitude,double longtitude)
    {
        AsyncHttpClient client = new AsyncHttpClient(true,80,443);
        JSONObject bodyJSON=new JSONObject();
        StringEntity bodySE = null;

        SharedPreferences sharedPreferences;
        sharedPreferences =getApplicationContext().getSharedPreferences("KorisniciDB", MODE_PRIVATE); //GlavniActivity.this
        String token = sharedPreferences.getString("Token", "");
        Log.i("ws", "-----------TOOOOOOOOOOOOOKEN " + token);
        client.setAuthenticationPreemptive(true);
        client.addHeader("Authorization", "Bearer " + token);



        try {
            bodyJSON.put("lokacija_prijave_x", latitude);
            bodyJSON.put("lokacija_prijave_y", longtitude);
            String formatiran = formatString(osobe);
            bodyJSON.put("osobe",formatiran);
            bodySE = new StringEntity(bodyJSON.toString());


        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
        client.post(null, "https://mojspasilac.asprogram.com/api/prijave", bodySE, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"


                Toast.makeText(GlavniActivity.this, "Uspesna prijava", Toast.LENGTH_SHORT).show();

                //startActivity(new Intent(LoginActivity.this, GlavniActivity.class));
                //finish();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if(statusCode == 0)
                    Toast.makeText(GlavniActivity.this, "Neuspešna prijava. Proverite konekciju sa internetom", Toast.LENGTH_SHORT).show();
                else if(statusCode == 403)
                    Toast.makeText(GlavniActivity.this, "403 je", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(GlavniActivity.this, "Neuspešna prijava", Toast.LENGTH_SHORT).show();

                Log.i("ws", "---->>onFailure : " + statusCode);
            }

        });

    }

    //////////////////////////////////////////////KRAJ POST-A/////////////////////////////////////////////

    //////////////////////////////////////////////METODA FORMAT STRING ZA JSON////////////////////////////
    String formatString(ArrayList<Osoba> osobe)   // [{"broj_godina":20,"id_zdravstvenog_stanja":1}]
    {
        int br = 1;
        int n = osobe.size();
        String ret = "[";
        for(Osoba o : osobe)
        {
            String pom = "{\"broj_godina\":";
            pom+=String.valueOf(o.getBrGodina());
            pom+=",";
            pom+="\"id_zdravstvenog_stanja\":";
            pom+=String.valueOf(o.getZdravstvenoStanje());
            pom+="}";

            ret+=pom;
            if(br != n) ret+=",";
            br++;
        }
        ret+="]";
        return ret;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////
}