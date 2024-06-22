package Configuracion;

public class Contactos {
    private Integer id;
    private String pais;
    private String nombres;
    private Integer telefono;
    private String nota;
    private String foto;

    public Contactos(Integer id, String pais, String nombres, Integer telefono, String nota) {
        this.id = id;
        this.pais = pais;
        this.nombres = nombres;
        this.telefono = telefono;
        this.nota = nota;
    }

    public Contactos() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombre) {
        this.nombres = nombre;
    }

    public Integer getTelefono() {
        return telefono;
    }

    public void setTelefono(Integer telefono) {
        this.telefono = telefono;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public byte[] getFoto() {
        return foto.getBytes();
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
