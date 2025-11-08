public enum TipoHabitacion {

    INDIVIDUAL("Individual", 1, 1000),
    DOBLE("Doble", 2, 2000),
    TRIPLE("Triple", 3, 3000),
    SUITE("Suite", 2, 6000),
    PRESIDENCIAL("Presidencial", 4, 20000);

    private String descripcion;
    private int capacidad;
    private double precio;

    TipoHabitacion(String descripcion, int capacidad, double precio) {
        this.descripcion = descripcion;
        this.capacidad = capacidad;
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public double getPrecio() {
        return precio;
    }
}
