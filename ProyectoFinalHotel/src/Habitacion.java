import Interfaz.Guardable;
import org.json.JSONObject;
import java.time.LocalDate;
import java.util.Objects;
import Enum.EstadoHabitacion;
import Enum.TipoHabitacion;
public class Habitacion implements Guardable {

    private final int id;
    private static int contador = 1;
    private int numero;
    private TipoHabitacion tipoHabitacion;
    private int piso;
    private int capacidad;
    private double precioPorNoche;
    private EstadoHabitacion estadoHabitacion;
    private String motivoNoDisponible;

    public Habitacion(int numero, TipoHabitacion tipo, int piso) {
        this.id = contador++;
        this.numero = numero;
        this.tipoHabitacion = tipo;
        this.piso = piso;
        this.capacidad = tipo.getCapacidad();
        this.precioPorNoche = tipo.getPrecio();
        this.estadoHabitacion = EstadoHabitacion.LIBRE;
        this.motivoNoDisponible = "";
    }

    public Habitacion(JSONObject json) {
        this.id = json.getInt("id");
        this.numero = json.getInt("numero");
        this.tipoHabitacion = TipoHabitacion.valueOf(json.getString("tipo"));
        this.piso = json.getInt("piso");
        this.capacidad = json.getInt("capacidad");
        this.precioPorNoche = json.getDouble("precioPorNoche");
        this.estadoHabitacion = EstadoHabitacion.valueOf(json.getString("estado"));
        this.motivoNoDisponible = json.optString("motivoNoDisponible", "");
    }

    public int getId() {
        return id;
    }

    public int getNumero() {
        return numero;
    }

    public TipoHabitacion getTipoHabitacion() {
        return tipoHabitacion;
    }

    public int getPiso() {
        return piso;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public double getPrecioPorNoche() {
        return precioPorNoche;
    }

    public EstadoHabitacion getEstadoHabitacion() {
        return estadoHabitacion;
    }

    public String getMotivoNoDisponible() {
        return motivoNoDisponible;
    }



    public void setNumero(int numero) {
        if(numero < 0){
            throw new IllegalArgumentException("Numero de habitacion invalido");
        }
        this.numero = numero;
    }

    public void setTipoHabitacion(TipoHabitacion tipoHabitacion) {
        this.tipoHabitacion = tipoHabitacion;
        this.capacidad = tipoHabitacion.getCapacidad();
        this.precioPorNoche = tipoHabitacion.getPrecio();
    }

    public void setPiso(int piso) {
        if(piso < 0){
            throw new IllegalArgumentException("Piso invalido");
        }
        this.piso = piso;
    }

    public void setEstadoHabitacion(EstadoHabitacion estadoHabitacionNuevo, String motivo) {
        this.estadoHabitacion = estadoHabitacionNuevo;

        if(estadoHabitacionNuevo == estadoHabitacion.LIMPIEZA ||
                estadoHabitacionNuevo == estadoHabitacion.MANTENIMIENTO ||
                estadoHabitacionNuevo == estadoHabitacion.FUERA_SERVICIO){
            this.motivoNoDisponible = motivo;
        }else{
            this.motivoNoDisponible = "";
        }
    }

    public boolean estaDisponible(LocalDate inicio, LocalDate fin){
        if(inicio == null || fin == null){
            throw new IllegalArgumentException("Fechas nulas invalidas");
        }

        if(inicio.isAfter(fin)){
            throw new IllegalArgumentException("Fecha incompatibles");
        }

        if(estadoHabitacion != EstadoHabitacion.LIBRE && estadoHabitacion != EstadoHabitacion.LIMPIEZA){
            return false;
        }

        //falta consultar si hay alguna reserva para esa fecha

        return estadoHabitacion == EstadoHabitacion.LIBRE;
    }

    public double calcularPrecio(int noches){
        if(noches <= 0){
            throw new IllegalArgumentException("Noches invalidas");
        }

        return noches * precioPorNoche;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        object.put("id", this.id);
        object.put("numero", this.numero);
        object.put("tipoHabitacion", this.tipoHabitacion.name());
        object.put("piso", this.piso);
        object.put("capacidad", this.capacidad);
        object.put("precioPorNoche", this.precioPorNoche);
        object.put("estadoHabitacion", this.estadoHabitacion.name());
        object.put("motivoNoDisponible", this.motivoNoDisponible);
        return object;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Habitacion that = (Habitacion) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Habitacion{" +
                "id=" + id +
                ", numero=" + numero +
                ", tipoHabitacion=" + tipoHabitacion.getDescripcion() +
                ", piso=" + piso +
                ", capacidad=" + capacidad +
                ", precioPorNoche=" + precioPorNoche +
                ", estadoHabitacion=" + estadoHabitacion.getDescripcion() +
                ", motivoNoDisponible='" + motivoNoDisponible + '\'' +
                '}';
    }
}
