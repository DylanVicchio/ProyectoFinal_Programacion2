package Exception;

public class ReservaInvalidaException extends Exception {

    public ReservaInvalidaException(String mensaje) {
        super(mensaje);
    }

    @Override
    public String getMessage() {
        return "Reserva invalida: " + super.getMessage();
    }
}