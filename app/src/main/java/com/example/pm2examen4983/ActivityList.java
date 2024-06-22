package com.example.pm2examen4983;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import Configuracion.Contactos;
import Configuracion.SQLiteConexion;
import Configuracion.Trans;

public class ActivityList extends AppCompatActivity {

    SQLiteConexion conexion;
    ListView contactosList;
    ArrayList<Contactos> lista;
    ArrayList<String> Arreglo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        conexion = new SQLiteConexion(this, Trans.DBname, null, Trans.Version);
        contactosList = (ListView) findViewById(R.id.listaContactos);

        obtenerInfo();

        ArrayAdapter<String> adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Arreglo);
        contactosList.setAdapter(adp);

        contactosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String elementoSeleccionado = (String) parent.getItemAtPosition(position);

                Toast.makeText(getApplicationContext(), elementoSeleccionado, Toast.LENGTH_SHORT).show();
            }
        });

        Button btnBack = findViewById(R.id.btnAtras);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para abrir SecondActivity
                Intent intent = new Intent(ActivityList.this, ActivityInit.class);
                startActivity(intent);
            }
        });
    }

    private void obtenerInfo(){
        SQLiteDatabase db = conexion.getReadableDatabase();
        Contactos contacto = null;
        lista = new ArrayList<Contactos>();

        //cursor para recorrer los datos de la tabla
        Cursor cursor = db.rawQuery(Trans.SelectAllContactos, null);

        while(cursor.moveToNext())
        {
            contacto = new Contactos();
            contacto.setId(cursor.getInt(0));
            contacto.setPais(cursor.getString(1));
            contacto.setNombres(cursor.getString(2));
            contacto.setTelefono(cursor.getInt(3));
            contacto.setNota(cursor.getString(4));


            lista.add(contacto);
        }
        cursor.close();
        FillDate();


    }
    private void FillDate()
    {
        Arreglo = new ArrayList<String>();
        for(int i=0; i < lista.size(); i++)
        {
            Arreglo.add(lista.get(i).getNombres() + " | " +
                    lista.get(i).getTelefono());

        }
    }
}