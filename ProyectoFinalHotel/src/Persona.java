import java.util.Objects;

public abstract class Persona {

    private int id;
    private String nombre;
    private String apellido;
    private int numeroCell;
    private int dni;
    private int direccion;
    private String mail;

    public Persona(int id, String nombre, String apellido, int numeroCell, int dni, int direccion, String mail) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.numeroCell = numeroCell;
        this.dni = dni;
        this.direccion = direccion;
        this.mail = mail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getNumeroCell() {
        return numeroCell;
    }

    public void setNumeroCell(int numeroCell) {
        this.numeroCell = numeroCell;
    }

    public int getDireccion() {
        return direccion;
    }

    public void setDireccion(int direccion) {
        this.direccion = direccion;
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String toString() {
        return "Persona{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", numeroCell=" + numeroCell +
                ", dni=" + dni +
                ", direccion=" + direccion +
                ", mail='" + mail + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Persona persona = (Persona) o;
        return id == persona.id && dni == persona.dni;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dni);
    }
}
