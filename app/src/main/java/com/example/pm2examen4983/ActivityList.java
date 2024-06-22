package com.example.pm2examen4983;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    ArrayAdapter<String> adapter;
    SearchView buscar;


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
        buscar = (SearchView) findViewById(R.id.searchView);
        obtenerInfo();

        ArrayAdapter<String> adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Arreglo);
        contactosList.setAdapter(adp);

       buscar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText){
                adp.getFilter().filter(newText);
                return true;
            }
        });

        contactosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contactos contactoSeleccionado = lista.get(position);
                //Toast.makeText(getApplicationContext(), elementoSeleccionado, Toast.LENGTH_SHORT).show();
                msjConfirmacion(contactoSeleccionado);
            }
        });

        Button btnBack = findViewById(R.id.btnAtras);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    private String obtenerCodigoArea(String pais) {
        switch (pais) {
            case "Honduras":
                return "+504";
            case "Guatemala":
                return "+502";
            case "El Salvador":
                return "+503";
            case "Costa Rica":
                return "+506";
            case "Nicaragua":
                return "+505";
            default:
                return "";
        }
    }


    private void FillDate() {
        Arreglo = new ArrayList<String>();
        for(int i = 0; i < lista.size(); i++) {
            Contactos contacto = lista.get(i);
            String codigoArea = obtenerCodigoArea(contacto.getPais());
            Arreglo.add(contacto.getNombres() + " | " + codigoArea + contacto.getTelefono());
        }
    }


    private void msjConfirmacion(Contactos contacto){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Acción");
        builder.setMessage("¿Desea llamar a " + contacto.getNombres() + "?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                realizarLlamada(contacto);

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void realizarLlamada(Contactos contacto){

        String codigoArea = obtenerCodigoArea(contacto.getPais());
        String numeroTelefono = codigoArea + contacto.getTelefono();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + numeroTelefono));
        startActivity(intent);
    }
}