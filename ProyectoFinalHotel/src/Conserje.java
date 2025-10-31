public class Conserje extends Persona {
    private String direccionSucursal;
    private int idConserje;
    private EstadoEmpleado e;

    public Conserje(String nombre, String apellido, int numeroCell, int dni, int direccion, String mail, String direccionSucursal, int idConserje, EstadoEmpleado e) {
        super(nombre, apellido, numeroCell, dni, direccion, mail);
        this.direccionSucursal = direccionSucursal;
        this.idConserje = idConserje;
        this.e = e;
    }

    public String getDireccionSucursal() {
        return direccionSucursal;
    }

    public void setDireccionSucursal(String direccionSucursal) {
        this.direccionSucursal = direccionSucursal;
    }

    public int getIdConserje() {
        return idConserje;
    }

    public void setIdConserje(int idConserje) {
        this.idConserje = idConserje;
    }

    public EstadoEmpleado getE() {
        return e;
    }

    public void setE(EstadoEmpleado e) {
        this.e = e;
    }

    @Override
    public String toString() {
        return "Conserje{" + '\'' + super.toString() +
                "direccionSucursal='" + direccionSucursal + '\'' +
                ", idConserje=" + idConserje +
                ", estado=" + e +
                '}';
    }
}
