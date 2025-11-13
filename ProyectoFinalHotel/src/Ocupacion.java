import Interfaz.Guardable;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;
import Enum.EstadoHabitacion;
import Exception.ReservaInvalidaException;
public class Ocupacion implements Guardable {

    private int id;
    private static int contador = 1;
    private Habitacion habitacion;
    private Pasajero pasajero;
    private Reserva reserva;
    private LocalDateTime fechaCheckIn;
    private LocalDateTime fechaCheckOut;
    private double montoPagado;
    private ArrayList<Consumo> consumos;
    private int idHabitacion;
    private int dniPasajero;
    private int idReserva;

    public Ocupacion(Habitacion habitacion, Pasajero pasajero, Reserva reserva) {
        this.id = contador++;
        this.habitacion = habitacion;
        this.pasajero = pasajero;
        this.reserva = reserva;
        this.fechaCheckIn = LocalDateTime.now();
        this.fechaCheckOut = null;
        this.montoPagado = 0;
        this.consumos = new ArrayList<>();
        habitacion.setEstadoHabitacion(EstadoHabitacion.OCUPADO, "");
        this.idHabitacion = habitacion.getId();
        this.dniPasajero = pasajero.getDni();
        if(reserva != null) {
            this.idReserva = reserva.getId();
        }
    }

    public Ocupacion(JSONObject object){
        this.id = object.getInt("id");
        this.fechaCheckIn = LocalDateTime.parse(object.getString("fechaCheckIn"));
        if (object.has("fechaCheckOut")) {
            this.fechaCheckOut = LocalDateTime.parse(object.getString("fechaCheckOut"));
        } else {
            this.fechaCheckOut = null;
        }
        this.montoPagado = object.getDouble("montoPagado");

        this.idHabitacion = object.getInt("idHabitacion");
        this.dniPasajero = object.getInt("dniPasajero");
        this.idReserva = object.optInt("idReserva", -1);

        this.habitacion = null;
        this.pasajero = null;
        this.reserva = null;

        this.consumos = new ArrayList<>();
        JSONArray arrayConsumos = object.getJSONArray("consumos");
        for(int i = 0; i < arrayConsumos.length(); i++){
            consumos.add(new Consumo(arrayConsumos.getJSONObject(i)));
        }
        if (this.id >= contador) {
            contador = this.id + 1;
        }
    }

    public void reconectarObjetos(Pasajero p, Habitacion h, Reserva r) {
        this.pasajero = p;
        this.habitacion = h;
        this.reserva = r;
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

    public int getIdHabitacion() {
        return idHabitacion;
    }

    public int getDniPasajero() {
        return dniPasajero;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setFechaCheckOut(LocalDateTime fechaCheckOut) {
        this.fechaCheckOut = fechaCheckOut;
    }

    public void setMontoPagado(double montoPagado) {
        if(montoPagado < 0) {
            throw new IllegalArgumentException("Monto pagado invalido");
        }
        this.montoPagado = montoPagado;
    }

    public void agregarConsumos(Consumo consumo) {
        if(fechaCheckOut != null){
            throw new IllegalArgumentException("Ocupacion finalizada, no es posible agregar consumos");
        }
        if(consumo == null){
            throw new IllegalArgumentException("Consumo invalido");
        }
        consumos.add(consumo);
    }

    public int getDuracion() {
        LocalDateTime fin;
        if(fechaCheckOut == null){
            fin = LocalDateTime.now();
        }else{
            fin = fechaCheckOut;
        }
        return (int)ChronoUnit.DAYS.between(fechaCheckIn, fin);
    }

    public double calcularTotal(){
        int noches = getDuracion();
        if(noches <= 0){
            throw new IllegalArgumentException("Noches invalidas");
        }
        double totalHabitacion = habitacion.calcularPrecio(noches);
        double totalConsumos = 0;

        for(Consumo c : consumos){
            totalConsumos += c.getMonto();
        }

        return totalHabitacion + totalConsumos;
    }

    public void finalizarOcupacion() throws ReservaInvalidaException {
        if(fechaCheckOut != null){
            throw new IllegalArgumentException("Check out ya fue realizado");
        }
        this.fechaCheckOut = LocalDateTime.now();
        this.montoPagado = calcularTotal();
        habitacion.setEstadoHabitacion(EstadoHabitacion.LIMPIEZA, "Limpieza luego del check out");
        pasajero.addHistorial(this);

        if(reserva != null){
            reserva.finalizarReserva();
        }
    }

    public boolean verificarActiva(){
        return fechaCheckOut == null;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        object.put("id", this.id);
        object.put("dniPasajero", this.dniPasajero);
        object.put("idHabitacion", this.idHabitacion);
        if(this.idReserva != -1) {
            object.put("idReserva", this.idReserva);
        }
        object.put("fechaCheckIn", this.fechaCheckIn.toString());
        if (fechaCheckOut != null) {
            object.put("fechaCheckOut", this.fechaCheckOut.toString());
        }
        object.put("montoPagado", this.montoPagado);

        JSONArray consumosArray = new JSONArray();
        for (Consumo consumo : consumos) {
            consumosArray.put(consumo.toJSON());
        }
        object.put("consumos", consumosArray);

        return object;
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
