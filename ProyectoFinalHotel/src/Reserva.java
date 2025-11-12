import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reserva {


    private final int id;
    private static int contador = 1;
    private LocalDate diaReserva;
    private LocalDate diaEntrada;
    private LocalDate diaSalida;
    private Habitacion habitacionReservada;
    private EstadoReserva estado;
    private double montoTotal;


    public Reserva(LocalDate diaEntrada, LocalDate diaSalida, Habitacion habitacion) {
        this.diaReserva = LocalDate.now();
        this.id = contador;
        contador++;
        this.habitacionReservada = habitacion;
        this.estado = EstadoReserva.PENDIENTE;
        this.diaEntrada = diaEntrada;
        this.diaSalida = diaSalida;
        this.montoTotal = habitacionReservada.calcularPrecio((int) duration.toDays());
    }

    public LocalDate getDiaReserva() {
        return diaReserva;
    }

    public LocalDate getDiaEntrada() {
        return diaEntrada;
    }

    public void setDiaEntrada(LocalDate diaEntrada) {
        this.diaEntrada = diaEntrada;
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

    public boolean confirmarReserva(){

        if (!LocalDate.now().isAfter(diaEntrada)){
            this.estado = EstadoReserva.CANCELADA;
            return false;
        }

        this.estado = EstadoReserva.CONFIRMADA;
        return true;

        //confirma que el check-in de la reserva se haga dentro del tiempo acordado
    }

    public void cancelarReserva () {

        this.diaEntrada = null;
        this.diaSalida = null;
        this.diaReserva = null;
        this.estado = EstadoReserva.CANCELADA;

    }

    public EstadoReserva finalizarReserva (){

        if (this.estado == EstadoReserva.CONFIRMADA){
            this.estado = EstadoReserva.COMPLETADA;
            return this.estado;
        }

        System.out.println("reserva no confirmada, cancele la reserva para finalisarla \n");
        return this.estado;

        //termina la reserva en el check-out y chequea si la finalizacion es correcta

    }





}
