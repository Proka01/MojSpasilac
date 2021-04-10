package com.example.mojSpasilacAndroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class PravljenjeVozilaActivity extends AppCompatActivity {

    private EditText brojMesta;
    private Button prijaviVozilo;
    private double lokacijaVozilaX;
    private double lokacijaVozilaY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pravljenje_vozila);

        brojMesta = findViewById(R.id.editTextNumber);
        prijaviVozilo = findViewById(R.id.button2);

        lokacijaVozilaX=44.8;
        lokacijaVozilaY=20.46;

        prijaviVozilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posaljiVozilo();
            }
        });
    }

    private void posaljiVozilo()
    {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject bodyJSON=new JSONObject();
        StringEntity bodySE = null;

        try {
            bodyJSON.put("kapacitet", Integer.parseInt(brojMesta.getText().toString()));
            bodyJSON.put("lokacija_vozila_X", lokacijaVozilaX);
            bodyJSON.put("lokacija_vozila_Y", lokacijaVozilaY);
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
                Toast.makeText(PravljenjeVozilaActivity.this, "Greska!!!", Toast.LENGTH_SHORT).show();
            }

        });
    }
}