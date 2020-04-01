package com.example.linkder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.linkder.Adaptadores.ListaJuegosAdaptador;
import com.example.linkder.Adaptadores.MisJuegosAdaptador;
import com.example.linkder.Modelos.Juego;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListaJuegosActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SharedPreferences prefs;
    private RecyclerView recyclerView;
    private ListaJuegosAdaptador adaptador;
    private ArrayList<Juego> listaJuegos = new ArrayList<Juego>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_juegos);

        //toolbar
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
        }
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        //toolbar.setLogo(R.drawable.ic_home_white_24dp);
        //toolbar.setTitle("Ejemplo TOOLBAR");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        prefs =  getSharedPreferences("Preference", Context.MODE_PRIVATE);


        if(Utilidades.verificaConexion(getApplication())) {
            //si hay conexion a internet
            getAllListaJuegos();
        } else {
            Toast.makeText(getApplicationContext(),"Esta acción requiere conexión a internet", Toast.LENGTH_SHORT).show();
        }


        //adaptador
        adaptador = new ListaJuegosAdaptador(listaJuegos, new ListaJuegosAdaptador.OnItemClickListener() {
            /*Los eventos que se generan son los mismos OnClickListener que se agregaron en NotaAdaptador*/
            @Override
            public void OnItemClick(Juego juego, int position) {
                if(Utilidades.verificaConexion(getApplication())) {
                    //si hay conexion a internet
                    inscribirjuego(juego.getNombreJuego());
                } else {
                    Toast.makeText(getApplicationContext(),"Esta acción requiere conexión a internet", Toast.LENGTH_SHORT).show();
                }

            }


        });


        /*Se referencia el ReciclerView del layout*/
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        /* se agrega los valores por defecto para mostrar el reciclerview tipo de layout y la animación*/
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        /*Ahora se finalmente se le agrega el adaptador que se creo arriba al recyclerview que es el que contiene el listado*/
        recyclerView.setAdapter(adaptador);











    }
    private void inscribirjuego(String nombre){

        Map<String, String> params = new HashMap<String, String>();
        params.put("nombre_video_juego", nombre);
        String mailUsuarioLogueado = prefs.getString("mail", null);
        params.put("email", mailUsuarioLogueado);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = "http://abascur.cl/android/android_1/AgregarJuego"; //
        JsonObjectRequest jsonReque = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("success")) {

                                Toast.makeText(getApplicationContext(), "Se ha agregado a favoritos", Toast.LENGTH_LONG).show();
                            } else {
                                //Error 003 - rut invalido
                                Toast.makeText(getApplicationContext(), "Hubo un error en la petición", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();

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



    private void getAllListaJuegos(){

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = "http://abascur.cl/android/android_1/ListarJuegos";
        JsonObjectRequest jsonReque = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//Log.d(“RESPUESTA", response.toString());
                        try {
                            String status = response.getString("status");
                            if (status.equals("success")) {
                                //peticion exitosa pero puede haber o no dato
                                Object mensaje = response.get("mensaje");

                                guardaListaJuegos((JSONArray) mensaje);


                            } else{
                                Toast.makeText(getApplicationContext(), "Ha ocurrido un error en la petición", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
// VolleyLog.d(“RESPUESTA", "Error: " + error.getMessage());
            }
        });
        queue.add(jsonReque);

    }

    //guarda los datos recuperados en una lista
    private void guardaListaJuegos(JSONArray mensaje){
        try {
            /*se recorre el JSON y se agregan las nuevas notas*/
            if (mensaje.length() > 0 ) {
                for (int i = 0; i < mensaje.length(); i++) {
                    JSONObject jsonObject = mensaje.getJSONObject(i);
                    int idJuego = jsonObject.getInt("id_video_juego");
                    String nombreJuego = jsonObject.getString("nombre_video_juego");

                    listaJuegos.add(new Juego(idJuego, nombreJuego));
                }
            }


            /*se actualiza la lista del adaptador para ver los cambios reflejados*/
            adaptador.updateList(listaJuegos);


        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        Intent intent = new Intent(ListaJuegosActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Limpia las sharedPreference. Debe llamarse despues de registrar la accion
    private void limpiaSharedPreferences() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}
