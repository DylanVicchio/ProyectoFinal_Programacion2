import java.util.ArrayList;

public class Administrador extends Usuario {

    ArrayList<Reporte> reportes;

    public Administrador(int id, String nombre, String apellido, int numeroCell, int dni, int direccion, String mail, String username, String password, TipoUsuario tipoUsuario, boolean activo) {
        super(id, nombre, apellido, numeroCell, dni, direccion, mail, username, password, tipoUsuario, activo);
    }


    @Override
    public String toString() {
        return "Administrador{ }"+super.toString()+"}";
    }
}
