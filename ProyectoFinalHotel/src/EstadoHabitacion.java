public enum EstadoHabitacion {
    LIBRE("Libre"),
    OCUPADO("Ocupada"),
    RESERVADO("Reservada"),
    LIMPIEZA("Limpieza"),
    MANTENIMIENTO("Mantenimiento");

    private String descripcion;

    EstadoHabitacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
