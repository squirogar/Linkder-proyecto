package com.example.linkder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PerfilActivity extends AppCompatActivity {
    private TextView textNick, textMail, textDescripcion, textContacto;
    private Toolbar toolbar;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

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

        textNick = findViewById(R.id.textNick);
        textMail = findViewById(R.id.textMail);
        textDescripcion = findViewById(R.id.textDescripcion);
        textContacto = findViewById(R.id.textContacto);

        //Obtenemos el bundle enviado desde la actividad
        Bundle b = getIntent().getExtras();

        //ponemos la informacion en los textwievs correpondientes
        textNick.setText(b.getString("nick"));
        textMail.setText(b.getString("email"));
        textDescripcion.setText(b.getString("descripcion"));
        textContacto.setText(b.getString("contactos"));
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
        Intent intent = new Intent(PerfilActivity.this,MainActivity.class);
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
