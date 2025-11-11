import org.json.JSONObject;

public interface Guardable {

    // convierte el objeto a formato JSON
    JSONObject toJSON();

    // guarda el objeto en un archivo
    void guardarEnArchivo();

    // carga el objeto desde un archivo
    void cargarDesdeArchivo();
}
