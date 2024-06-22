package com.example.pm2examen4983;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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
    ImageView ObjectoImagen;
    Button btncaptura;
    String PathImagen;
    String currentPhotoPath;

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

        ObjectoImagen = (ImageView) findViewById(R.id.fotoView);
        btncaptura = (Button) findViewById(R.id.btnFoto);

        btncaptura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Permisos();
            }
        });


        nombre = (EditText) findViewById(R.id.nombre);
        pais = (Spinner) findViewById(R.id.pais);
        FillData();
        telefono = (EditText)findViewById(R.id.telefono);
        nota = (EditText)findViewById(R.id.nota);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);


        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Guardar();
            }
        });

        Button btnOpenList = findViewById(R.id.btnContactosG);
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

            Long resultado = db.insert(Trans.TableContactos, Trans.id, valores);

            //Toast.makeText(getApplicationContext(), "REGISTRO INGRESADO CON EXITO" + resultado.toString(), Toast.LENGTH_LONG).show();

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

    private void FillData() {
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
                    String image64 = ConvertImageBase64(imagen);
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.pmo120232p.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, peticion_captura_imagen);
            }
        }
    }

    private String ConvertImageBase64(Bitmap bitmap){
        ByteArrayOutputStream byteImage = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteImage);

        byte[] byteArray = byteImage.toByteArray();
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
    }
}