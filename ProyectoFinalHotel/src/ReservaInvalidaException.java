public class ReservaInvalidaException extends Exception {

    private String motivo;
    private Reserva reserva;

    public ReservaInvalidaException(String mensaje) {
        super(mensaje);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}