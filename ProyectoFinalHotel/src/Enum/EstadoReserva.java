package Enum;

public enum EstadoReserva {
    PENDIENTE("Pendiente"),
    CANCELADA("Cancelada"),
    CONFIRMADA("Confirmada"),
    COMPLETADA("Completada");

    private String descripcion;

    EstadoReserva(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
