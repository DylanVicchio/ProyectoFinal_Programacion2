import java.util.Objects;
import Interfaz.Guardable;
import org.json.JSONObject;

public abstract class Persona implements Guardable {

    private int id;
    private static int contador = 0;
    private String nombre;
    private String apellido;
    private int numeroCell;
    private int dni;
    private int direccion;
    private String mail;

    public Persona(String nombre, String apellido, int numeroCell, int dni, int direccion, String mail) {
        contador++;
        this.id = contador;
        this.nombre = nombre;
        this.apellido = apellido;
        this.numeroCell = numeroCell;
        this.dni = dni;
        this.direccion = direccion;
        this.mail = mail;
    }

    public Persona(JSONObject json) {
        this.id = json.getInt("id");
        this.nombre = json.getString("nombre");
        this.apellido = json.getString("apellido");
        this.numeroCell = json.getInt("numeroCell");
        this.dni = json.getInt("dni");
        this.direccion = json.getInt("direccion");
        this.mail = json.getString("mail");
        if (this.id >= contador) {
            contador = this.id + 1;
        }
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
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("nombre", this.nombre);
        json.put("apellido", this.apellido);
        json.put("numeroCell", this.numeroCell);
        json.put("dni", this.dni);
        json.put("direccion", this.direccion);
        json.put("mail", this.mail);
        return json;
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
        return dni == persona.dni;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dni);
    }
}
