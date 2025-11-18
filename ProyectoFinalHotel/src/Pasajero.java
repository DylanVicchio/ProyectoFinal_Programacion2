import Interfaz.Guardable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class Pasajero extends Persona implements Guardable {

    private String origen;
    private String domicilioOrigen;
    private final ArrayList<Ocupacion> historialEstadia;

    public Pasajero(String nombre, String apellido, int numeroCell, int dni, int direccion, String mail, String origen, String domicilioOrigen) {
        super(nombre, apellido, numeroCell, dni, direccion, mail);
        this.origen = origen;
        this.domicilioOrigen = domicilioOrigen;
        this.historialEstadia = new ArrayList<>();
    }

    public Pasajero(JSONObject json) {
        super(json);
        this.origen = json.getString("origen");
        this.domicilioOrigen = json.getString("domicilioOrigen");
        this.historialEstadia = new ArrayList<>();
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

    public void addHistorial(Ocupacion ocupacion) throws IllegalArgumentException {
        if (ocupacion == null) {
            throw new IllegalArgumentException("La ocupación no puede ser null");
        }
        this.historialEstadia.add(ocupacion);
    }

    public ArrayList<Ocupacion> getHistorial() {
        return new ArrayList<>(this.historialEstadia);
    }


    public String getHistorialString() {
        StringBuilder str = new StringBuilder();

        if (historialEstadia.isEmpty()) {
            return "Sin estadías previas.";
        }

        str.append("Historial de Estadías:\n");
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

        JSONObject json = super.toJSON();
        json.put("origen", this.origen);
        json.put("domicilioOrigen", this.domicilioOrigen);
        json.put("cantidadEstadias", this.historialEstadia.size());

        JSONArray idOcupaciones = new JSONArray();
        for (Ocupacion ocu : historialEstadia) {

            idOcupaciones.put(ocu.getId());

        }
        json.put("idsHistorial", idOcupaciones);

        return json;
    }


    @Override
    public String toString() {
        // toString() simple
        return "Pasajero{" +
                "dni=" + getDni() +
                ", nombre='" + getNombre() + " " + getApellido() + '\'' +
                ", origen='" + origen + '\'' +
                '}';
    }
}
