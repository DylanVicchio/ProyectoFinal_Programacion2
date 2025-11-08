import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Habitacion {

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
        this.numero = numero;
    }

    public void setTipoHabitacion(TipoHabitacion tipoHabitacion) {
        this.tipoHabitacion = tipoHabitacion;
    }

    public void setPiso(int piso) {
        this.piso = piso;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public void setPrecioPorNoche(double precioPorNoche) {
        if (precioPorNoche < 0) {
            throw new IllegalArgumentException("Precio por noche no valido");
        }
        this.precioPorNoche = precioPorNoche;
    }

    public void setEstadoHabitacion(EstadoHabitacion estadoHabitacion) {
        this.estadoHabitacion = estadoHabitacion;
    }

    public void setMotivoNoDisponible(String motivoNoDisponible) {
        this.motivoNoDisponible = motivoNoDisponible;
    }

    public boolean estaDisponmible(LocalDate inicio, LocalDate fin){
        //falta completar clases para consultas
    }

    public double calcularPrecio(int noches){
        if(noches < 0){
            throw new IllegalArgumentException("Noches no validas");
        }

        return noches * precioPorNoche;
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
                ", tipoHabitacion=" + tipoHabitacion +
                ", piso=" + piso +
                ", capacidad=" + capacidad +
                ", precioPorNoche=" + precioPorNoche +
                ", estadoHabitacion=" + estadoHabitacion +
                ", motivoNoDisponible='" + motivoNoDisponible + '\'' +
                '}';
    }
}
