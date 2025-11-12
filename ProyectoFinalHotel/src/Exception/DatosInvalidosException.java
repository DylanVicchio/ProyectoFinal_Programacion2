package Exception;

public class DatosInvalidosException extends Exception {

    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }



    @Override
    public String getMessage() {
        return "Datos incorrectos: " + super.getMessage();
    }
}