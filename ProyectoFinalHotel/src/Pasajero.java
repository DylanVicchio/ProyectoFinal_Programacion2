public class Pasajero extends Persona {

    private String origen;
    private String domicilioOrigen;

    public Pasajero(String nombre, String apellido, int numeroCell, int dni, int direccion, String mail, String origen, String domicilioOrigen) {
        super(nombre, apellido, numeroCell, dni, direccion, mail);
        this.origen = origen;
        this.domicilioOrigen = domicilioOrigen;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDomicilioOrigen() {
        return domicilioOrigen;
    }

    public void setDomicilioOrigen(String domicilioOrigen) {
        this.domicilioOrigen = domicilioOrigen;
    }

    @Override
    public String toString() {
        return "Pasajero{" + '\'' + super.toString() +
                "origen='" + origen + '\'' +
                ", domicilioOrigen='" + domicilioOrigen + '\'' +
                '}';
    }
}
