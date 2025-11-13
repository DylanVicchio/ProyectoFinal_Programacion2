package Enum;

public enum EstadoHabitacion {
    LIBRE("Libre"),
    OCUPADO("Ocupada"),
    RESERVADO("Reservada"),
    LIMPIEZA("Limpieza"),
    MANTENIMIENTO("Mantenimiento"),
    FUERA_SERVICIO("Fuera de servicio");

    private final String descripcion;

    EstadoHabitacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean estaDisponible() {
        return this == LIBRE || this == LIMPIEZA;
    }
}
