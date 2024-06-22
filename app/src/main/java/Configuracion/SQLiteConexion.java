package Configuracion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;

public class SQLiteConexion extends SQLiteOpenHelper {


    public SQLiteConexion(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // CREACION DE TODAS LAS TABLAS EN SQLITE
        db.execSQL(Trans.CreateTableContactos);
        Log.d("SQLiteConexion", "Tabla contactos creada");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Trans.DropTableContactos);
        onCreate(db);
    }
}
