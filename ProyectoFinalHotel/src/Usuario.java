import java.util.Objects;

public abstract class Usuario extends Persona {
    private String username;
    private String password;
    private TipoUsuario tipoUsuario;
    private boolean activo;

    public Usuario(int id, String nombre, String apellido, int numeroCell, int dni, int direccion, String mail, String username, String password, TipoUsuario tipoUsuario, boolean activo) {
        super(id, nombre, apellido, numeroCell, dni, direccion, mail);
        this.username = username;
        this.password = password;
        this.tipoUsuario = tipoUsuario;
        this.activo = activo;
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
