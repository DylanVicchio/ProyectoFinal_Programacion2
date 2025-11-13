import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDate;
import java.util.List;
import Enum.EstadoHabitacion;
import Enum.TipoUsuario;
import Enum.EstadoReserva;
import Exception.DatosInvalidosException;
import Exception.HabitacionNoDisponibleException;
import Exception.ReservaInvalidaException;
import Exception.SeguridadException;


public class HotelManagerE {

    private GestorHotel<Habitacion> gestorHabitaciones;
    private GestorHotel<Pasajero> gestorPasajeros;
    private GestorHotel<Reserva> gestorReservas;
    private GestorHotel<Ocupacion> gestorOcupaciones;
    private GestorHotel<Usuario> gestorUsuarios;

    private Usuario usuarioLogueado;

    public HotelManagerE() {
        this.gestorHabitaciones = new GestorHotel<>("habitaciones.json");
        this.gestorPasajeros = new GestorHotel<>("pasajeros.json");
        this.gestorReservas = new GestorHotel<>("reservas.json");
        this.gestorOcupaciones = new GestorHotel<>("ocupaciones.json");
        this.gestorUsuarios = new GestorHotel<>("usuarios.json");
        this.usuarioLogueado = null;
    }

    // SEGURIDAD Y PERMISOS

    public boolean login(String username, String password) {
        List<Usuario> usuarios = gestorUsuarios.listarTodos();
        for(Usuario u : usuarios) {
            if (u.autenticar(username, password)) {
                this.usuarioLogueado = u;
                System.out.println("Login exitoso. Bienvenido, " + u.getNombre() + " (" + u.getTipoUsuario() + ")");
                return true;
            }
        }
        System.err.println("Error: Usuario o contraseña incorrectos.");
        return false;
    }

