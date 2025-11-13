import java.util.Objects;
import Enum.TipoUsuario;
import Interfaz.Autentificable;
import org.json.JSONObject;

public abstract class Usuario extends Persona implements Autentificable {
    private String username;
    private String password;
    private TipoUsuario tipoUsuario;
    private boolean activo;

    public Usuario(String nombre, String apellido, int numeroCell, int dni, int direccion, String mail, String username, String password, TipoUsuario tipoUsuario, boolean activo) {
        super(nombre, apellido, numeroCell, dni, direccion, mail);
        this.username = username;
        this.password = password;
        this.tipoUsuario = tipoUsuario;
        this.activo = activo;
    }

    public Usuario(JSONObject json) {
        super(json);
        this.username = json.getString("username");
        this.password = json.getString("password");
        this.tipoUsuario = TipoUsuario.valueOf(json.getString("tipoUsuario"));
        this.activo = json.getBoolean("activo");
    }

    @Override
    public boolean autenticar(String username, String password) {
        return this.activo && this.username.equals(username) && this.password.equals(password);
    }

    @Override
    public boolean validarSesion() {
        return this.activo;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("username", this.username);
        json.put("password", this.password);
        json.put("tipoUsuario", this.tipoUsuario.name());
        json.put("activo", this.activo);
        return json;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(username, usuario.username) && Objects.equals(password, usuario.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, password);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", tipoUsuario=" + tipoUsuario +
                ", activo=" + activo +
                '}';
    }



}
