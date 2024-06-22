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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Configuracion.SQLiteConexion;
import Configuracion.Trans;

public class ActivityInit extends AppCompatActivity {
    Spinner pais;
    EditText nombre,  telefono, nota, foto;
    Button btnGuardar;

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
        setContentView(R.layout.activity_init);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        nombre = (EditText) findViewById(R.id.nombre);
        pais = (Spinner) findViewById(R.id.pais);

        telefono = (EditText)findViewById(R.id.telefono);
        nota = (EditText)findViewById(R.id.nota);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        ObjectoImagen = (ImageView) findViewById(R.id.fotoView);
        btncaptura = (Button) findViewById(R.id.btnFoto);
        Button btnOpenList = findViewById(R.id.btnContactosG);

        Paises();

        btncaptura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Permisos();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Guardar();
            }
        });

        btnOpenList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para abrir el otro Activity
                Intent intent = new Intent(ActivityInit.this, ActivityList.class);
                startActivity(intent);
            }
        });
    }

    private void Guardar(){
        String ingresoNombre = nombre.getText().toString().trim();
        String ingresoTel = telefono.getText().toString().trim();
        String ingresoNota = nota.getText().toString().trim();
        String seleccionPais = pais.getSelectedItem().toString();

        if (TextUtils.isEmpty(ingresoNombre) || TextUtils.isEmpty(ingresoTel) || TextUtils.isEmpty(ingresoNota) || seleccionPais.equals("Seleccione un país:")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error de Validación");
            builder.setMessage("Por favor complete todos los campos.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        try {
            SQLiteConexion conexion = new SQLiteConexion(this, Trans.DBname, null, Trans.Version);
            SQLiteDatabase db = conexion.getWritableDatabase();

            ContentValues valores = new ContentValues();
            valores.put(Trans.pais, pais.getSelectedItem().toString());
            valores.put(Trans.nombres, nombre.getText().toString());
            valores.put(Trans.telefono, telefono.getText().toString());
            valores.put(Trans.nota, nota.getText().toString());

            if (fotoBitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                fotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] fotoBytes = baos.toByteArray();
                valores.put(Trans.foto, fotoBytes);
            }

            Long resultado = db.insert(Trans.TableContactos, Trans.id, valores);


            // Mostrar AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Registro Exitoso");
            builder.setMessage("REGISTRO INGRESADO CON EXITO ");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            db.close();
        }
        catch (Exception ex){
            Log.e("DB_ERROR", ex.toString());
            Toast.makeText(getApplicationContext(), "ERROR AL INGRESAR REGISTRO", Toast.LENGTH_LONG).show();

        }
    }

    private void Paises() {
        String[] paises = {"Seleccione un país:","Honduras", "Guatemala", "El Salvador", "Nicaragua", "Costa Rica"};

        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paises);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_item);
        pais.setAdapter(adp);
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