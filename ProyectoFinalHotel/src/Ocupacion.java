import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;

public class Ocupacion implements Guardable{

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

        habitacion.setEstadoHabitacion(EstadoHabitacion.OCUPADO, "");
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

        this.consumos = new ArrayList<>();
        JSONArray arrayConsumos = object.getJSONArray("consumos");
        for(int i = 0; i < arrayConsumos.length(); i++){
            consumos.add(new Consumo(arrayConsumos.getJSONObject(i)));
        }

        this.habitacion = new Habitacion(object.getJSONObject("habitacion"));
        //Tira error porque faltan los constructores JSON de reserva y pasajero
        this.pasajero = new Pasajero(object.getJSONObject("pasajero"));
        this.reserva = new Reserva(object.getJSONObject("reserva"));
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

    public void finalizarOcupacion(){
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
        object.put("pasajero", this.pasajero);
        object.put("habitacion", this.habitacion);
        object.put("reserva", this.reserva);
        object.put("fechaCheckIn", this.fechaCheckIn.toString());

        if (fechaCheckOut != null) {
            object.put("fechaCheckOut", this.fechaCheckOut.toString());
        }

        object.put("montoPagado", this.montoPagado);

        // Guardar consumos
        JSONArray consumosArray = new JSONArray();
        for (Consumo consumo : consumos) {
            consumosArray.put(consumo.toJSON());
        }
        object.put("consumos", consumosArray);

        return object;
    }

    @Override
    public void guardarEnArchivo() {
        JSONUtiles.escribirArchivo("Ocupacion" + id + ".json", toJSON());
    }

    @Override
    public void cargarDesdeArchivo() {
        JSONObject array = JSONUtiles.leerArchivo("ocupaciones/" + id + ".json");
        this.fechaCheckIn = LocalDateTime.parse(array.getString("fechaCheckIn"));

       if(array.has("fechaCheckOut")){
           this.fechaCheckOut = LocalDateTime.parse(array.getString("fechaCheckOut"));
       }

        this.montoPagado = array.getDouble("montoPagado");

        this.consumos.clear();
        JSONArray consumosArray = array.getJSONArray("consumos");
        for (int i = 0; i < consumosArray.length(); i++) {
            consumos.add(new Consumo(consumosArray.getJSONObject(i)));
        }
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
