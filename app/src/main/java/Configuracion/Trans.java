package Configuracion;

public class Trans {
    //VERSION DE LA BD
    public static final int Version = 1;

    // NOMBRE DE LA BD
    public static final String DBname = "PM1_2024";

    // TABLA CONTACTOS
    public static final String TableContactos = "contactos";

    // PROPIEDADES
    public static final String id = "id";
    public static final String pais = "pais";
    public static final String nombres = "nombres";
    public static final String telefono = "telefono";
    public static final String nota = "nota";
    public static final String foto = "foto";

    public static final String CreateTableContactos = "CREATE TABLE " + TableContactos + " ( " +
            id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            pais + " TEXT, " +
            nombres + " TEXT, " +
            telefono + " INTEGER, " +
            nota + " TEXT)";


    public static final String SelectAllContactos = "SELECT * FROM " + TableContactos;

    public static final String DropTableContactos = "DROP TABLE IF EXISTS " + TableContactos;

}
