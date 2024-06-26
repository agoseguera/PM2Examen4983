package com.example.pm2examen4983;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    private int contactop = -1;
    Contactos contactoSeleccionado = null;

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

                contactop = position;
                String elementoSeleccionado = (String) parent.getItemAtPosition(position);

                Toast.makeText(getApplicationContext(), "Ha seleccionado un contacto", Toast.LENGTH_SHORT).show();
                Contactos contactoSeleccionado = lista.get(position);
            }
        });
        contactosList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                contactoSeleccionado = lista.get(position);
                msjConfirmacionLlamada(contactoSeleccionado);
                return true;
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
        Button btnVerImagen = findViewById(R.id.btnVerImagen);
        btnVerImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactop != -1) {
                    Contactos contactos = lista.get(contactop);
                    Intent intent = new Intent(ActivityList.this, MostrarImagen.class);
                    intent.putExtra("contactfoto", contactos.getId());
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(ActivityList.this)
                            .setTitle("Seleccione un contacto")
                            .setMessage("Debe seleccionar un contacto para ver su imagen.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
        });

        Button btnCompartir = findViewById(R.id.btnCompartir);
        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactop != -1) {
                    Contactos contactos= lista.get(contactop);
                    compartir(contactos);
                } else {
                    new AlertDialog.Builder(ActivityList.this)
                            .setTitle("Seleccione un contacto")
                            .setMessage("Debe seleccionar un contacto para compartir.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
        });

        Button btnEliminar = findViewById(R.id.btnEliminar);
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactop != -1) {
                        Contactos contactos= lista.get(contactop);
                        eliminarContacto(contactos);
                } else {
                    new AlertDialog.Builder(ActivityList.this)
                            .setTitle("Seleccione un contacto")
                            .setMessage("Debe seleccionar un contacto para eliminar.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
        });

        Button btnActualizar = findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                    if (contactop != -1) {
                        Contactos contactos= lista.get(contactop);
                        iniciarActualizacionContacto(contactos);
                } else {
                        new AlertDialog.Builder(ActivityList.this)
                                .setTitle("Seleccione un contacto")
                                .setMessage("Debe seleccionar un contacto para actualizar.")
                                .setPositiveButton("OK", null)
                                .show();
                }
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
                return ""; // Devuelve una cadena vacía si el país no está en la lista
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
    private void compartir(Contactos contacto) {
        String codigoArea = obtenerCodigoArea(contacto.getPais());
        String info = "Nombre: " + contacto.getNombres() + "\n" +
                "Teléfono: " + codigoArea + contacto.getTelefono() + "\n" +
                "País: " + contacto.getPais() + "\n" +
                "Nota: " + contacto.getNota();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TITLE, "INFORMACIÓN DE CONTACTO");
        intent.putExtra(Intent.EXTRA_TEXT, info);
        intent.setType("text/plain");
        Intent share = Intent.createChooser(intent, null);
        startActivity(share);
    }
    private void msjConfirmacionLlamada(Contactos contacto){
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, 1);
        } else {
            startActivity(intent);
        }
    }

    private void eliminarContacto(Contactos contacto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar eliminación");
        builder.setMessage("¿Está seguro de que desea eliminar el contacto " + contacto.getNombres() + "?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLiteDatabase db = conexion.getWritableDatabase();
                String[] params = {String.valueOf(contacto.getId())};
                db.delete(Trans.TableContactos, Trans.id + "=?", params);

                obtenerInfo();
                ArrayAdapter<String> adp = new ArrayAdapter<>(ActivityList.this, android.R.layout.simple_list_item_1, Arreglo);
                contactosList.setAdapter(adp);
                contactoSeleccionado = null;

                AlertDialog.Builder successBuilder = new AlertDialog.Builder(ActivityList.this);
                successBuilder.setTitle("Registro Eliminado");
                successBuilder.setMessage("REGISTRO ELIMINADO CON EXITO ");
                successBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                successBuilder.show();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void iniciarActualizacionContacto(Contactos contacto) {
        Intent intent = new Intent(ActivityList.this, ActivityUpdate.class);
        intent.putExtra("contactoId", contacto.getId());
        intent.putExtra("nombre", contacto.getNombres());
        intent.putExtra("telefono", String.valueOf(contacto.getTelefono()));
        intent.putExtra("nota", contacto.getNota());
        intent.putExtra("pais", contacto.getPais());
        intent.putExtra("contactfoto", contacto.getId());

        startActivity(intent);
    }



}
