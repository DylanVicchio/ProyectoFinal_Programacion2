import java.util.ArrayList;
import Enum.TipoUsuario;
import org.json.JSONObject;

public class Administrador extends Usuario {

    ArrayList<Reporte> reportes;

    public Administrador(String nombre, String apellido, int numeroCell, int dni, int direccion, String mail, String username, String password, boolean activo) {
        super(nombre, apellido, numeroCell, dni, direccion, mail, username, password, TipoUsuario.ADMINISTRADOR, activo);
        this.reportes = new ArrayList<>();
    }

    public Administrador(JSONObject json) {
        super(json);
        this.reportes = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Administrador{ "+super.toString()+"}";
    }
}