    public void logout() {
        this.usuarioLogueado = null;
        System.out.println("Sesión cerrada.");
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    // Chequeos de permisos
    private void checkLogin() throws SeguridadException {
        if (usuarioLogueado == null) {
            throw new SeguridadException("Debe iniciar sesión.");
        }
    }
    private void checkRecepcionista() throws SeguridadException {
        checkLogin();
        if (!(usuarioLogueado instanceof Recepcionista)) {
            throw new SeguridadException("Acción solo para Recepcionistas.");
        }
    }
    private void checkAdmin() throws SeguridadException {
        checkLogin();
        if (!(usuarioLogueado instanceof Administrador)) {
            throw new SeguridadException("Acción solo para Administradores.");
        }
    }

    // Check-In, Check-Out y manejo del corto

    public Reserva realizarReserva(int dniPasajero, int numHabitacion, LocalDate inicio, LocalDate fin)
            throws DatosInvalidosException, HabitacionNoDisponibleException, SeguridadException {

        checkRecepcionista(); // PERMISO

        Pasajero pasajero = gestorPasajeros.buscarPorDni(dniPasajero);
        Habitacion habitacion = gestorHabitaciones.buscarPorId(numHabitacion);

        if (pasajero == null) throw new DatosInvalidosException("Pasajero no encontrado (DNI: " + dniPasajero + ")");
        if (habitacion == null) throw new DatosInvalidosException("Habitación no encontrada (Nro: " + numHabitacion + ")");
        if (inicio.isAfter(fin) || inicio.isBefore(LocalDate.now())) {
            throw new DatosInvalidosException("Fechas inválidas.");
        }

        if (!verificarDisponibilidad(habitacion, inicio, fin)) {
            throw new HabitacionNoDisponibleException("Habitación no disponible para esas fechas.");
        }

        Reserva nuevaReserva = new Reserva(pasajero, inicio, fin, habitacion);
        gestorReservas.agregar(nuevaReserva);
        System.out.println("Reserva creada con éxito (ID: " + nuevaReserva.getId() + ")");
        return nuevaReserva;
    }

    public Ocupacion realizarCheckIn(int idReserva)
            throws ReservaInvalidaException, HabitacionNoDisponibleException, SeguridadException {

        checkRecepcionista(); // PERMISO

        Reserva reserva = gestorReservas.buscarPorId(idReserva);
        if (reserva == null) throw new ReservaInvalidaException("Reserva no encontrada.");

        reserva.confirmarReserva(); // Confirma o lanza excepción

        Habitacion habitacion = reserva.getHabitacionReservada();
        if (!habitacion.estaDisponible() && habitacion.getEstadoHabitacion() != EstadoHabitacion.RESERVADO) {
            throw new HabitacionNoDisponibleException("Habitación no está en condiciones (Ocupada o Mantenimiento).");
        }

        Ocupacion ocupacion = new Ocupacion(habitacion, reserva.getPasajero(), reserva);
        gestorOcupaciones.agregar(ocupacion);

        System.out.println("Check-In exitoso. Ocupación ID: " + ocupacion.getId());
        return ocupacion;
    }

    public Ocupacion realizarCheckOut(int numHabitacion)
            throws DatosInvalidosException, ReservaInvalidaException, SeguridadException {

        checkRecepcionista(); // PERMISO

        Habitacion habitacion = gestorHabitaciones.buscarPorId(numHabitacion);
        if (habitacion == null) throw new DatosInvalidosException("Habitación no encontrada.");

        if (habitacion.getEstadoHabitacion() != EstadoHabitacion.OCUPADO) {
            throw new DatosInvalidosException("La habitación " + numHabitacion + " no figura como ocupada.");
        }

        Ocupacion ocupacionActiva = null;
        List<Ocupacion> ocupaciones = gestorOcupaciones.listarTodos();
        for (Ocupacion o : ocupaciones) {
            if (o.getHabitacion() != null && o.getHabitacion().equals(habitacion) && o.verificarActiva()) {
                ocupacionActiva = o;
                break;
            }
        }

        if (ocupacionActiva == null) throw new DatosInvalidosException("Error interno: No se encontró ocupación activa para la habitación " + numHabitacion);

        ocupacionActiva.finalizarOcupacion();

        System.out.println("Check-Out Exitoso. Habitación " + numHabitacion + ". Monto Total: $" + ocupacionActiva.getMontoPagado());
        return ocupacionActiva;
    }

    private boolean verificarDisponibilidad(Habitacion habitacion, LocalDate inicio, LocalDate fin) {
        if (!habitacion.estaDisponible()) {
            return false;
        }

        List<Reserva> reservas = gestorReservas.listarTodos();
        for (Reserva r : reservas) {
            if (r.getHabitacionReservada() != null && r.getHabitacionReservada().equals(habitacion) &&
                    (r.getEstado() == EstadoReserva.CONFIRMADA || r.getEstado() == EstadoReserva.PENDIENTE)) {

                // Lógica de solapamiento
                if (inicio.isBefore(r.getDiaSalida()) && fin.isAfter(r.getDiaEntrada())) {
                    return false; // Hay solapamiento
                }
            }
        }
        return true;
    }

    // AÑADIDO: Métodos de Admin
    public void crearUsuario(String nombre, String apellido, int numeroCell, int dni, int direccion, String mail, String username, String password, boolean activo, TipoUsuario tipo)
            throws SeguridadException {

        checkAdmin(); // PERMISO

        if (tipo == TipoUsuario.ADMINISTRADOR) {
            gestorUsuarios.agregar(new Administrador(nombre, apellido, numeroCell, dni, direccion, mail, username, password, activo));
        } else if (tipo == TipoUsuario.RECEPCIONISTA) {
            gestorUsuarios.agregar(new Recepcionista(nombre, apellido, numeroCell, dni, direccion, mail, username, password, activo));
        }
        System.out.println("Usuario " + username + " creado.");
    }

    // --- 3. PERSISTENCIA (El Cerebro de Carga/Guardado) ---

    public void guardarDatos() throws SeguridadException {
        checkAdmin(); // PERMISO (Solo Admin hace backup)

        System.out.println("Guardando datos...");
        gestorHabitaciones.guardarEnArchivo();
        gestorPasajeros.guardarEnArchivo();
        gestorReservas.guardarEnArchivo();
        gestorOcupaciones.guardarEnArchivo();
        gestorUsuarios.guardarEnArchivo();
        System.out.println("Datos guardados.");
    }

    public void cargarDatos() {
        System.out.println("Cargando datos...");

        // Limpia la caché interna de los gestores
        gestorHabitaciones.limpiar();
        gestorPasajeros.limpiar();
        gestorReservas.limpiar();
        gestorOcupaciones.limpiar();
        gestorUsuarios.limpiar();

        // 1. Cargar Entidades Base
        cargarHabitaciones();
        cargarPasajeros();
        cargarUsuarios();

        // 2. Cargar Entidades con Dependencias
        cargarReservas();
        cargarOcupaciones();

        // 3. Reconectar Historial de Pasajeros
        reconectarHistorialPasajeros();

        System.out.println("Datos cargados. " + gestorHabitaciones.cantidad() + " habitaciones, " + gestorPasajeros.cantidad() + " pasajeros, " + gestorUsuarios.cantidad() + " usuarios.");
    }

    private void cargarHabitaciones() {
        JSONArray array = JSONUtiles.leerArchivoArray("habitaciones.json");
        for (int i=0; i < array.length(); i++) {
            gestorHabitaciones.agregar(new Habitacion(array.getJSONObject(i)));
        }
    }
    private void cargarPasajeros() {
        JSONArray array = JSONUtiles.leerArchivoArray("pasajeros.json");
        for (int i=0; i < array.length(); i++) {
            gestorPasajeros.agregar(new Pasajero(array.getJSONObject(i)));
        }
    }
    private void cargarUsuarios() {
        JSONArray array = JSONUtiles.leerArchivoArray("usuarios.json");
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            TipoUsuario tipo = TipoUsuario.valueOf(json.getString("tipoUsuario"));

            if (tipo == TipoUsuario.ADMINISTRADOR) {
                gestorUsuarios.agregar(new Administrador(json));
            } else if (tipo == TipoUsuario.RECEPCIONISTA) {
                gestorUsuarios.agregar(new Recepcionista(json));
            }
        }
    }

