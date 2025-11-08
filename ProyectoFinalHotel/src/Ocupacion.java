public class Ocupacion {

    private Habitaciones habitacion;
    private Pasajero pasajero;
    //private Checks checkIn;
    private double precio;
    private int noches;

    public Ocupacion(Habitaciones habitacion, Pasajero pasajero, , double precio, int noches) {
        this.habitacion = habitacion;
        this.pasajero = pasajero;
        //this.checkIn = checkIn;
        this.precio = precio;
        this.noches = noches;
    }

    public Habitaciones getHabitacion(){
        return habitacion;
    }

    public void setHabitacion(Habitaciones habitacion) {
        this.habitacion = habitacion;
    }

    public Pasajero getPasajero() {
        return pasajero;
    }

    public void setPasajero(Pasajero pasajero) {
        this.pasajero = pasajero;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getNoches() {
        return noches;
    }

    public void setNoches(int noches) {
        this.noches = noches;
    }

    @Override
    public String toString() {
        return "Ocupacion{" +
                "habitacion=" + habitacion +
                ", pasajero=" + pasajero +
                ", precio=" + precio +
                ", noches=" + noches +
                '}';
    }



}
