import java.time.LocalDateTime;

public class Checks {

    private LocalDateTime llegada;
    private LocalDateTime salida;

    public Checks(LocalDateTime llegada, LocalDateTime salida) {
        this.llegada = llegada;
        this.salida = salida;
    }

    public LocalDateTime getLlegada() {
        return llegada;
    }

    public void setLlegada(LocalDateTime llegada) {
        this.llegada = llegada;
    }

    public LocalDateTime getSalida() {
        return salida;
    }

    public void setSalida(LocalDateTime salida) {
        this.salida = salida;
    }

    @Override
    public String toString() {
        return "Checks{" +
                "llegada=" + llegada +
                ", salida=" + salida +
                '}';
    }
}
