import Interfaz.Guardable;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Objects;

public class Consumo implements Guardable {
    private final int id;
    private static int contador = 1;
    private String descripcion;
    private double monto;
    private LocalDateTime fecha;

    public Consumo(String descripcion, double monto) {
        this.id = contador++;
        this.descripcion = descripcion;
        this.monto = monto;
        this.fecha = LocalDateTime.now();
    }

    public Consumo(JSONObject json) {
        this.id = json.getInt("id");
        this.descripcion = json.getString("descripcion");
        this.monto = json.getDouble("monto");
        this.fecha = LocalDateTime.parse(json.getString("fecha"));
        if(this.id >= contador){
            contador = this.id + 1;
        }
    }

    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }



    public void setDescripcion(String descripcion) {

        if(descripcion == null || descripcion.trim().isEmpty()){
         throw new IllegalArgumentException("Descripcion vacia invalida");
        }
            this.descripcion = descripcion;
    }

    public void setMonto(double monto) {
        if (monto < 0) {
            throw new IllegalArgumentException("Monto invalido");
        }
        this.monto = monto;

    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        object.put("id", this.id);
        object.put("descripcion", this.descripcion);
        object.put("monto", this.monto);
        object.put("fecha", this.fecha.toString());
        return object;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Consumo consumo = (Consumo) o;
        return id == consumo.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Consumo{" +
                "id=" + id +
                ", descripcion='" + descripcion + '\'' +
                ", monto=" + monto +
                ", fecha=" + fecha +
                '}';
    }
}
