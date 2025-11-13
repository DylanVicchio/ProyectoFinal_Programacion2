package Exception;

public class SeguridadException extends Exception {

    public SeguridadException(String mensaje) {
        super(mensaje);
    }

    @Override
    public String getMessage() {
        return "Acceso Denegado: " + super.getMessage();
    }
}
