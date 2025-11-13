import java.util.ArrayList;

import Enum.TipoUsuario;
import org.json.JSONObject;

public class Administrador extends Usuario {

    public Administrador(String nombre, String apellido, int numeroCell, int dni, int direccion, String mail, String username, String password, boolean activo) {
        super(nombre, apellido, numeroCell, dni, direccion, mail, username, password, TipoUsuario.ADMINISTRADOR, activo);
    }

    public Administrador(JSONObject json) {
        super(json);
    }

}
