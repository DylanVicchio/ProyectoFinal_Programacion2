import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONUtiles {

    public static void escribirArchivo(String nombreArchivo, JSONArray jsonArray) {
        try (FileWriter fileWriter = new FileWriter(nombreArchivo)) {
            fileWriter.write(jsonArray.toString());
            fileWriter.flush();
        } catch (IOException e) {
            System.err.println("Error al escribir archivo JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static JSONArray leerArchivoArray(String nombreArchivo) {
        try {
            String contenido = new String(Files.readAllBytes(Paths.get(nombreArchivo)));
            System.out.println("Archivo JSON array leído: " + nombreArchivo);
            return new JSONArray(contenido);
        } catch (IOException e) {
            System.err.println("Error al leer archivo JSON array: " + e.getMessage());
            return new JSONArray();
        }
    }
}
