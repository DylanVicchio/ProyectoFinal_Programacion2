package Exception;

public class HabitacionNoDisponibleException extends Exception {

    public HabitacionNoDisponibleException(String mensaje) {
        super(mensaje);
    }
    @Override
    public String getMessage() {
        return "Habitacion no disponible: " + super.getMessage();
    }
}