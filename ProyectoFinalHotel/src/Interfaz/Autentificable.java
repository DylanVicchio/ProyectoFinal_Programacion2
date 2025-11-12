package Interfaz;

public interface Autentificable {

    // autentica un usuario con username y password
    boolean autenticar(String username, String password);

    // valida si la sesion del usuario sigue activa
    boolean validarSesion();
}
