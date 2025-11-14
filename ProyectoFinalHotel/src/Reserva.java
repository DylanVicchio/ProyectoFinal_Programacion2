import Interfaz.Guardable;
import org.json.JSONObject;
import Enum.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import Exception.ReservaInvalidaException;

public class Reserva implements Guardable {


    private final int id;
    private static int contador = 1;
    private Pasajero pasajero;
    private final LocalDateTime diaCreacion;
    private LocalDate diaEntrada;
    private LocalDate diaSalida;
    private Habitacion habitacionReservada;
    private EstadoReserva estado;
    private double montoTotal;
    private final int dniPasajero_json;
    private final int idHabitacion_json;


    public Reserva(Pasajero pasajero, LocalDate diaEntrada, LocalDate diaSalida, Habitacion habitacion) {

        this.id = contador;
        contador++;

        this.diaCreacion = LocalDateTime.now();
        this.pasajero = pasajero;
        this.habitacionReservada = habitacion;
        this.estado = EstadoReserva.PENDIENTE;
        this.diaEntrada = diaEntrada;
        this.diaSalida = diaSalida;
        this.dniPasajero_json = pasajero.getDni();
        this.idHabitacion_json = habitacion.getId();

        calcularNuevoMonto();

    }

    public Reserva(JSONObject json) {
        this.id = json.getInt("id");
        this.diaEntrada = LocalDate.parse(json.getString("diaEntrada"));
        this.diaSalida = LocalDate.parse(json.getString("diaSalida"));
        this.diaCreacion = LocalDateTime.parse(json.getString("fechaCreacion"));
        this.estado = EstadoReserva.valueOf(json.getString("estado"));
        this.montoTotal = json.getDouble("montoTotal");

        this.dniPasajero_json = json.getInt("dniPasajero");
        this.idHabitacion_json = json.getInt("idHabitacion");

        this.pasajero = null;
        this.habitacionReservada = null;

        if (this.id >= contador) {
            contador = this.id + 1;
        }
    }

    public void reconectarObjetos(Pasajero p, Habitacion h) {

        this.pasajero = p;
        this.habitacionReservada = h;

    }

    public int getDniPasajero_json() {
        return dniPasajero_json;
    }

    public int getIdHabitacion_json() {
        return idHabitacion_json;
    }

    public int getId() {
        return this.id;
    }

    public Pasajero getPasajero() {
        return pasajero;
    }

    public void setPasajero(Pasajero pasajero) {
        this.pasajero = pasajero;
    }

    public LocalDateTime getDiaCreacion() {
        return diaCreacion;
    }

    public LocalDate getDiaEntrada() {
        return diaEntrada;
    }

    public void setDiaEntrada(LocalDate diaEntrada) {
        this.diaEntrada = diaEntrada;
        calcularNuevoMonto();
    }

    public LocalDate getDiaSalida() {
        return diaSalida;
    }

    public void setDiaSalida(LocalDate diaSalida) {
        this.diaSalida = diaSalida;
    }

    public Habitacion getHabitacionReservada() {
        return habitacionReservada;
    }

    public void setHabitacionReservada(Habitacion habitacionReservada) {
        this.habitacionReservada = habitacionReservada;
        calcularNuevoMonto();
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public long calcularNoches() {
        long noches = ChronoUnit.DAYS.between(this.diaEntrada, this.diaSalida);
        if (noches <= 0) {
            return 1;
        } else {
            return noches;
        }
    }

    public void calcularNuevoMonto() {

        this.montoTotal = habitacionReservada.calcularPrecio((int) calcularNoches());
    }


    public void confirmarReserva() throws ReservaInvalidaException {
        if (this.estado != EstadoReserva.PENDIENTE) {
            throw new ReservaInvalidaException("Solo se pueden confirmar reservas PENDIENTES.");
        }

        if (LocalDate.now().isAfter(diaEntrada)) {
            this.estado = EstadoReserva.CANCELADA;
            throw new ReservaInvalidaException("La fecha de inicio ya pasó. Reserva cancelada automáticamente.");
        }

        this.estado = EstadoReserva.CONFIRMADA;
    }

    public void cancelarReserva() {

        if (this.estado == EstadoReserva.COMPLETADA) {
            throw new IllegalStateException("No se puede cancelar una reserva COMPLETADA.");
        }
        this.estado = EstadoReserva.CANCELADA;

    }

    public void finalizarReserva() {

        if (this.estado == EstadoReserva.CONFIRMADA) {
            this.estado = EstadoReserva.COMPLETADA;
        } else {
            throw new IllegalStateException("Solo se puede finalizar una reserva CONFIRMADA.");
        }

    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", this.id);


        json.put("dniPasajero", this.dniPasajero_json);
        json.put("idHabitacion", this.idHabitacion_json);

        json.put("diaEntrada", this.diaEntrada.toString());
        json.put("diaSalida", this.diaSalida.toString());
        json.put("fechaCreacion", this.diaCreacion.toString());
        json.put("estado", this.estado.name());
        json.put("montoTotal", this.montoTotal);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Reserva reserva = (Reserva) o;
        return id == reserva.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        long noches = ChronoUnit.DAYS.between(diaEntrada, diaSalida);
        String pStr = (pasajero != null) ? pasajero.getNombre() : "DNI: " + dniPasajero_json;
        String hStr = (habitacionReservada != null) ? "" + habitacionReservada.getNumero() : "ID: " + idHabitacion_json;

        return "Reserva{" +
                "id=" + id +
                ", Pasajero=" + pStr +
                ", habitacion=" + hStr +
                ", noches=" + noches +
                ", estado=" + estado +
                ", montoTotal=$" + montoTotal +
                '}';
    }
}

