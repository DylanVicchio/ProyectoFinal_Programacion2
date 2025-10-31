import java.util.Objects;

public class Habitaciones {

    private EstadoHabitacion estado;
    private int numeroHabitacion;
    private int piso;
    private Tamanio tamanio;
    private boolean matrimonial;
    private boolean servicios;
    private boolean cajaFuerte;
    private boolean Jacuzzi;
    private boolean banio;

    public Habitaciones(EstadoHabitacion estado, int numeroHabitacion, int piso, Tamanio tamanio, boolean matrimonial, boolean servicios, boolean cajaFuerte, boolean jacuzzi, boolean banio) {
        this.estado = estado;
        this.numeroHabitacion = numeroHabitacion;
        this.piso = piso;
        this.tamanio = tamanio;
        this.matrimonial = matrimonial;
        this.servicios = servicios;
        this.cajaFuerte = cajaFuerte;
        this.Jacuzzi = jacuzzi;
        this.banio = banio;
    }

    public EstadoHabitacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoHabitacion estado) {
        this.estado = estado;
    }

    public int getNumeroHabitacion() {
        return numeroHabitacion;
    }

    public void setNumeroHabitacion(int numeroHabitacion) {
        this.numeroHabitacion = numeroHabitacion;
    }

    public int getPiso() {
        return piso;
    }

    public void setPiso(int piso) {
        this.piso = piso;
    }

    public Tamanio getTamanio() {
        return tamanio;
    }

    public void setTamanio(Tamanio tamanio) {
        this.tamanio = tamanio;
    }

    public boolean isMatrimonial() {
        return matrimonial;
    }

    public void setMatrimonial(boolean matrimonial) {
        this.matrimonial = matrimonial;
    }

    public boolean isServicios() {
        return servicios;
    }

    public void setServicios(boolean servicios) {
        this.servicios = servicios;
    }

    public boolean isCajaFuerte() {
        return cajaFuerte;
    }

    public void setCajaFuerte(boolean cajaFuerte) {
        this.cajaFuerte = cajaFuerte;
    }

    public boolean isJacuzzi() {
        return Jacuzzi;
    }

    public void setJacuzzi(boolean jacuzzi) {
        Jacuzzi = jacuzzi;
    }

    public boolean isBanio() {
        return banio;
    }

    public void setBanio(boolean banio) {
        this.banio = banio;
    }

    @Override
    public String toString() {
        return "Habitaciones{" +
                "estado=" + estado +
                ", numeroHabitacion=" + numeroHabitacion +
                ", piso=" + piso +
                ", tamanio=" + tamanio +
                ", matrimonial=" + matrimonial +
                ", servicios=" + servicios +
                ", cajaFuerte=" + cajaFuerte +
                ", Jacuzzi=" + Jacuzzi +
                ", banio=" + banio +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Habitaciones that = (Habitaciones) o;
        return numeroHabitacion == that.numeroHabitacion && piso == that.piso;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroHabitacion, piso);
    }
}
