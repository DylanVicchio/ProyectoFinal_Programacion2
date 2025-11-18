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
        // busca un usuario dentro del gestor
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
        // cierra la sesion del usuario actual
        this.usuarioLogueado = null;
        System.out.println("Sesión cerrada.");
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    // Chequeos de permisos
    private void checkLogin() throws SeguridadException {
        // confirma que haya un usuario
        if (usuarioLogueado == null) {
            throw new SeguridadException("Debe iniciar sesión.");
        }
    }

    private void checkRecepcionista() throws SeguridadException {
        checkLogin(); // confirma que haya un usuario y que sea recepcionista
        if (!(usuarioLogueado instanceof Recepcionista)) {
            throw new SeguridadException("Acción solo para Recepcionistas.");
        }
    }

    private void checkAdmin() throws SeguridadException {
        checkLogin(); // confirma que haya un usuario y que sea recepcionista
        if (!(usuarioLogueado instanceof Administrador)) {
            throw new SeguridadException("Acción solo para Administradores.");
        }
    }

    // Check-In, Check-Out y manejo del corto

    public Reserva realizarReserva(int dniPasajero, int numHabitacion, LocalDate inicio, LocalDate fin)
            throws DatosInvalidosException, HabitacionNoDisponibleException, SeguridadException {

        checkRecepcionista(); // PERMISO A RECEPCIONISTA

        Pasajero pasajero = gestorPasajeros.buscarPorDni(dniPasajero);
        Habitacion habitacion = gestorHabitaciones.buscarPorId(numHabitacion);
        // busca pasajero por dni y habitacion por id y lluego confirma que existan/este disponible
        if (pasajero == null) throw new DatosInvalidosException("Pasajero no encontrado (DNI: " + dniPasajero + ")");
        if (habitacion == null)
            throw new DatosInvalidosException("Habitación no encontrada (Nro: " + numHabitacion + ")");
        if (inicio.isAfter(fin) || inicio.isBefore(LocalDate.now())) {
            throw new DatosInvalidosException("Fechas inválidas.");
        }

        if (!verificarDisponibilidad(habitacion, inicio, fin)) {
            throw new HabitacionNoDisponibleException("Habitación no disponible para esas fechas.");
        }
        // agrega la nueva reserva
        Reserva nuevaReserva = new Reserva(pasajero, inicio, fin, habitacion);
        gestorReservas.agregar(nuevaReserva);
        gestorReservas.guardarEnArchivo();
        System.out.println("Reserva creada con éxito (ID: " + nuevaReserva.getId() + ")");
        return nuevaReserva;
    }

    public Ocupacion realizarCheckIn(int idReserva)
            throws ReservaInvalidaException, HabitacionNoDisponibleException, SeguridadException {

        checkRecepcionista(); // PERMISO PARA RECEPCIONISTA

        Reserva reserva = gestorReservas.buscarPorId(idReserva);
        if (reserva == null) throw new ReservaInvalidaException("Reserva no encontrada.");

        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(reserva.getDiaEntrada())) {
            throw new ReservaInvalidaException(
                    "No se puede hacer check-in antes de la fecha de entrada. " +
                            "Fecha de entrada: " + reserva.getDiaEntrada()
            );
        }

        if (hoy.isAfter(reserva.getDiaSalida())) {
            throw new ReservaInvalidaException(
                    "La reserva ya expiró. Fecha de salida era: " + reserva.getDiaSalida()
            );
        }
        reserva.confirmarReserva(); // Confirma o lanza excepción

        Habitacion habitacion = reserva.getHabitacionReservada(); // confirma que la habitacion este reserva
        if (!habitacion.estaDisponible() && habitacion.getEstadoHabitacion() != EstadoHabitacion.RESERVADO) {
            throw new HabitacionNoDisponibleException("Habitación no está en condiciones (Ocupada o Mantenimiento).");
        }
        // agrega la ocupacion
        Ocupacion ocupacion = new Ocupacion(habitacion, reserva.getPasajero(), reserva);
        gestorOcupaciones.agregar(ocupacion);
        gestorOcupaciones.guardarEnArchivo();
        System.out.println("Check-In exitoso. Ocupación ID: " + ocupacion.getId());
        return ocupacion;
    }

    public Ocupacion realizarCheckOut(int numHabitacion)
            throws DatosInvalidosException, SeguridadException {

        checkRecepcionista(); // PERMISO PARA RECEPCIONISTA

        Habitacion habitacion = gestorHabitaciones.buscarPorId(numHabitacion); // busca habitacion por id y luego pasa por verificaciones
        if (habitacion == null) throw new DatosInvalidosException("Habitación no encontrada.");

        if (habitacion.getEstadoHabitacion() != EstadoHabitacion.OCUPADO) {
            throw new DatosInvalidosException("La habitación " + numHabitacion + " no figura como ocupada.");
        }
        // verifica si la ocupacion esta activa y si tiene la misma habitacion
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
        // finaliza la ocupacion
        ocupacionActiva.finalizarOcupacion();

        System.out.println("Check-Out Exitoso. Habitación " + numHabitacion + ". Monto Total: $" + ocupacionActiva.getMontoPagado());
        gestorOcupaciones.guardarEnArchivo();
        return ocupacionActiva;
    }

    public void agregarConsumo(int numHabitacion, String descripcion, double monto) throws DatosInvalidosException, SeguridadException {

        checkRecepcionista(); // PERMISO PARA RECEPCIONISTA

        // comprueba que lo que llegue por parametro no sea erroneo
        if (descripcion == null || descripcion.trim().isEmpty())
            throw new DatosInvalidosException("Descripcion no puede estar vacia.");

        if (monto <= 0) {
            throw new DatosInvalidosException("Monto precio debe ser mayor a 0.");
        }
        // busca la habitacion y verifica su estado
        Habitacion habitacion = gestorHabitaciones.buscarPorId(numHabitacion);
        if (habitacion == null) {
            throw new DatosInvalidosException("Habitacion no encontrada.");
        }

        if (habitacion.getEstadoHabitacion() != EstadoHabitacion.OCUPADO) {
            throw new DatosInvalidosException("La habitacion no esta ocupada actualmente.");
        }
        // comprueba la habitacion de la ocupacion y la habitacion
        Ocupacion ocupacionActiva = null;
        List<Ocupacion> ocupaciones = gestorOcupaciones.listarTodos();

        for (Ocupacion o : ocupaciones) {
            if (o.getHabitacion() != null && o.getHabitacion().getId() == habitacion.getId() && o.verificarActiva()) {
                ocupacionActiva = o;
                break;
            }
        }
        // comprueba que exista
        if (ocupacionActiva == null) {
            throw new DatosInvalidosException("No se encontro ocuppacion activa para la habitacion " + numHabitacion);
        }
        // agrega un consumo a la ocupacion
        Consumo nuevoConsumo = new Consumo(descripcion, monto);
        ocupacionActiva.agregarConsumos(nuevoConsumo);
        gestorOcupaciones.guardarEnArchivo();
        System.out.println("  Consumo agregado exitosamente");
        System.out.println("  Habitación: " + numHabitacion);
        System.out.println("  Descripción: " + descripcion);
        System.out.println("  Monto: " + monto);
        System.out.println("  Total de consumos en la ocupación: " + ocupacionActiva.getConsumos().size());
    }

    public List<Consumo> listarConsumosDeOcupacion(int numHabitacion)
            throws DatosInvalidosException, SeguridadException {

        checkRecepcionista(); // PERMISO PARA RECEPCIONISTA
        // busca la habitacion y verifica su estado
        Habitacion habitacion = gestorHabitaciones.buscarPorId(numHabitacion);
        if (habitacion == null) {
            throw new DatosInvalidosException("Habitación no encontrada.");
        }
        // comprueba la habitacion de la ocupacion y la habitacion
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
        // comprueba que exista
        if (ocupacionActiva == null) {
            throw new DatosInvalidosException(
                    "No hay ocupación activa en la habitación " + numHabitacion
            );
        }

        List<Consumo> consumos = ocupacionActiva.getConsumos();
        // comprueba que no este vacio y muestra los consumos
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

    public Pasajero registrarPasajero(String nombre, String apellido, int numeroCell, int dni,
                                      int direccion, String mail, String origen, String domicilioOrigen)
            throws DatosInvalidosException, SeguridadException {
        checkRecepcionista(); // PERMISO PARA RECEPCIONISTA

        // verificaciones
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DatosInvalidosException("El nombre no puede estar vacío.");
        }

        if (apellido == null || apellido.trim().isEmpty()) {
            throw new DatosInvalidosException("El apellido no puede estar vacío.");
        }

        if (dni <= 0) {
            throw new DatosInvalidosException("DNI inválido: " + dni);
        }

        if (mail == null || mail.trim().isEmpty() || !mail.contains("@")) {
            throw new DatosInvalidosException("Email inválido.");
        }
        Pasajero pasajeroExistente = gestorPasajeros.buscarPorDni(dni);
        if (pasajeroExistente != null) {
            throw new DatosInvalidosException("Ya existe un pasajero registrado con DNI");
        }

        // crea y agrega pasajero
        Pasajero nuevoPasajero = new Pasajero(nombre, apellido, numeroCell, dni, direccion, mail, origen, domicilioOrigen);
        gestorPasajeros.agregar(nuevoPasajero);
        System.out.println("Pasajero agregado");
        gestorPasajeros.guardarEnArchivo();
        return nuevoPasajero;
    }

    public Pasajero buscarPasajero(int dni) throws SeguridadException {
        checkRecepcionista();

        Pasajero pasajero = gestorPasajeros.buscarPorDni(dni);

        if (pasajero == null) {
            System.out.println("No se encontró pasajero con DNI " + dni);
            return null;
        }

        System.out.println("Informacion del Pasajero: ");
        System.out.println("ID: " + pasajero.getId());
        System.out.println("Nombre: " + pasajero.getNombre() + " " + pasajero.getApellido());
        System.out.println("DNI: " + pasajero.getDni());
        System.out.println("Mail: " + pasajero.getMail());
        System.out.println("Direccion: " + pasajero.getDireccion());
        System.out.println("Origen: " + pasajero.getOrigen());
        System.out.println("Domicilio: " + pasajero.getDomicilioOrigen());
        return pasajero;
    }

    public void listarTodosPasajeros() throws SeguridadException {
        checkRecepcionista();

        List<Pasajero> pasajeros = gestorPasajeros.listarTodos();

        if (pasajeros.isEmpty()) {
            System.out.println("No hay pasajeros registrados en el sistema.");
            return;
        }

        System.out.println("Pasajeros Registrados: ");
        for (Pasajero pasajero : pasajeros) {
            System.out.println("-------------------");
            System.out.println("ID: " + pasajero.getId());
            System.out.println("Nombre: " + pasajero.getNombre() + " " + pasajero.getApellido());
            System.out.println("DNI: " + pasajero.getDni());
            System.out.println("Mail: " + pasajero.getMail());
            System.out.println("Direccion: " + pasajero.getDireccion());
            System.out.println("Origen: " + pasajero.getOrigen());
            System.out.println("Domicilio: " + pasajero.getDomicilioOrigen());
            System.out.println("-------------------");
        }

    }

    public void actualizarPasajero(int dni, String nuevoEmail, int nuevoTelefono,
                                   String nuevaDireccionOrigen)
            throws DatosInvalidosException, SeguridadException {

        checkRecepcionista();
        // busca pasajero y verifica que exista
        Pasajero pasajero = gestorPasajeros.buscarPorDni(dni);

        if (pasajero == null) {
            throw new DatosInvalidosException("No se encontró pasajero");
        }

        // actualizar datos
        if (nuevoEmail != null && !nuevoEmail.trim().isEmpty()) {
            if (!nuevoEmail.contains("@")) {
                throw new DatosInvalidosException("Email inválido");
            }
            pasajero.setMail(nuevoEmail);
        }

        if (nuevoTelefono > 0) {
            pasajero.setNumeroCell(nuevoTelefono);
        } else {
            throw new DatosInvalidosException("Numero invalido");
        }

        if (nuevaDireccionOrigen != null && !nuevaDireccionOrigen.trim().isEmpty()) {
            pasajero.setDomicilioOrigen(nuevaDireccionOrigen);
        } else {
            throw new DatosInvalidosException("Domicilio invalido");
        }
        gestorPasajeros.guardarEnArchivo();

    }

    public void listarReserva() throws SeguridadException{
        checkRecepcionista();

        List<Reserva> reservas = gestorReservas.listarTodos();

        if(reservas.isEmpty()){
            System.out.println("No hay reservas registradas en el sistema.");
            return;
        }

        System.out.println("Reservas registradas");
        for(Reserva reserva : reservas){
            System.out.println("-------------------");
            System.out.println("ID: " + reserva.getId());
            System.out.println("Dia creacion: " + reserva.getDiaCreacion());
            System.out.println("Pasajero: " + reserva.getPasajero().getDni());
            System.out.println("Habitacion reservada: " + reserva.getHabitacionReservada().getId());
            System.out.println("Estado: " + reserva.getEstado().getDescripcion());
            System.out.println("Dia entrada: " + reserva.getDiaEntrada().toString());
            System.out.println("Dia salida: " + reserva.getDiaSalida().toString());
            System.out.println("Monto: " + reserva.getMontoTotal());
            System.out.println("-------------------");
        }
    }

    public Reserva buscarReserva(int id) throws SeguridadException{
        checkRecepcionista();

        Reserva reserva = gestorReservas.buscarPorId(id);

        if (reserva == null) {
            System.out.println("No se encontró la reserva con id: " + id);
            return null;
        }

        System.out.println("Informacion de la reserva: ");
        System.out.println("ID: " + reserva.getId());
        System.out.println("Dia creacion: " + reserva.getDiaCreacion());
        System.out.println("Pasajero: " + reserva.getPasajero().getDni());
        System.out.println("Habitacion reservada: " + reserva.getHabitacionReservada().getId());
        System.out.println("Estado: " + reserva.getEstado().getDescripcion());
        System.out.println("Dia entrada: " + reserva.getDiaEntrada().toString());
        System.out.println("Dia salida: " + reserva.getDiaSalida().toString());
        System.out.println("Monto: " + reserva.getMontoTotal());
        return reserva;
    }

    public void cambiarHabitacionOcupacion(int idHabitacionActual, int idHabitacionNueva)
            throws DatosInvalidosException, HabitacionNoDisponibleException, SeguridadException {

        checkRecepcionista(); // PERMISO PARA RECEPCIONISTA

        Habitacion habitacionActual = gestorHabitaciones.buscarPorId(idHabitacionActual);
        if (habitacionActual == null) {
            throw new DatosInvalidosException("Habitación actual no encontrada (ID: " + idHabitacionNueva + ")");
        }

        if (habitacionActual.getEstadoHabitacion() != EstadoHabitacion.OCUPADO) {
            throw new DatosInvalidosException("La habitación " + idHabitacionNueva + " no está ocupada actualmente.");
        }

        Ocupacion ocupacionActiva = null;
        List<Ocupacion> ocupaciones = gestorOcupaciones.listarTodos();
        for (Ocupacion o : ocupaciones) {
            if (o.getHabitacion().getId() == habitacionActual.getId() && o.verificarActiva()) {
                ocupacionActiva = o;
                break;
            }
        }

        if (ocupacionActiva == null) {
            throw new DatosInvalidosException("No se encontró ocupación activa para la habitación " + idHabitacionActual);
        }

        Habitacion habitacionNueva = gestorHabitaciones.buscarPorId(idHabitacionActual);
        if (habitacionNueva == null) {
            throw new DatosInvalidosException("Habitación nueva no encontrada (Nro: " + idHabitacionNueva  +")");
        }

        if (!habitacionNueva.estaDisponible()) {
            throw new HabitacionNoDisponibleException(
                    "La habitación " + idHabitacionActual + " no está disponible.");
        }

        habitacionActual.setEstadoHabitacion(EstadoHabitacion.LIBRE, "");
        habitacionNueva.setEstadoHabitacion(EstadoHabitacion.OCUPADO, "");
        ocupacionActiva.cambiarHabitacion(habitacionNueva);

        gestorOcupaciones.guardarEnArchivo();
        gestorHabitaciones.guardarEnArchivo();
        System.out.println("Cambio de habitación exitoso");
    }

    public void guardarDatosRecepcionista() throws SeguridadException {
        checkRecepcionista(); // PERMISO PARA RECEPCIONISTA

        gestorPasajeros.guardarEnArchivo();
        gestorReservas.guardarEnArchivo();
        gestorOcupaciones.guardarEnArchivo();
        gestorHabitaciones.guardarEnArchivo();

    }

    public void cambiarEstadoHabitacion(int numHabitacion, EstadoHabitacion nuevoEstado, String motivo)
            throws DatosInvalidosException, SeguridadException {

        checkRecepcionista(); // PERMISO PARA RECEPCIONISTA

        Habitacion habitacion = gestorHabitaciones.buscarPorId(numHabitacion);
        if (habitacion == null) {
            throw new DatosInvalidosException("Habitación no encontrada");
        }

        EstadoHabitacion estadoAnterior = habitacion.getEstadoHabitacion();

        if (estadoAnterior == EstadoHabitacion.OCUPADO &&
                nuevoEstado != EstadoHabitacion.LIBRE) {
            throw new DatosInvalidosException(
                    "No puede cambiar el estado de una habitación ocupada. " +
                            "Primero debe realizar el Check-Out."
            );
        }

        switch (nuevoEstado) {
            case OCUPADO:
                throw new DatosInvalidosException("No puede marcar una habitación como OCUPADA manualmente. Use la función de Check-In.");

            case RESERVADO:
                throw new DatosInvalidosException("No puede marcar una habitación como RESERVADA manualmente. Use la función de Crear Reserva.");

            case LIMPIEZA:
            case MANTENIMIENTO:
            case FUERA_SERVICIO:
                // estos estados requieren un motivo
                if (motivo == null || motivo.trim().isEmpty()) {
                    throw new DatosInvalidosException("Debe especificar un motivo para el estado " + nuevoEstado);
                }
                break;

            case LIBRE:
                // verificar que no haya ocupación y reserva activa
                List<Ocupacion> ocupaciones = gestorOcupaciones.listarTodos();
                for (Ocupacion o : ocupaciones) {
                    if (o.getHabitacion() != null && o.getHabitacion().getId() == habitacion.getId() && o.verificarActiva()) {
                        throw new DatosInvalidosException("No puede marcar como LIBRE una habitación con ocupación activa. Primero debe realizar el Check-Out.");
                    }
                }
                List<Reserva> reservas = gestorReservas.listarTodos();
                for (Reserva r : reservas) {
                    if (r.getHabitacionReservada().getId() == habitacion.getId() &&
                            (r.getEstado() == EstadoReserva.CONFIRMADA ||
                                    r.getEstado() == EstadoReserva.PENDIENTE)) {
                        throw new DatosInvalidosException(
                                "No puede marcar como LIBRE una habitación con reservas pendientes. ID Reserva: " + r.getId()
                        );
                    }
                }
                motivo = "";
                break;
        }

        habitacion.setEstadoHabitacion(nuevoEstado, motivo);
        gestorHabitaciones.guardarEnArchivo();

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
                if (!(fin.isBefore(r.getDiaEntrada()) || inicio.isAfter(r.getDiaSalida()))) {
                    return false; // Hay solapamiento
                }
            }
        }
        return true;
    }

    // Métodos de Admin
    public void crearUsuario(String nombre, String apellido, int numeroCell, int dni, int direccion, String mail, String username, String password, boolean activo, TipoUsuario tipo)
            throws SeguridadException, DatosInvalidosException {

        checkAdmin(); // PERMISO PARA ADMIN

        // comprueba que no haya errores
        if (nombre == null || nombre.trim().isEmpty() ||
                apellido == null || apellido.trim().isEmpty() ||
                username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            throw new DatosInvalidosException("Los campos obligatorios (nombre, apellido, username, password) no pueden estar vacíos.");
        }

        if (dni <= 0) {
            throw new DatosInvalidosException("DNI inválido.");
        }
        if (tipo == null) {
            throw new DatosInvalidosException("Debe especificar un tipo de usuario válido (ADMINISTRADOR o RECEPCIONISTA).");
        }

        if (mail == null || mail.trim().isEmpty() || !mail.contains("@")) {
            throw new DatosInvalidosException("Email inválido.");
        }

        if (gestorUsuarios.existeUsuarioConUsername(username)) {
            throw new DatosInvalidosException("El nombre de usuario '" + username + "' ya está registrado. Elija otro.");
        }

        // verifica si el DNI ya existe
        if (gestorUsuarios.existeUsuarioConDNI(dni)) {
            throw new DatosInvalidosException("Ya existe un usuario con el DNI " + dni + " registrado en el sistema.");
        }

        // crea usuario segun su tipo
        Usuario nuevoUsuario = null;
        if (tipo == TipoUsuario.ADMINISTRADOR) {
             nuevoUsuario = new Administrador(nombre, apellido, numeroCell, dni, direccion, mail, username, password, activo);
        } else if (tipo == TipoUsuario.RECEPCIONISTA) {
            nuevoUsuario = new Recepcionista(nombre, apellido, numeroCell, dni, direccion, mail, username, password, activo);
        }else{
            throw new DatosInvalidosException("Tipo de usuario no compatible con la creación.");
        }
        // agrega usuario
        gestorUsuarios.agregar(nuevoUsuario);
        gestorUsuarios.guardarEnArchivo();
        System.out.println("Usuario " + username + " creado.");
    }

    // Persistencia de datos Archivos JSON

    public void inicializarDatos() {
        System.out.println("Creando datos iniciales controlados...");
        try {
            // 1. Crear el admin "crudo" y agregarlo directamente
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
        checkAdmin(); // PERMISO Solo Admin hace backup

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
        // Limpia los gestores
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