import Interfaz.Guardable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Enum.EstadoHabitacion;
import Exception.ReservaInvalidaException;

public class Ocupacion implements Guardable {

    private final int id;
    private static int contador = 1;
    private Habitacion habitacion;
    private Pasajero pasajero;
    private Reserva reserva; // Puede ser null
    private final LocalDateTime fechaCheckIn;
    private LocalDateTime fechaCheckOut;
    private double montoPagado;
    private final ArrayList<Consumo> consumos;
    private final int idHabitacion_json;
    private final int dniPasajero_json;
    private final int idReserva_json;

    public Ocupacion(Habitacion habitacion, Pasajero pasajero, Reserva reserva) {
        this.id = contador++;
        this.habitacion = habitacion;
        this.pasajero = pasajero;
        this.reserva = reserva;
        this.fechaCheckIn = LocalDateTime.now();
        this.fechaCheckOut = null;
        this.montoPagado = 0;
        this.consumos = new ArrayList<>();

        habitacion.setEstadoHabitacion(EstadoHabitacion.OCUPADO, "Check-In Pasajero: " + pasajero.getDni());

        this.idHabitacion_json = habitacion.getId();
        this.dniPasajero_json = pasajero.getDni();
        this.idReserva_json = (reserva != null) ? reserva.getId() : -1;
    }

    public Ocupacion(JSONObject json) {
        this.id = json.getInt("id");
        this.fechaCheckIn = LocalDateTime.parse(json.getString("fechaCheckIn"));
        if (json.has("fechaCheckOut")) {
            this.fechaCheckOut = LocalDateTime.parse(json.getString("fechaCheckOut"));
        } else {
            this.fechaCheckOut = null;
        }

        this.montoPagado = json.getDouble("montoPagado");
        this.idHabitacion_json = json.getInt("idHabitacion");
        this.dniPasajero_json = json.getInt("dniPasajero");
        this.idReserva_json = json.optInt("idReserva", -1);
        this.habitacion = null;
        this.pasajero = null;
        this.reserva = null;

        this.consumos = new ArrayList<>();
        JSONArray arrayConsumos = json.getJSONArray("consumos");
        for (int i = 0; i < arrayConsumos.length(); i++) {
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

    public int getIdHabitacion_json() {
        return idHabitacion_json;
    }

    public int getDniPasajero_json() {
        return dniPasajero_json;
    }

    public int getIdReserva_json() {
        return idReserva_json;
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

    public List<Consumo> getConsumos() {
        return new ArrayList<>(consumos);
    }

    public void setFechaCheckOut(LocalDateTime fechaCheckOut) {
        this.fechaCheckOut = fechaCheckOut;
    }

    public void agregarConsumos(Consumo consumo) {
        if (fechaCheckOut != null) {
            throw new IllegalStateException("Ocupacion finalizada, no es posible agregar consumos");
        }
        if (consumo == null) {
            throw new IllegalArgumentException("Consumo invalido");
        }
        consumos.add(consumo);
    }

    public void setMontoPagado(double montoPagado) {
        if (montoPagado < 0) {
            throw new IllegalArgumentException("Monto pagado invalido");
        }
        this.montoPagado = montoPagado;
    }

    public int getDuracion() {
        LocalDateTime fin;
        if (fechaCheckOut == null) {
            fin = LocalDateTime.now();
        } else {
            fin = fechaCheckOut;
        }
        return (int) ChronoUnit.DAYS.between(fechaCheckIn, fin);
    }

    public double calcularTotal() {
        int noches = getDuracion();
        if (noches <= 0) {
            noches = 1;
        }
        double totalHabitacion = habitacion.calcularPrecio(noches);
        double totalConsumos = 0;

        for (Consumo c : consumos) {
            totalConsumos += c.getMonto();
        }

        return totalHabitacion + totalConsumos;
    }

    public void finalizarOcupacion() {
        if (fechaCheckOut != null) {
            throw new IllegalArgumentException("Check out ya fue realizado");
        }
        this.fechaCheckOut = LocalDateTime.now();
        this.montoPagado = calcularTotal();
        habitacion.setEstadoHabitacion(EstadoHabitacion.LIMPIEZA, "Limpieza luego del check out");
        pasajero.addHistorial(this);

        if (reserva != null) {
            reserva.finalizarReserva();
        }
    }

    public boolean verificarActiva() {

        return fechaCheckOut == null;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", this.id);

        json.put("idHabitacion", this.idHabitacion_json);
        json.put("dniPasajero", this.dniPasajero_json);
        if (this.idReserva_json != -1) {
            json.put("idReserva", this.idReserva_json);
        }

        json.put("fechaCheckIn", this.fechaCheckIn.toString());
        if (fechaCheckOut != null) {
            json.put("fechaCheckOut", this.fechaCheckOut.toString());
        }
        json.put("montoPagado", this.montoPagado);

        // Consumos (está bien anidado)
        JSONArray consumosArray = new JSONArray();
        for (Consumo consumo : consumos) {
            consumosArray.put(consumo.toJSON());
        }
        json.put("consumos", consumosArray);

        return json;
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
        String pStr = (pasajero != null) ? pasajero.getNombre() : "DNI: " + dniPasajero_json;
        String hStr = (habitacion != null) ? "" + habitacion.getNumero() : "ID: " + idHabitacion_json;
        return "Ocupacion{" +
                "id=" + id +
                ", hab=" + hStr +
                ", pax=" + pStr +
                ", checkIn=" + fechaCheckIn.toLocalDate() +
                ", checkOut=" + (fechaCheckOut != null ? fechaCheckOut.toLocalDate() : "ACTIVA") +
                '}';
    }
}