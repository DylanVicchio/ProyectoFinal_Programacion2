import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Reporte {

    private int id;
    private String tipo;
    private LocalDateTime fechaGeneracion;
    private String contenido;
    private static int contador = 1;

    public Reporte(int id, String tipo, LocalDateTime fechaGeneracion, String contenido) {
        this.id = contador++;
        this.tipo = tipo;
        this.fechaGeneracion = fechaGeneracion;
        this.contenido = contenido;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    @Override
    public String toString() {
        return "Reporte{" +
                "id=" + id +
                ", tipo='" + tipo + '\'' +
                ", fechaGeneracion=" + fechaGeneracion +
                ", contenido='" + contenido + '\'' +
                '}';
    }

}
