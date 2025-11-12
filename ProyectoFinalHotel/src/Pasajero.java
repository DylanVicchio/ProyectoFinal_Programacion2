import Interfaz.Guardable;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;


public class Pasajero extends Persona implements Guardable {

    private String origen;
    private String domicilioOrigen;
    private ArrayList<Ocupacion> historialEstadia;

    public Pasajero(String nombre, String apellido, int numeroCell, int dni, int direccion, String mail, String origen, String domicilioOrigen, Ocupacion ocupacion) {
        super(nombre, apellido, numeroCell, dni, direccion, mail);
        this.origen = origen;
        this.domicilioOrigen = domicilioOrigen;
        this.historialEstadia = new ArrayList<>();
    }

    public Pasajero(JSONObject json) {
        super(
                json.getString("nombre"),
                json.getString("apellido"),
                json.getInt("numeroCell"),
                json.getInt("dni"),
                json.getInt("direccion"),
                json.getString("mail")
        );
        this.origen = json.getString("origen");
        this.domicilioOrigen = json.getString("domicilioOrigen");
        this.historialEstadia = new ArrayList<>();
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
        this.historialEstadia.add(ocupacion);
    }

    public String getHistorial() {
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < this.historialEstadia.size(); i++) {
            str.append(this.historialEstadia.get(i).toString());
        }
        return str.toString();
    }

    public int calcularFidelidad() {
        return historialEstadia.size();
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", getId());
        json.put("nombre", getNombre());
        json.put("apellido", getApellido());
        json.put("numeroCell", getNumeroCell());
        json.put("dni", getDni());
        json.put("direccion", getDireccion());
        json.put("mail", getMail());
        json.put("origen", this.origen);
        json.put("domicilioOrigen", this.domicilioOrigen);
        json.put("cantidadEstadias", this.historialEstadia.size());

        JSONArray idsOcupaciones = new JSONArray();
        for (Ocupacion ocu : historialEstadia) {
            idsOcupaciones.put(ocu.getId());
        }
        json.put("idsHistorial", idsOcupaciones);

        return json;
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
