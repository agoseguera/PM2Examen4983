package com.example.pm2examen4983;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import Configuracion.SQLiteConexion;
import Configuracion.Trans;

public class ActivityUpdate extends AppCompatActivity {

    Spinner pais;
    EditText nombre, telefono, nota;
    Button btnActualizar;
    int contactoId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);

        Button btnBack = findViewById(R.id.btnAtras);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityUpdate.this, ActivityList.class);
                startActivity(intent);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nombre = findViewById(R.id.nombre);
        pais = findViewById(R.id.pais);
        telefono = findViewById(R.id.telefono);
        nota = findViewById(R.id.nota);
        btnActualizar = findViewById(R.id.btnActualizar);

        FillData();

        // Recibir datos del Intent
        Intent intent = getIntent();
        contactoId = intent.getIntExtra("contactoId", -1);
        nombre.setText(intent.getStringExtra("nombre"));
        telefono.setText(intent.getStringExtra("telefono"));
        nota.setText(intent.getStringExtra("nota"));
        String paisSeleccionado = intent.getStringExtra("pais");

        // Seleccionar el país en el Spinner
        if (paisSeleccionado != null) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) pais.getAdapter();
            int position = adapter.getPosition(paisSeleccionado);
            pais.setSelection(position);
        }

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarContacto();
            }
        });
    }

    private void FillData() {
        String[] paises = {"Seleccione un país:", "Honduras", "Guatemala", "El Salvador", "Nicaragua", "Costa Rica"};
        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paises);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_item);
        pais.setAdapter(adp);
    }

    private void actualizarContacto() {
        String ingresoNombre = nombre.getText().toString().trim();
        String ingresoTel = telefono.getText().toString().trim();
        String ingresoNota = nota.getText().toString().trim();
        String seleccionPais = pais.getSelectedItem().toString();

        if (TextUtils.isEmpty(ingresoNombre) || TextUtils.isEmpty(ingresoTel) || TextUtils.isEmpty(ingresoNota) || seleccionPais.equals("Seleccione un país:")) {
            Toast.makeText(this, "Por favor complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SQLiteConexion conexion = new SQLiteConexion(this, Trans.DBname, null, Trans.Version);
            SQLiteDatabase db = conexion.getWritableDatabase();

            ContentValues valores = new ContentValues();
            valores.put(Trans.pais, seleccionPais);
            valores.put(Trans.nombres, ingresoNombre);
            valores.put(Trans.telefono, ingresoTel);
            valores.put(Trans.nota, ingresoNota);

            String[] params = {String.valueOf(contactoId)};
            db.update(Trans.TableContactos, valores, Trans.id + "=?", params);

            Toast.makeText(this, "Contacto actualizado con éxito", Toast.LENGTH_SHORT).show();

            db.close();
        } catch (Exception ex) {
            Toast.makeText(this, "Error al actualizar el contacto", Toast.LENGTH_LONG).show();
        }
    }
}