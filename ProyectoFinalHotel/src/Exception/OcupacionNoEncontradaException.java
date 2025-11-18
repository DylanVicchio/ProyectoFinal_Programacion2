package Exception;

public class OcupacionNoEncontradaException extends RuntimeException {
    public OcupacionNoEncontradaException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "Ocupacion no encontradad: " + super.getMessage();
    }
}
