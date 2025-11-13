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
import Enum.TipoHabitacion;


public class HotelManagerE {

    private final GestorHotel<Habitacion> gestorHabitaciones;
    private final GestorHotel<Pasajero> gestorPasajeros;
    private final GestorHotel<Reserva> gestorReservas;
    private final GestorHotel<Ocupacion> gestorOcupaciones;
    private final GestorHotel<Usuario> gestorUsuarios;

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
        for (Usuario u : usuarios) {
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
        if (habitacion == null)
            throw new DatosInvalidosException("Habitación no encontrada (Nro: " + numHabitacion + ")");
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
            throws DatosInvalidosException, SeguridadException {

        checkRecepcionista(); // PERMISO

        Habitacion habitacion = gestorHabitaciones.buscarPorId(numHabitacion);
        if (habitacion == null) throw new DatosInvalidosException("Habitación no encontrada.");

        if (habitacion.getEstadoHabitacion() != EstadoHabitacion.OCUPADO) {
            throw new DatosInvalidosException("La habitación " + numHabitacion + " no figura como ocupada.");
        }

        Ocupacion ocupacionActiva = null;
        List<Ocupacion> ocupaciones = gestorOcupaciones.listarTodos();
        for (Ocupacion o : ocupaciones) {
            if (o.getHabitacion() != null && o.getHabitacion().getId() == habitacion.getId() && o.verificarActiva()) {
                ocupacionActiva = o;
                break;
            }
        }

        if (ocupacionActiva == null)
            throw new DatosInvalidosException("Error interno: No se encontró ocupación activa para la habitación " + numHabitacion);

        ocupacionActiva.finalizarOcupacion();

        System.out.println("Check-Out Exitoso. Habitación " + numHabitacion + ". Monto Total: $" + ocupacionActiva.getMontoPagado());
        return ocupacionActiva;
    }

    public void agregarConsumo(int numHabitacion, String descripcion, double monto) throws DatosInvalidosException, SeguridadException {

        checkRecepcionista();

        if (descripcion == null || descripcion.trim().isEmpty())
            throw new DatosInvalidosException("Descripcion no puede estar vacia.");

        if (monto <= 0) {
            throw new DatosInvalidosException("Monto precio debe ser mayor a 0.");
        }

        Habitacion habitacion = gestorHabitaciones.buscarPorId(numHabitacion);
        if (habitacion == null) {
            throw new DatosInvalidosException("Habitacion no encontrada.");
        }

        if (habitacion.getEstadoHabitacion() != EstadoHabitacion.OCUPADO) {
            throw new DatosInvalidosException("La habitacion no esta ocupada actualmente.");
        }

        Ocupacion ocupacionActiva = null;
        List<Ocupacion> ocupaciones = gestorOcupaciones.listarTodos();

        for (Ocupacion o : ocupaciones) {
            if (o.getHabitacion() != null && o.getHabitacion().getId() == habitacion.getId() && o.verificarActiva()) {
                ocupacionActiva = o;
                break;
            }
        }

        if (ocupacionActiva == null) {
            throw new DatosInvalidosException("No se encontro ocuppacion activa para la habitacion " + numHabitacion);
        }

        Consumo nuevoConsumo = new Consumo(descripcion, monto);
        ocupacionActiva.agregarConsumos(nuevoConsumo);

        System.out.println("  Consumo agregado exitosamente");
        System.out.println("  Habitación: " + numHabitacion);
        System.out.println("  Descripción: " + descripcion);
        System.out.println("  Monto: " + monto);
        System.out.println("  Total de consumos en la ocupación: " + ocupacionActiva.getConsumos().size());
    }

    public List<Consumo> listarConsumosDeOcupacion(int numHabitacion)
            throws DatosInvalidosException, SeguridadException {

        checkRecepcionista();

        Habitacion habitacion = gestorHabitaciones.buscarPorId(numHabitacion);
        if (habitacion == null) {
            throw new DatosInvalidosException("Habitación no encontrada.");
        }

        Ocupacion ocupacionActiva = null;
        List<Ocupacion> ocupaciones = gestorOcupaciones.listarTodos();

        for (Ocupacion o : ocupaciones) {
            if (o.getHabitacion() != null &&
                    o.getHabitacion().getId() == habitacion.getId() &&
                    o.verificarActiva()) {
                ocupacionActiva = o;
                break;
            }
        }

        if (ocupacionActiva == null) {
            throw new DatosInvalidosException(
                    "No hay ocupación activa en la habitación " + numHabitacion
            );
        }

        List<Consumo> consumos = ocupacionActiva.getConsumos();

        if (consumos.isEmpty()) {
            System.out.println("No hay consumos registrados para esta ocupación.");
        } else {
            System.out.println("\n=== Consumos de Habitación " + numHabitacion + " ===");
            double total = 0;
            for (Consumo c : consumos) {
                System.out.println("  - " + c.getDescripcion() + ": " + c.getMonto() +
                        " (" + c.getFecha().toLocalDate() + ")");
                total += c.getMonto();
            }
            System.out.println("  TOTAL CONSUMOS: " + total);
            System.out.println("=======================================\n");
        }

        return consumos;
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

    // Métodos de Admin
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

    // Persistencia de datos Archivos JSON

    public void inicializarDatos() {
        System.out.println("Creando datos iniciales controlados...");
        try {
            // 1. Crear el admin "crudo" y agregarlo directamente (es una acción interna permitida)
            Administrador adminTemporal = new Administrador("Admin", "Root", 123, 1, 123, "admin@hotel.com", "admin", "admin", true);
            this.gestorUsuarios.agregar(adminTemporal);

            // 2. Loguearse para que las demás operaciones de creación tengan permiso
            login("admin", "admin");

            // 3. Crear Habitaciones (No requiere permiso)
            gestorHabitaciones.agregar(new Habitacion(101, TipoHabitacion.INDIVIDUAL, 1));
            gestorHabitaciones.agregar(new Habitacion(102, TipoHabitacion.INDIVIDUAL, 1));
            gestorHabitaciones.agregar(new Habitacion(201, TipoHabitacion.DOBLE, 2));
            gestorHabitaciones.agregar(new Habitacion(301, TipoHabitacion.SUITE, 3));

            // 4. Crear Pasajeros (No requiere permiso)
            gestorPasajeros.agregar(new Pasajero("Juan", "Perez", 112233, 30123456, 123, "juan@mail.com", "Argentina", "Calle Falsa 123"));
            gestorPasajeros.agregar(new Pasajero("Maria", "Gomez", 445566, 32654987, 456, "maria@mail.com", "Chile", "Av. Siempre Viva 742"));

            // 5. Crear Recepcionista (Requiere permiso, por eso el login anterior)
            crearUsuario("Recepcionista", "Uno", 778899, 40111222, 789, "recep@hotel.com", "recep", "recep", true, TipoUsuario.RECEPCIONISTA);

            // 6. Guardar y cerrar la sesión temporal
            guardarDatos();
            logout();

        } catch (Exception e) {
            System.err.println("Error fatal creando datos iniciales: " + e.getMessage());
        }
    }

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

    public boolean cargarDatos() {
        System.out.println("Cargando datos...");
        boolean cargado = false;
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

        if (gestorUsuarios.cantidad() > 0) {
            cargado = true;
        }


        // 2. Cargar Entidades con Dependencias
        if (cargado) {
            cargarReservas();
            cargarOcupaciones();

            // 3. Reconectar Historial de Pasajeros
            reconectarHistorialPasajeros();
        }

        System.out.println("Datos cargados. " + gestorHabitaciones.cantidad() + " habitaciones, " + gestorPasajeros.cantidad() + " pasajeros, " + gestorUsuarios.cantidad() + " usuarios.");

        return cargado;
    }

    // 1.Carga de habitaciones
    private void cargarHabitaciones() {
        JSONArray array = JSONUtiles.leerArchivoArray("habitaciones.json");
        for (int i = 0; i < array.length(); i++) {
            gestorHabitaciones.agregar(new Habitacion(array.getJSONObject(i)));
        }
    }

    // 2.Carga de pasajeros
    private void cargarPasajeros() {
        JSONArray array = JSONUtiles.leerArchivoArray("pasajeros.json");
        for (int i = 0; i < array.length(); i++) {
            gestorPasajeros.agregar(new Pasajero(array.getJSONObject(i)));
        }
    }

    // 3. Carga de usuarios
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

    // 4. Carga de reservas
    private void cargarReservas() {
        JSONArray array = JSONUtiles.leerArchivoArray("reservas.json");
        for (int i = 0; i < array.length(); i++) {
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

    // 5. Carga de ocupaciones
    private void cargarOcupaciones() {
        JSONArray array = JSONUtiles.leerArchivoArray("ocupaciones.json");
        for (int i = 0; i < array.length(); i++) {
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

    // 6. Reconecta ocupaciones a pasajeros
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