    private void cargarReservas() {
        JSONArray array = JSONUtiles.leerArchivoArray("reservas.json");
        for (int i=0; i < array.length(); i++) {
            Reserva r = new Reserva(array.getJSONObject(i));
            Pasajero p = gestorPasajeros.buscarPorDni(r.getDniPasajero_json());
            Habitacion h = gestorHabitaciones.buscarPorId(r.getIdHabitacion_json());

            if (p != null && h != null) {
                r.reconectarObjetos(p, h);
                gestorReservas.agregar(r);
            } else {
                System.err.println("No se pudo cargar Reserva ID " + r.getId() + ": Pasajero (DNI " + r.getDniPasajero_json() + ") o Habitación (ID " + r.getIdHabitacion_json() + ") no encontrados.");
            }
        }
    }

    private void cargarOcupaciones() {
        JSONArray array = JSONUtiles.leerArchivoArray("ocupaciones.json");
        for (int i=0; i < array.length(); i++) {
            Ocupacion o = new Ocupacion(array.getJSONObject(i));
            Pasajero p = gestorPasajeros.buscarPorDni(o.getDniPasajero_json());
            Habitacion h = gestorHabitaciones.buscarPorId(o.getIdHabitacion_json());
            Reserva r = (o.getIdReserva_json() != -1) ? gestorReservas.buscarPorId(o.getIdReserva_json()) : null;

            if (p != null && h != null) {
                o.reconectarObjetos(p, h, r);
                gestorOcupaciones.agregar(o);
            } else {
                System.err.println("No se pudo cargar Ocupación ID " + o.getId());
            }
        }
    }

    private void reconectarHistorialPasajeros() {
        List<Pasajero> pasajeros = gestorPasajeros.listarTodos();
        List<Ocupacion> ocupaciones = gestorOcupaciones.listarTodos();

        for (Pasajero p : pasajeros) {
            for (Ocupacion o : ocupaciones) {
                if (o.getDniPasajero_json() == p.getDni() && !o.verificarActiva()) {
                    p.addHistorial(o);
                }
            }
        }
    }
}