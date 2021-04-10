package com.example.mojSpasilacAndroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class LoginActivity extends AppCompatActivity {

    private EditText eName;
    private EditText ePassword;

    private Button eLogin;
    private TextView eRegister;


    public SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        eName = findViewById(R.id.editTextTextPersonName);
        ePassword = findViewById(R.id.editTextTextPassword);
        eLogin = findViewById(R.id.button);
        eRegister=findViewById(R.id.textView3);

        sharedPreferences = getApplicationContext().getSharedPreferences("KorisniciDB", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        String loginToken = sharedPreferences.getString("Token",null);//ulaz bez logina

        if(sharedPreferences != null){

            String savedUsername = sharedPreferences.getString("Username", "");
            String savedPassword = sharedPreferences.getString("Password", "");
            //String savedToken = sharedPreferences.getString("")


            RegisterActivity.korisnik = new Korisnik(savedUsername, savedPassword);

            eName.setText(savedUsername);
            ePassword.setText(savedPassword);

        }

        eRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        eLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = eName.getText().toString();
                String userPassword = ePassword.getText().toString();


                if(userName.isEmpty() || userPassword.length()<4)
                {
                    Toast.makeText(LoginActivity.this, "Unesi ime i lozinku!", Toast.LENGTH_SHORT).show();

                }
                else {

                    //Toast.makeText(LoginActivity.this, userName + " " + userPassword, Toast.LENGTH_SHORT).show();

                    validate(userName, userPassword);

                }
            }
        });
    }


    private void validate(String userName, String userPassword)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject bodyJSON=new JSONObject();
        StringEntity bodySE = null;


        try {
            bodyJSON.put("username", userName);
            bodyJSON.put("password", userPassword);
            bodySE = new StringEntity(bodyJSON.toString());


        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
        client.post(null, "https://mojspasilac.asprogram.com/api/korisnici/login", bodySE, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                sharedPreferences = getApplicationContext().getSharedPreferences("KorisniciDB", MODE_PRIVATE);
                sharedPreferencesEditor = sharedPreferences.edit();

                String token = null;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject(new String(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    token = jsonObject.getString("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i("token", "######## loginToken" + token);

                sharedPreferencesEditor.putString("Username", userName);
                sharedPreferencesEditor.putString("Password", userPassword);
                sharedPreferencesEditor.putInt("id_tipa_korisnika",2);// MOZDA I NE TREBA OVO
                eName.setText(userName);
                ePassword.setText(userPassword);

                sharedPreferencesEditor.putString("Token", token);
                sharedPreferencesEditor.apply();

                Log.i("ws", "-----------KorisniciDB" + sharedPreferences.getAll().toString());

                startActivity(new Intent(LoginActivity.this, GlavniActivity.class));
                finish();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if(statusCode == 0)
                    Toast.makeText(LoginActivity.this, "Neuspešno logovanje. Proverite konekciju sa internetom", Toast.LENGTH_SHORT).show();
                else if(statusCode == 403)
                    Toast.makeText(LoginActivity.this, "Neuspešno logovanje. Korisničko ime ili lozinka nisu ispravni", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(LoginActivity.this, "Neuspešno logovanje", Toast.LENGTH_SHORT).show();

                Log.i("ws", "---->>onFailure : " + statusCode);
            }

        });

    }
}