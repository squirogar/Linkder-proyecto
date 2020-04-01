package com.example.linkder;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
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
import com.example.linkder.Adaptadores.MisJuegosAdaptador;
import com.example.linkder.Fragments.MostrarJugadoresFragment;
import com.example.linkder.Modelos.Juego;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MisJuegosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SharedPreferences prefs;
    private RecyclerView recyclerView;
    private MisJuegosAdaptador adaptador;
    private ArrayList<Juego> listaDeMisJuegos = new ArrayList<Juego>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_juegos);

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

        prefs = getSharedPreferences("Preference", Context.MODE_PRIVATE);

        //Se llena la lista con todos los juegos del usuario recuperados
        if(Utilidades.verificaConexion(getApplication())) {
            //si hay conexion a internet
            getAllListMisJuegos();
        } else {
            Toast.makeText(getApplicationContext(),"Esta acción requiere conexión a internet", Toast.LENGTH_SHORT).show();
        }


        //adaptador
        adaptador = new MisJuegosAdaptador(listaDeMisJuegos, new MisJuegosAdaptador.OnItemClickListener() {
            /*Los eventos que se generan son los mismos OnClickListener que se agregaron en NotaAdaptador*/
            @Override
            public void OnItemClick(Juego juego, int position) {
                mostrarJugadores(juego);
            }

            @Override
            public void OnDeleteClick(final Juego juego, final int position) {

                /*Tipo de dialog por defecto similar a un Alert en Js*/
                /*Solicita un contexto*/
                AlertDialog alertDialog = new AlertDialog.Builder(MisJuegosActivity.this,R.style.AlertDialogStyle).create();
                /*Se agrega un titulo*/
                alertDialog.setTitle("Alerta");
                /*Se agrega un Mensaje*/
                alertDialog.setMessage("¿Esta seguro que quiere eliminar la nota " + juego.getNombreJuego() + "?");
                /*Se agregan los botones con sus respectivos eventos*/
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }
                );
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(Utilidades.verificaConexion(getApplication())) {
                                    //si hay conexion a internet
                                    deleteJuegoFavorito(juego);
                                    adaptador.removeItem(position);
                                } else {
                                    Toast.makeText(getApplicationContext(),"Esta acción requiere conexión a internet", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        });
                /*se lanza el dialog*/
                alertDialog.show();
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


    //Muestra los jugadores de un juego especifico en un fragment
    private void mostrarJugadores(Juego juego) {
        /*Para poder configurar nuestro propio fragment y mostrarlo necesitamos de supportFragment*/
        FragmentManager fm = getSupportFragmentManager();
        MostrarJugadoresFragment dialogFragment = new MostrarJugadoresFragment().setAdapter(adaptador).setJuego(juego);
        dialogFragment.show(fm, "Sample Fragment");

    }




    //Metodo para eliminar un juego de favoritos
    public void deleteJuegoFavorito(Juego juego){
        Map<String, String> params = new HashMap<String, String>();
        String email = prefs.getString("mail", null);

        if(email == null)
            return;

        params.put("email", email);
        params.put("nombre_video_juego", juego.getNombreJuego());

        String URL = "http://abascur.cl/android/android_1/BorrarJuego";//cambiar!

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonReque = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("success")) {
                                Toast.makeText(getApplicationContext(), "Juego eliminado", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Ha ocurrido un error en la petición", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                // VolleyLog.d("JSONPost", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonReque);
    }


    //trae todas los juegos de la bd que el usuario haya agregado como favoritos
    private void getAllListMisJuegos(){
        Map<String, String> params = new HashMap<String, String>();
        String email = prefs.getString("mail", null);

        if(email == null)
            return;

        params.put("email", email);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = "http://abascur.cl/android/android_1/ListarJuegosUsuario";//cambiar!

        JsonObjectRequest jsonReque = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("success")) {
                                //peticion exitosa pero puede haber o no dato
                                Object mensaje = response.get("mensaje");
                                if(!mensaje.equals("NoData")){

                                    guardaListaMisJuegos((JSONArray) mensaje);

                                }else{
                                    Toast.makeText(getApplicationContext(), "No hay juegos registrados en favoritos", Toast.LENGTH_SHORT).show();

                                }



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
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonReque);
    }


    //guarda los datos recuperados en una lista
    private void guardaListaMisJuegos(JSONArray mensaje){
        try {
            /*se recorre el JSON y se agregan las nuevas notas*/
            if (mensaje.length() > 0 ) {
                for (int i = 0; i < mensaje.length(); i++) {
                    JSONObject jsonObject = mensaje.getJSONObject(i);
                    int idJuego = jsonObject.getInt("id_video_juego");
                    String nombreJuego = jsonObject.getString("nombre_video_juego");

                    listaDeMisJuegos.add(new Juego(idJuego, nombreJuego));
                }
            }


            /*se actualiza la lista del adaptador para ver los cambios reflejados*/
            adaptador.updateList(listaDeMisJuegos);


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
        Intent intent = new Intent(MisJuegosActivity.this,MainActivity.class);
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