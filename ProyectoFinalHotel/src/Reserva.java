import Interfaz.Guardable;
import org.json.JSONObject;
import Enum.EstadoReserva;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Reserva implements Guardable {


    private final int id;
    private static int contador = 1;
    private Pasajero pasajero;
    private LocalDateTime diaCreacion;
    private LocalDate diaEntrada;
    private LocalDate diaSalida;
    private Habitacion habitacionReservada;
    private EstadoReserva estado;
    private double montoTotal;


    public Reserva(LocalDate diaEntrada, LocalDate diaSalida, Habitacion habitacion, String nombre, String apellido, int numeroCell, int dni, int direccion, String mail, String origen, String domicilioOrigen, Ocupacion ocupacion) {
        this.diaCreacion = LocalDateTime.now();
        this.id = contador;
        contador++;
        this.pasajero = new Pasajero( nombre, apellido, numeroCell, dni, direccion, mail, origen, domicilioOrigen, ocupacion);
        this.habitacionReservada = habitacion;
        this.estado = EstadoReserva.PENDIENTE;
        this.diaEntrada = diaEntrada;
        this.diaSalida = diaSalida;
        calcularNuevoMonto();
    }

    public Reserva(JSONObject json) {
        this.id = json.getInt("id");
        this.pasajero = new Pasajero(json.getJSONObject("pasajero"));
        this.habitacionReservada = new Habitacion(json.getJSONObject("habitacion"));
        this.diaEntrada = LocalDate.parse(json.getString("diaEntrada"));
        this.diaSalida = LocalDate.parse(json.getString("diaSalida"));
        this.diaCreacion = LocalDateTime.parse(json.getString("fechaCreacion"));
        this.estado = EstadoReserva.valueOf(json.getString("estado"));
        this.montoTotal = json.getDouble("montoTotal");

        if (this.id >= contador) {
            contador = this.id + 1;
        }
    }

    public int getId() {return id;}

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

    public long calcularNoches() {
        long noches = ChronoUnit.DAYS.between(this.diaEntrada, this.diaSalida);
        if (noches <= 0) {
            return 1;
        }else {
            return noches;
        }
    }

    public void calcularNuevoMonto() {
        this.montoTotal = habitacionReservada.calcularPrecio((int) calcularNoches());
    }



    public EstadoReserva verificarEstadoReserva(){

        if (LocalDate.now().isBefore(this.diaEntrada) || this.estado == EstadoReserva.CONFIRMADA){

            return this.estado;

        }

        this.estado = EstadoReserva.CANCELADA;
        return this.estado;

        //comprueba el estado de la reserva y lo devuelve

    }

    public void confirmarReserva(){
        if (this.estado != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden confirmar reservas PENDIENTES.");
        }

        if (!LocalDate.now().isAfter(diaEntrada)){
            this.estado = EstadoReserva.CANCELADA;
            throw new IllegalStateException("La fecha de inicio ya pasó. Reserva cancelada automáticamente.");
        }

        this.estado = EstadoReserva.CONFIRMADA;


        //confirma que el check-in de la reserva se haga dentro del tiempo acordado
    }

    public void cancelarReserva () {

        if (this.estado == EstadoReserva.COMPLETADA) {
            throw new IllegalStateException("No se puede cancelar una reserva COMPLETADA.");
        }
        this.estado = EstadoReserva.CANCELADA;

    }

    public void finalizarReserva (){

        if (this.estado == EstadoReserva.CONFIRMADA){
            this.estado = EstadoReserva.COMPLETADA;
        }else {
            throw new IllegalStateException("Solo se puede finalizar una reserva CONFIRMADA.");
        }

    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("pasajero", this.pasajero.toJSON());
        json.put("habitacion", this.habitacionReservada.toJSON());
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
        return "Reserva{" +
                "id=" + id +
                ", Pasajero=" + pasajero.getNombre() +
                ", habitacion=" + habitacionReservada.getNumero() +
                ", noches=" + noches +
                ", estado=" + estado +
                ", montoTotal=$" + montoTotal +
                '}';
    }
}

