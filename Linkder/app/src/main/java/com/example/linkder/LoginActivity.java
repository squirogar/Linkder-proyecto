package com.example.linkder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.linkder.models.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class LoginActivity extends AppCompatActivity {

    private TextView txtRegistrate;
    private SharedPreferences prefs;
    private EditText editMail, editPass;
    private Button btnIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //shared preferences
        prefs = getSharedPreferences("Preference", Context.MODE_PRIVATE);

        //login
        editMail = findViewById(R.id.editMail);
        editPass = findViewById(R.id.editPass);
        btnIngresar = findViewById(R.id.btnIngresar);
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validaEntradaNula()) {
                    if(Utilidades.isMailValid(editMail.getText().toString())) {
                        if(Utilidades.verificaConexion(getApplication())) {
                            //si hay conexion a internet
                            validaUsuario();
                        } else {
                            Toast.makeText(getApplicationContext(),"Esta acción requiere conexión a internet", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),"e-mail ingresado no valido", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        //registro
        txtRegistrate = findViewById(R.id.txtRegistrate);
        txtRegistrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    //Valida que los campos ingresados no sean nulos
    private boolean validaEntradaNula() {
        boolean flag = false;
        if(editMail.getText().toString().isEmpty()) {
            editMail.setError("El e-mail es obligatorio");
        } else if(editPass.getText().toString().isEmpty()) {
            editPass.setError("La contraseña es obligatoria");
        } else {
            flag = true;
        }
        return flag;
    }

    //Comprueba si el usuario existe
    private void validaUsuario() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", editMail.getText().toString());

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = "http://abascur.cl/android/misnotasapp/GetUsuario"; //cambiar!

        JsonObjectRequest jsonReque = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("success")) {
                                //peticion exitosa pero puede haber o no dato
                                Object mensaje = response.get("mensaje");
                                if(mensaje instanceof JSONObject){
                                    if(((JSONObject) mensaje).get("clave").equals(editPass.getText().toString())) {
                                        //si las password es la correcta
                                        guardaInfoLogin();
                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Contraseña equivocada. Intente nuevamente", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    //Retorna noData
                                    Toast.makeText(getApplicationContext(),"No hay nadie registrado con ese correo", Toast.LENGTH_LONG).show();
                                }

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



    //Guarda en las SharedPreferences que el usuario se logueo
    private void guardaInfoLogin() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("mail", editMail.getText().toString());
        editor.putString("pass", editPass.getText().toString());
        editor.putBoolean("logueado", true);
        editor.apply();
    }


}
