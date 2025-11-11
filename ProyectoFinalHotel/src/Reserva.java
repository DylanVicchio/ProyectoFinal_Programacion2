import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Reserva {

    Scanner sc = new Scanner(System.in);

    private int id;
    private static int contador = 1;
    private LocalDateTime diaReserva;
    private LocalDateTime diaEntrada;
    private LocalDateTime diaSalida;
    private Habitacion habitacionReservada;
    private EstadoReserva estado;
    private double montoTotal;
    private Duration duration = Duration.between(this.diaEntrada, this.diaSalida);


    public Reserva(LocalDateTime diaEntrada, LocalDateTime diaSalida, Habitacion habitacion) {
        this.diaReserva = LocalDateTime.now();
        this.id = contador;
        contador++;
        this.habitacionReservada = habitacion;
        this.estado = EstadoReserva.PENDIENTE;
        this.diaEntrada = diaEntrada;
        this.diaSalida = diaSalida;
        this.montoTotal = habitacionReservada.calcularPrecio((int) duration.toDays());
    }

    public LocalDateTime getDiaReserva() {
        return diaReserva;
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

    public Habitacion getHabitacionReservada() {
        return habitacionReservada;
    }

    public void setHabitacionReservada(Habitacion habitacionReservada) {
        this.habitacionReservada = habitacionReservada;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void calcularNuevoMonto() {
        this.montoTotal = habitacionReservada.calcularPrecio((int) duration.toDays());
    }

    public EstadoReserva verificarEstadoReserva(){

        if (LocalDateTime.now().isBefore(this.diaEntrada) != false || this.estado == EstadoReserva.CONFIRMADA){

            return this.estado;

        }

        this.estado = EstadoReserva.CANCELADA;
        return this.estado;

        //comprueba el estado de la reserva y lo devuelve

    }

    public String confirmarReserva(){

        if (LocalDateTime.now().isAfter(diaEntrada) != true){
            this.estado = EstadoReserva.CANCELADA;
            return "la reserva expiro \n";
        }

        this.estado = EstadoReserva.CONFIRMADA;
        return "reserva confirmada \n";

        //confirma que el check-in de la reserva se haga dentro del tiempo acordado
    }

    public void cancelarReserva () {

        int s;
        System.out.println("esta seguro que desea cancelar la reserva? 1-si, 2-no \n");
        s = sc.nextInt();

        if (s < 1 || s > 2){
            throw new IllegalArgumentException("numero ingresado no valido");
        }

        if ( s == 1){
            this.diaEntrada = null;
            this.diaSalida = null;
            this.diaReserva = null;
            this.estado = EstadoReserva.CANCELADA;

            System.out.println("Se cancelo la reserva exitosamente \n");
        }

        if (s == 2){
            System.out.println("cancelacion no realisada \n");
        }

        //libera el espacio de la reserva en caso de cancelarla

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
