import java.time.LocalDateTime;
import java.util.Objects;

public class Consumo {
    private final int id;
    private static int contador = 1;
    private String descripcion;
    private double monto;
    private LocalDateTime fecha;

    public Consumo(String descripcion, double monto, LocalDateTime fecha) {
        this.id = contador++;
        this.descripcion = descripcion;
        this.monto = monto;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setMonto(double monto) {
        if (monto < 0) {
            throw new IllegalArgumentException("Monto no valido");
        }
        this.monto = monto;

    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Consumo consumo = (Consumo) o;
        return id == consumo.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Consumo{" +
                "id=" + id +
                ", descripcion='" + descripcion + '\'' +
                ", monto=" + monto +
                ", fecha=" + fecha +
                '}';
    }
}
