public class Recepcionista extends Usuario {

    private Pasajero cliente;

    public Recepcionista(String nombre, String apellido, int numeroCell, int dni, int direccion, String mail, String username, String password, boolean activo) {
        super(nombre, apellido, numeroCell, dni, direccion, mail, username, password, TipoUsuario.RECEPCIONISTA, activo);
    }



}
