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

import com.example.linkder.models.Usuario;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RegistroActivity extends AppCompatActivity {
    private ImageView imgBack;
    private EditText editNombre, editMail, editPass, editDescripcion, editContacto;
    private Button btnReg;
    private Realm mRealm;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Realm
        setUpRealmConfig();
        mRealm = Realm.getDefaultInstance();

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
                        if(yaEstaRegistrado()) {
                            Toast.makeText(getApplicationContext(),"Ya hay alguien registrado con ese e-mail y/o nickname",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(),"Éxito al registrar", Toast.LENGTH_SHORT).show();
                            guardaInfoReg();
                            registraNuevoUsuario();

                            //Una vez listo el registro se deriva al splash
                            Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                            startActivity(intent);
                            //Se mata esta actividad
                            finish();
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

    //valida si ya existe un usuario registrado
    private boolean yaEstaRegistrado() {
        Usuario u = mRealm.where(Usuario.class).equalTo("mail", editMail.getText().toString())
                .or().equalTo("nick", editNombre.getText().toString())
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

    //Guarda en las sharedPreference que el usuario se registro
    private void guardaInfoReg() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("mail", editMail.getText().toString());
        editor.putString("pass", editPass.getText().toString());
        editor.putBoolean("logueado", true);
        editor.apply();
    }

    //Guarda en Realm el registro de usuario
    private void registraNuevoUsuario() {
        Date cal = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaRegistro = sdf.format(cal.getTime());

        Usuario nuevo_usuario = new Usuario(editMail.getText().toString()
                , editNombre.getText().toString()
                , editPass.getText().toString()
                , editDescripcion.getText().toString()
                , editContacto.getText().toString()
                , fechaRegistro);
        mRealm.beginTransaction();
        mRealm.insertOrUpdate(nuevo_usuario);
        mRealm.commitTransaction();
    }
}
