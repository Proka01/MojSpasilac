package com.example.mojSpasilacAndroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class HitanAktiviti extends AppCompatActivity {

    Button btn_hitno_prijavi;
    EditText edit_text_br_osoba;
    private ArrayList<Osoba> osobe = new ArrayList<>();
    private int broj_osoba = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hitan_aktiviti);

        double longitude = getIntent().getDoubleExtra("longitude", 1);
        double latitude = getIntent().getDoubleExtra("latitude", 1);
        btn_hitno_prijavi = (Button) findViewById(R.id.btn_hitna_prijava);
        edit_text_br_osoba = (EditText) findViewById(R.id.editText_broj_osoba);
        broj_osoba = Integer.valueOf(String.valueOf(edit_text_br_osoba.getText()));

        //Toast.makeText(HitanAktiviti.this, String.valueOf(longitude)+" "+String.valueOf(latitude), Toast.LENGTH_LONG).show();

        btn_hitno_prijavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                broj_osoba = Integer.valueOf(String.valueOf(edit_text_br_osoba.getText()));
                for(int i = 0; i < broj_osoba; i++)
                {
                    Osoba o = new Osoba(40,2);
                    osobe.add(o);
                }

                if(osobe.size()!=0 && latitude!= -1 && longitude!= -1)
                {
                    validate(osobe,latitude,longitude);
                    Toast.makeText(HitanAktiviti.this, "Vaša prijava je poslata", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void validate(ArrayList<Osoba> osobe, double latitude, double longtitude)
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


                Toast.makeText(HitanAktiviti.this, "Uspesna prijava", Toast.LENGTH_SHORT).show();

                //startActivity(new Intent(LoginActivity.this, GlavniActivity.class));
                //finish();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if(statusCode == 0)
                    Toast.makeText(HitanAktiviti.this, "Neuspešna prijava. Proverite konekciju sa internetom", Toast.LENGTH_SHORT).show();
                else if(statusCode == 403)
                    Toast.makeText(HitanAktiviti.this, "403 je", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(HitanAktiviti.this, "Neuspešna prijava", Toast.LENGTH_SHORT).show();

                Log.i("ws", "---->>onFailure : " + statusCode);
            }

        });

    }


    /////////////////////////////////////////////////////////////////////////////////////////

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

}