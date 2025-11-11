import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Ocupacion {

    private int id;
    private static int contador = 1;
    private Habitacion habitacion;
    private Pasajero pasajero;
    private Reserva reserva;
    private LocalDateTime fechaCheckIn;
    private LocalDateTime fechaCheckOut;
    private double montoPagado;
    private ArrayList<Consumo> consumos;

    public Ocupacion(Habitacion habitacion, Pasajero pasajero, Reserva reserva) {
        this.id = contador++;
        this.habitacion = habitacion;
        this.pasajero = pasajero;
        this.reserva = reserva;
        this.fechaCheckIn = LocalDateTime.now();
        this.fechaCheckOut = null;
        this.montoPagado = 0;
        this.consumos = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public Pasajero getPasajero() {
        return pasajero;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public LocalDateTime getFechaCheckIn() {
        return fechaCheckIn;
    }

    public LocalDateTime getFechaCheckOut() {
        return fechaCheckOut;
    }

    public double getMontoPagado() {
        return montoPagado;
    }

    public ArrayList<Consumo> getConsumos() {
        return new ArrayList<>(consumos);
    }


    public void setHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
    }

    public void setPasajero(Pasajero pasajero) {
        this.pasajero = pasajero;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public void setFechaCheckIn(LocalDateTime fechaCheckIn) {
        this.fechaCheckIn = fechaCheckIn;
    }

    public void setFechaCheckOut(LocalDateTime fechaCheckOut) {
        this.fechaCheckOut = fechaCheckOut;
    }

    public void setMontoPagado(double montoPagado) {
        this.montoPagado = montoPagado;
    }

    public void agregarConsumos(Consumo consumo) {
        if(fechaCheckOut != null){
            throw new IllegalArgumentException("Ocupacion finalizada, no es posible agregar consumos");
        }
        consumos.add(consumo);
    }

    public double calcularTotal(int noches){
        double totalHabitacion = habitacion.calcularPrecio(noches);
        double totalConsumo = 0;
        for(Consumo c : consumos){
            totalConsumo += c.getMonto();
        }
        return totalConsumo + totalHabitacion;
    }

    public void finalizarOcupacion(){
        if(fechaCheckOut != null){
            throw new IllegalArgumentException("Check out no valido, ya fue realizado");
        }
        this.fechaCheckOut = LocalDateTime.now();

        // faltan acciones con pasajero y actualizar la reserva
    }

    public boolean verificarActiva(){
        return fechaCheckOut == null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ocupacion ocupacion = (Ocupacion) o;
        return id == ocupacion.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Ocupacion{" +
                "id=" + id +
                ", habitacion=" + habitacion.toString() +
                ", pasajero=" + pasajero.toString() +
                ", reserva=" + reserva.toString() +
                ", fechaCheckIn=" + fechaCheckIn.toString() +
                ", fechaCheckOut=" + fechaCheckOut.toString() +
                ", montoPagado=" + montoPagado +
                ", consumos=" + consumos.toString() +
                '}';
    }
}
