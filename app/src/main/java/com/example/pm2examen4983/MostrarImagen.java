package com.example.pm2examen4983;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import Configuracion.SQLiteConexion;
import Configuracion.Trans;

public class MostrarImagen extends AppCompatActivity {
    SQLiteConexion conexion;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mostrar_imagen);

        imageView = findViewById(R.id.imageView);
        conexion = new SQLiteConexion(this, Trans.DBname, null, Trans.Version);

        Intent intent = getIntent();
        int contactfoto = intent.getIntExtra("contactfoto", -1);

        if (contactfoto != -1) {
            cargarImagen(contactfoto);
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
}
