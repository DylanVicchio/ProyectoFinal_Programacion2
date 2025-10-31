import java.time.LocalDateTime;

public class Reserva {

    private LocalDateTime diaReserva;
    private LocalDateTime diaEntrada;
    private LocalDateTime diaSalida;

    public Reserva(LocalDateTime diaReserva, LocalDateTime diaEntrada, LocalDateTime diaSalida) {
        this.diaReserva = diaReserva;
        this.diaEntrada = diaEntrada;
        this.diaSalida = diaSalida;
    }

    public LocalDateTime getDiaReserva() {
        return diaReserva;
    }

    public void setDiaReserva(LocalDateTime diaReserva) {
        this.diaReserva = diaReserva;
    }

    public LocalDateTime getDiaEntrada() {
        return diaEntrada;
    }

    public void setDiaEntrada(LocalDateTime diaEntrada) {
        this.diaEntrada = diaEntrada;
    }

    public LocalDateTime getDiaSalida() {
        return diaSalida;
    }

    public void setDiaSalida(LocalDateTime diaSalida) {
        this.diaSalida = diaSalida;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "dia Reserva=" + diaReserva +
                ", Entrada=" + diaEntrada +
                ", Salida=" + diaSalida +
                '}';
    }
}
