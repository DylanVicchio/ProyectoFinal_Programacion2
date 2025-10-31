public class Recepcionista extends Persona {

    private int idRecepcionista;
    private String direccionSucursal;
    private EstadoEmpleado estado;

    public Recepcionista(String nombre, String apellido, int numeroCell, int dni, int direccion, String mail, int idRecepcionista, String direccionSucursal, EstadoEmpleado estado) {
        super(nombre, apellido, numeroCell, dni, direccion, mail);
        this.idRecepcionista = idRecepcionista;
        this.direccionSucursal = direccionSucursal;
        this.estado = estado;
    }

    public int getIdRecepcionista() {
        return idRecepcionista;
    }

    public void setIdRecepcionista(int idRecepcionista) {
        this.idRecepcionista = idRecepcionista;
    }

    public String getDireccionSucursal() {
        return direccionSucursal;
    }

    public void setDireccionSucursal(String direccionSucursal) {
        this.direccionSucursal = direccionSucursal;
    }

    public EstadoEmpleado getEstado() {
        return estado;
    }

    public void setEstado(EstadoEmpleado estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Recepcionista{" + '\'' + super.toString() +
                "idRecepcionista=" + idRecepcionista +
                ", direccionSucursal='" + direccionSucursal + '\'' +
                ", estado=" + estado +
                '}';
    }
}
