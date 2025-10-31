public class Manager extends Persona {

    private int idManager;
    private String direccionSucursal;
    private final EstadoEmpleado estadoManager;

    public Manager(String nombre, String apellido, int numeroCell, int dni, int direccion, String mail, EstadoEmpleado estadoManager, String direccionSucursal, int idManager) {
        super(nombre, apellido, numeroCell, dni, direccion, mail);
        this.estadoManager = estadoManager;
        this.direccionSucursal = direccionSucursal;
        this.idManager = idManager;
    }

    public int getIdManager() {
        return idManager;
    }

    public void setIdManager(int idManager) {
        this.idManager = idManager;
    }

    public String getDireccionSucursal() {
        return direccionSucursal;
    }

    public void setDireccionSucursal(String direccionSucursal) {
        this.direccionSucursal = direccionSucursal;
    }

    @Override
    public String toString() {
        return "Manager{" + '\'' + super.toString() +
                "idManager=" + idManager +
                ", direccionSucursal='" + direccionSucursal + '\'' +
                '}';
    }


}
