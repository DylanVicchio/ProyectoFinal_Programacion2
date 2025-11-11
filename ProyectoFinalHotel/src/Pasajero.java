import java.util.HashMap;

public class Pasajero extends Persona {

    private String origen;
    private String domicilioOrigen;
    private static int estadias = 0;
    private HashMap<Integer, Ocupacion> historialEstadia = new HashMap<>();

    public Pasajero(int id, String nombre, String apellido, int numeroCell, int dni, int direccion, String mail, String origen, String domicilioOrigen, Ocupacion ocupacion) {
        super(id, nombre, apellido, numeroCell, dni, direccion, mail);
        this.origen = origen;
        this.domicilioOrigen = domicilioOrigen;
        estadias++;
        this.historialEstadia.put(estadias, ocupacion);
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

    public void addHistorial (Ocupacion ocupacion){
        estadias++;
        this.historialEstadia.put(estadias, ocupacion);
    }

    public String getHistorial() {
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < this.historialEstadia.size(); i++) {
            str.append(this.historialEstadia.get(i).toString());
        }
        return str.toString();
    }

    @Override
    public String toString() {
        return "Pasajero{" + super.toString() + '\'' +
                "origen='" + origen + '\'' +
                ", domicilioOrigen='" + domicilioOrigen + '\'' +
                ", historialEstadia=" + getHistorial() +
                '}';
    }
}
