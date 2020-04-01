package com.example.linkder.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.linkder.Adaptadores.JugadoresAdaptador;
import com.example.linkder.Adaptadores.MisJuegosAdaptador;
import com.example.linkder.HomeActivity;
import com.example.linkder.MisJuegosActivity;
import com.example.linkder.Modelos.Juego;
import com.example.linkder.Modelos.Jugador;
import com.example.linkder.PerfilActivity;
import com.example.linkder.R;
import com.example.linkder.Utilidades;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MostrarJugadoresFragment extends DialogFragment {

    private MisJuegosAdaptador adaptadorJuego;
    private JugadoresAdaptador adaptadorJugadores;
    private Juego juego;
    private TextView textTitulo;
    private Intent intent;
    private DialogInterface.OnDismissListener onDismissListener;
    private RecyclerView recyclerView;
    private ArrayList<Jugador> listaDeJugadores = new ArrayList<Jugador>();

    public MostrarJugadoresFragment() {

    }

    /*método para agregar el adaptador desde la actividad*/
    public MostrarJugadoresFragment setAdapter(MisJuegosAdaptador adaptadorJuego) {
        this.adaptadorJuego = adaptadorJuego;
        return this;
    }

    public MostrarJugadoresFragment setJuego(Juego juego) {
        this.juego = juego;
        return this;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Se asocia la vista con el layout respectivo
        View v = inflater.inflate(R.layout.mostrar_jugadores_fragment, container, false);


        /*Se utiliza igual que una actividad con la diferencia que antes del findViewById se debe indicar la vista que referencia*/
        textTitulo = v.findViewById(R.id.textTitulo);
        textTitulo.setText(textTitulo.getText().toString() + juego.getNombreJuego());


        //aca iria el codigo para inflar el otro adapter: JugadoresAdaptador
        //Se llena la lista con todos los jugadores que les gusto el juego
        if(Utilidades.verificaConexion(getActivity().getApplication())) {
            //si hay conexion a internet
            getAllListJugadores(juego.getNombreJuego());
        } else {
            Toast.makeText(getActivity().getApplicationContext(),"Esta acción requiere conexión a internet", Toast.LENGTH_SHORT).show();
        }


        //adaptador de jugadores
        adaptadorJugadores = new JugadoresAdaptador(listaDeJugadores, new JugadoresAdaptador.OnItemClickListener() {
            /*Los eventos que se generan son los mismos OnClickListener que se agregaron en NotaAdaptador*/
            @Override
            public void OnItemClick(Jugador jugador, int position) {
                //se visita el perfil de ese jugador
                if(Utilidades.verificaConexion(getActivity().getApplication())) {
                    //si hay conexion a internet
                    getDatosUsuario(jugador.getEmail());
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),"Esta acción requiere conexión a internet", Toast.LENGTH_SHORT).show();
                }
            }


        });


        /*Se referencia el ReciclerView del layout*/
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        /* se agrega los valores por defecto para mostrar el reciclerview tipo de layout y la animación*/
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        /*Ahora se finalmente se le agrega el adaptador que se creo arriba al recyclerview que es el que contiene el listado*/
        recyclerView.setAdapter(adaptadorJugadores);

        return v;
    }

    //trae todas los jugadores de la bd que tengan como favorito ese juego
    private void getAllListJugadores(String juego){
        Map<String, String> params = new HashMap<String, String>();

        params.put("nombre_video_juego", juego);

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String URL = "http://abascur.cl/android/android_1/ObtenerUsuariosJuego";//cambiar!

        JsonObjectRequest jsonReque = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("success")) {

                                //peticion exitosa pero puede haber o no dato
                                Object mensaje = response.get("mensaje");
                                if (!mensaje.equals("NoData")) {
                                    guardaListaJugadores((JSONArray) mensaje);
                                }else{

                                    Toast.makeText(getActivity().getApplicationContext(), "No hay jugadores", Toast.LENGTH_SHORT).show();
                                }

                            } else{
                                Toast.makeText(getActivity().getApplicationContext(), "Ha ocurrido un error en la petición", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            Toast.makeText(getActivity().getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonReque);
    }


    //guarda los datos recuperados en una lista
    private void guardaListaJugadores(JSONArray mensaje){
        try {
            /*se recorre el JSON y se agregan las nuevas notas*/
            if (mensaje.length() > 0 ) {
                for (int i = 0; i < mensaje.length(); i++) {
                    JSONObject jsonObject = mensaje.getJSONObject(i);
                    String nick = jsonObject.getString("nick");
                    String email = jsonObject.getString("email");

                    listaDeJugadores.add(new Jugador(nick, email));
                }
            }


            /*se actualiza la lista del adaptador para ver los cambios reflejados*/
            adaptadorJugadores.updateList(listaDeJugadores);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    //se recuperan los datos del jugador seleccionado, se ponen en un bundle y se envía a una nueva actividad
    //donde se visitará su perfil
    void getDatosUsuario(String mail) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", mail); //esta bien como mail? o email???

        String URL = "http://abascur.cl/android/android_1/ObtenerUsuario"; //cambiar!
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

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
                                intent = new Intent(getActivity(), PerfilActivity.class);
                                intent.putExtras(b);
                                startActivity(intent);

                            } else {
                                //cuando status no es "success"
                                Toast.makeText(getActivity().getApplicationContext(), "Ha ocurrido un error en la petición", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        queue.add(jsonReque);
    }
}
