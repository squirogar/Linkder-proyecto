package com.example.linkder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SharedPreferences prefs;
    private Button btnPerfil, btnListaJuegos, btnMisJuegos;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //toolbar
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
        }
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        //toolbar.setLogo(R.drawable.ic_home_white_24dp);
        //toolbar.setTitle("Ejemplo TOOLBAR");
        setSupportActionBar(toolbar);

        prefs =  getSharedPreferences("Preference", Context.MODE_PRIVATE);


        //botones
        btnPerfil = findViewById(R.id.btnPerfil);
        btnMisJuegos = findViewById(R.id.btnMisJuegos);
        btnListaJuegos = findViewById(R.id.btnListaJuegos);

        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mailUsuarioLogueado = prefs.getString("mail", null);
                if(mailUsuarioLogueado != null)
                    if(Utilidades.verificaConexion(getApplication())) {
                        //si hay conexion a internet
                        getDatosUsuario(mailUsuarioLogueado);
                    } else {
                        Toast.makeText(getApplicationContext(),"Esta acción requiere conexión a internet", Toast.LENGTH_SHORT).show();
                    }

            }
        });

        btnMisJuegos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(HomeActivity.this, MisJuegosActivity.class);
                startActivity(intent);
            }
        });

        btnListaJuegos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(HomeActivity.this, ListaJuegosActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuhome, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id_seleccion = item.getItemId(); //obtenemos el id de la opcion seleccionada
        switch (id_seleccion) {
            case R.id.menu_salir:
                //si se selecciono "salir"
                cierraSesion();
                return true;
            default:
                Toast.makeText(getApplicationContext(), "error en procesar seleccion", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }

    }

    private void cierraSesion() {
        limpiaSharedPreferences();
        Intent intent = new Intent(HomeActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Limpia las sharedPreference. Debe llamarse despues de registrar la accion
    private void limpiaSharedPreferences() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    //se recuperan los datos de usuario logueado en la aplicación, se ponen en un bundle y se envía a una mueva actividad
    void getDatosUsuario(String mail) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", mail); //esta bien como mail? o email???

        String URL = "http://abascur.cl/android/android_1/ObtenerUsuario"; //cambiar!
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonReque = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String status = response.getString("status");
                            if (status.equals("success"))
                            {
                                //peticion exitosa pero puede haber o no dato
                                Object mensaje = response.get("mensaje");

                                    Bundle b = new Bundle();

                                    b.putString("nick", ((JSONObject) mensaje).get("nick").toString());
                                    b.putString("email",((JSONObject) mensaje).get("email").toString());
                                    b.putString("descripcion", ((JSONObject) mensaje).get("descripcion").toString());
                                    b.putString("contactos", ((JSONObject) mensaje).get("contactos").toString());
                                    intent = new Intent(HomeActivity.this, PerfilActivity.class);
                                    intent.putExtras(b);
                                    startActivity(intent);

                            } else {
                                //cuando status no es "success"
                                Toast.makeText(getApplicationContext(), "Ha ocurrido un error en la petición", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        queue.add(jsonReque);
    }
}