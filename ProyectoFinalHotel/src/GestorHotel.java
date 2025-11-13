import Interfaz.Guardable;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class GestorHotel<T extends Guardable>  {
    private List<T> elementos;
    private String archivoJSON;

    public GestorHotel(String archivoJSON) {
        this.elementos = new ArrayList<>();
        this.archivoJSON = archivoJSON;
    }

    public void agregar(T elemento) {
        elementos.add(elemento);
    }

    public boolean eliminar(T elemento) {
        return elementos.remove(elemento);
    }

    public T buscarPorDni(int dni) {
        for (T elemento : elementos) {
            if (elemento instanceof Pasajero) {
                if (((Pasajero) elemento).getDni() == dni) {
                    return elemento;
                }
            }
        }
        return null;
    }

    public T buscarPorId(int id) {
        for (T elemento : elementos) {
            // Se usa el patrón de instanceof para acceder al ID correcto
            if (elemento instanceof Habitacion) {
                if (((Habitacion) elemento).getId() == id) return elemento;
            } else if (elemento instanceof Reserva) {
                if (((Reserva) elemento).getId() == id) return elemento;
            } else if (elemento instanceof Ocupacion) {
                if (((Ocupacion) elemento).getId() == id) return elemento;
            }
        }
        return null;
    }

    public int cantidad() {
        return elementos.size();
    }

    public void guardarEnArchivo() {
            JSONArray jsonArray = new JSONArray();

            for (T elemento : elementos) {
                jsonArray.put(elemento.toJSON());
            }
            JSONUtiles.escribirArchivo(archivoJSON, jsonArray);
    }
}
