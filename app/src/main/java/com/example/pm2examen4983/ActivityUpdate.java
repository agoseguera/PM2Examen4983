package com.example.pm2examen4983;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;

import Configuracion.SQLiteConexion;
import Configuracion.Trans;

public class ActivityUpdate extends AppCompatActivity {

    Spinner pais;
    EditText nombre, telefono, nota;
    Button btnActualizar;
    int contactoId;

    SQLiteConexion conexion;
    ImageView imageView;
    static final int peticion_acceso_camara = 101;
    static final int peticion_captura_imagen = 102;

    private Bitmap fotoBitmap;
    ImageView ObjectoImagen;
    Button btncaptura;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);

        ObjectoImagen = (ImageView) findViewById(R.id.imageView);
        imageView = findViewById(R.id.imageView);
        conexion = new SQLiteConexion(this, Trans.DBname, null, Trans.Version);

        Intent intent1 = getIntent();
        int contactfoto = intent1.getIntExtra("contactfoto", -1);

        if (contactfoto != -1) {
            cargarImagen(contactfoto);
        }

        btncaptura = (Button) findViewById(R.id.btnFoto);
        btncaptura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Permisos();
            }
        });
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

            if (fotoBitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                fotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] fotoBytes = baos.toByteArray();
                valores.put(Trans.foto, fotoBytes);
            }

            String[] params = {String.valueOf(contactoId)};
            db.update(Trans.TableContactos, valores, Trans.id + "=?", params);

            //Toast.makeText(this, "Contacto actualizado con éxito", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Registro Actualizado");
            builder.setMessage("REGISTRO ACTUALIZADO CON EXITO ");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            db.close();
        } catch (Exception ex) {
            Toast.makeText(this, "Error al actualizar el contacto", Toast.LENGTH_LONG).show();
        }
    }
    private void cargarImagen(int contactfoto) {
        SQLiteDatabase db = conexion.getReadableDatabase();
        String[] args = new String[]{String.valueOf(contactfoto)};
        Cursor cursor = db.rawQuery("SELECT foto FROM " + Trans.TableContactos + " WHERE id = ?", args);

        if (cursor.moveToFirst()) {
            byte[] imagenBytes = cursor.getBlob(0);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
            imageView.setImageBitmap(bitmap);
        }
        cursor.close();
    }

    private void Permisos()
    {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String [] {Manifest.permission.CAMERA},
                    peticion_acceso_camara);
        }
        else
        {
            TomarFoto();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == peticion_acceso_camara)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                TomarFoto();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Acceso Denegado", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void TomarFoto()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager())!= null)
        {
            startActivityForResult(intent,  peticion_captura_imagen);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==  peticion_captura_imagen && resultCode == RESULT_OK)
        {
            if(data != null){
                Bundle extras = data.getExtras();
                if(extras != null){
                    Bitmap imagen = (Bitmap) extras.get("data");
                    ObjectoImagen.setImageBitmap(imagen);
                    fotoBitmap = imagen;

                }
            }
        }
    }
}