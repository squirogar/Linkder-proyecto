package com.example.linkder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.linkder.models.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RegistroActivity extends AppCompatActivity {
    private ImageView imgBack;
    private EditText editNombre, editMail, editPass, editDescripcion, editContacto;
    private Button btnReg;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //shared preferences
        prefs = getSharedPreferences("Preference", Context.MODE_PRIVATE);

        //volver a login
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //registro
        editNombre = findViewById(R.id.editNombre);
        editMail = findViewById(R.id.editMail);
        editPass = findViewById(R.id.editPass);
        editDescripcion = findViewById(R.id.editDescripcion);
        editContacto = findViewById(R.id.editContacto);
        btnReg = findViewById(R.id.btnReg);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validaEntradaNula()) {
                    if(Utilidades.isMailValid(editMail.getText().toString())) {
                        if(Utilidades.verificaConexion(getApplication())) {
                            validaUsuarioNuevo();
                        } else {
                            Toast.makeText(getApplicationContext(), "No hay conexión de internet, no se puede registrar", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),"e-mail ingresado no valido",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }


    //Valida que los campos ingresados no sean nulos
    private boolean validaEntradaNula() {
        boolean flag = false;
        if(editMail.getText().toString().isEmpty()) {
            editMail.setError("El e-mail es obligatorio");
        } else if(editNombre.getText().toString().isEmpty()) {
            editNombre.setError("El nombre es obligatorio");
        } else if(editPass.getText().toString().isEmpty()) {
            editPass.setError("La contraseña es obligatoria");
        } else if(editDescripcion.getText().toString().isEmpty()) {
            editDescripcion.setError("La descripción es obligatoria");
        } else if(editContacto.getText().toString().isEmpty()) {
            editContacto.setError("La información de contacto es obligatoria");
        } else {
            flag = true;
        }
        return flag;
    }

    //Valida si ya hay alguien registrado con ese rut, sino procede a registrar
    private void validaUsuarioNuevo() {
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
                                    Toast.makeText(getApplicationContext(),"Ya existe alguien registrado con ese rut", Toast.LENGTH_LONG).show();
                                } else {
                                    registraNuevoUsuario();
                                }

                            } else {
                                //Error 003 - rut invalido
                                Toast.makeText(getApplicationContext(),"Hubo un error en la petición", Toast.LENGTH_LONG).show();
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


    //Registra un nuevo usuario en la api y manda al usuario a la main
    private void registraNuevoUsuario() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("nick", editNombre.getText().toString());
        params.put("email", editMail.getText().toString());
        params.put("clave", editPass.getText().toString());
        params.put("descripcion", editDescripcion.getText().toString());
        params.put("contactos", editContacto.getText().toString());


        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = "http://abascur.cl/android/misnotasapp/InsertOrUpdateUsuario"; //cambiar!

        JsonObjectRequest jsonReque = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                            if (status.equals("success")) {
                                Toast.makeText(getApplicationContext(),"Exito al registrar",
                                        Toast.LENGTH_SHORT).show();
                                guardaInfoReg();
                                //Una vez listo el registro se deriva al splash
                                Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                                startActivity(intent);
                                //Se mata esta actividad
                                finish();
                            } else {
                                //error 000 - no se agrego json
                                Toast.makeText(getApplicationContext(),"Hubo un error en la petición", Toast.LENGTH_SHORT).show();
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



    //Guarda en las sharedPreference que el usuario se registro
    private void guardaInfoReg() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("mail", editMail.getText().toString());
        editor.putString("pass", editPass.getText().toString());
        editor.putBoolean("logueado", true);
        editor.apply();
    }



}
