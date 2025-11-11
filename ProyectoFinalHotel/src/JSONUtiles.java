import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONUtiles {

    public static void escribirArchivo(String nombreArchivo, JSONObject jsonObject) {
        try (FileWriter fileWriter = new FileWriter(nombreArchivo)) {
            fileWriter.write(jsonObject.toString());
            fileWriter.flush();
        } catch (IOException e) {
            System.err.println("Error al escribir archivo JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void escribirArchivo(String nombreArchivo, JSONArray jsonArray) {
        try (FileWriter fileWriter = new FileWriter(nombreArchivo)) {
            fileWriter.write(jsonArray.toString());
            fileWriter.flush();
        } catch (IOException e) {
            System.err.println("Error al escribir archivo JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static JSONObject leerArchivo(String nombreArchivo) {
        try {
            String contenido = new String(Files.readAllBytes(Paths.get(nombreArchivo)));
            System.out.println(" Archivo JSON leído: " + nombreArchivo);
            return new JSONObject(contenido);
        } catch (IOException e) {
            System.err.println("Error al leer archivo JSON: " + e.getMessage());
            return new JSONObject(); // Retorna objeto vacío
        }
    }


    public static JSONArray leerArchivoArray(String nombreArchivo) {
        try {
            String contenido = new String(Files.readAllBytes(Paths.get(nombreArchivo)));
            System.out.println("Archivo JSON array leído: " + nombreArchivo);
            return new JSONArray(contenido);
        } catch (IOException e) {
            System.err.println("Error al leer archivo JSON array: " + e.getMessage());
            return new JSONArray(); // Retorna array vacío
        }
    }

    public static boolean validarJSON(JSONObject json) {
        return json != null && json.length() > 0;
    }

}
