package com.example.mojSpasilacAndroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class RegisterActivity extends AppCompatActivity {

    private EditText eRegName;
    private EditText eRegPassword;
    private Button btn_Register;

    public  static Korisnik korisnik;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_Register = findViewById(R.id.btn_register);

        eRegName = findViewById(R.id.editTextUsername);
        eRegPassword = findViewById(R.id.editText_password);

        sharedPreferences = getApplicationContext().getSharedPreferences("KorisniciDB", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String regUsername = eRegName.getText().toString();
                String regPassword = eRegPassword.getText().toString();
                if(regUsername.isEmpty() || regPassword.length() < 4)
                {
                    Toast.makeText(RegisterActivity.this, "Unesi svoje podatke, šifra mora sadžati najmanje 4 karaktera!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    validate(regUsername, regPassword);
                    korisnik = new Korisnik(regUsername, regPassword);



                    //sharedPreferencesEditor.apply();

                    //                   startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }

            }
        });
    }

    private void validate(String userName, String userPassword)
    {
        /*if(userName.equals(RegisterActivity.korisnik.getUsername()) && userPassword.equals(RegisterActivity.korisnik.getUserpassword()))
        {
            return true;
        }*/

        AsyncHttpClient client = new AsyncHttpClient(true,80,443);
        JSONObject bodyJSON=new JSONObject();
        StringEntity bodySE = null;

        try {
            bodyJSON.put("username", userName);
            bodyJSON.put("password", userPassword);
            bodyJSON.put("id_tipa_korisnika",2); //NOVOOOOOOOOOOOOOOOOOOOOOOOOOOOO
            bodySE = new StringEntity(bodyJSON.toString());
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        client.post(null ,"https://mojspasilac.asprogram.com/api/korisnici/signup", bodySE, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                Toast.makeText(RegisterActivity.this, "Uspesno registrovanje!", Toast.LENGTH_SHORT).show();
                sharedPreferences = getApplicationContext().getSharedPreferences("KorisniciDB", MODE_PRIVATE);
                sharedPreferencesEditor = sharedPreferences.edit();


                sharedPreferencesEditor.putString("Username", userName);
                sharedPreferencesEditor.putString("Password", userPassword);
                sharedPreferencesEditor.putInt("id_tipa_korisnika",2);  // novooooooooooooooooooooooooooo

//                String token = new String(response, StandardCharsets.UTF_8);
//                Log.i("token","$$$$$$$$$$$$$$" + token);
//                sharedPreferencesEditor.putString("Token", token);

                sharedPreferencesEditor.apply();

                Log.i("ws","-----------KorisniciDB" + sharedPreferences.getAll().toString());

                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //String token = new String(errorResponse, StandardCharsets.UTF_8);

                //Log.i("lol","$$$$$$$$$$$$$$" + token);
                Toast.makeText(RegisterActivity.this, "Neuspešno registrovanje", Toast.LENGTH_SHORT).show();
            }

        });

    }

}