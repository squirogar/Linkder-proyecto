package com.example.linkder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

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

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class LoginActivity extends AppCompatActivity {

    private TextView txtRegistrate;
    private SharedPreferences prefs;
    private Realm mRealm;
    private EditText editMail, editPass;
    private Button btnIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //realm
        setUpRealmConfig();
        mRealm = Realm.getDefaultInstance();

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
                        if(validaUsuario()) {
                            Toast.makeText(getApplicationContext(),"Usuario validado", Toast.LENGTH_SHORT).show();
                            guardaInfoLogin();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),"e-mail y/o contraseña incorrectos", Toast.LENGTH_SHORT).show();
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
    private boolean validaUsuario() {
        Usuario u = mRealm.where(Usuario.class).equalTo("mail", editMail.getText().toString())
                .equalTo("password", editPass.getText().toString())
                .findFirst();

        if(u != null) {
            return true;
        }
        return false;
    }

    //Establece la configuracion de Realm
    private void setUpRealmConfig() {
        // Se inicializa realm
        Realm.init(this.getApplicationContext());

        // Configuración por defecto en realm
        RealmConfiguration config = new RealmConfiguration.
                Builder().
                deleteRealmIfMigrationNeeded().
                build();
        Realm.setDefaultConfiguration(config);

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
