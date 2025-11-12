import Interfaz.Guardable;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class GestorHotel<T> implements Guardable {
    private List<T> elementos;
    private String archivoJSON;

    public GestorHotel(String archivoJSON) {
        this.elementos = new ArrayList<>();
        this.archivoJSON = archivoJSON;
    }

    public void agregar(T elemento) {
        elementos.add(elemento);
    }

    public boolean eliminar(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID no válido");
        }

        for (int i = 0; i < elementos.size(); i++) {
            T elemento = elementos.get(i);

            try {
                int elementoId = (int) elemento.getClass().getMethod("getId").invoke(elemento);
                if (String.valueOf(elementoId).equals(id)) {
                    elementos.remove(i);
                    System.out.println("Elemento con ID " + id + " eliminado");
                    return true;
                }
            } catch (Exception e) {
                System.err.println("Error al acceder al ID del elemento: " + e.getMessage());
            }
        }

        System.out.println("Elemento con ID " + id + " no encontrado");
        return false;
    }

    public T buscar(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        for (T elemento : elementos) {
            try {
                int elementoId = (int) elemento.getClass().getMethod("getId").invoke(elemento);
                if (String.valueOf(elementoId).equals(id)) {
                    return elemento;
                }
            } catch (Exception e) {
                System.err.println("Error al buscar elemento: " + e.getMessage());
            }
        }
        return null;
    }

    public int cantidad() {
        return elementos.size();
    }

    public void guardarEnArchivo() {
        try {
            JSONArray jsonArray = new JSONArray();

            for (T elemento : elementos) {
                JSONObject jsonObj = (JSONObject) elemento.getClass().getMethod("toJSON").invoke(elemento);
                jsonArray.put(jsonObj);
            }

            JSONUtiles.escribirArchivo(archivoJSON, jsonArray);
            System.out.println(elementos.size() + " elementos guardados en " + archivoJSON);

        } catch (Exception e) {
            System.err.println("Error al guardar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void cargarDesdeArchivo() {
        try {
            JSONArray jsonArray = JSONUtiles.leerArchivoArray(archivoJSON);
            elementos.clear();

            System.out.println("Archivo JSON leído: " + jsonArray.length() + " elementos");

        } catch (Exception e) {
            System.err.println("Error al cargar datos: " + e.getMessage());
        }
    }

    public JSONArray exportarJSON() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (T elemento : elementos) {
                JSONObject jsonObj = (JSONObject) elemento.getClass().getMethod("toJSON").invoke(elemento);
                jsonArray.put(jsonObj);
            }
        } catch (Exception e) {
            System.err.println("Error al exportar JSON: " + e.getMessage());
        }
        return jsonArray;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("archivo", archivoJSON);
        json.put("elementos", exportarJSON());
        json.put("cantidad", elementos.size());
        return json;
    }


}
