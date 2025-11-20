import java.time.LocalDate;
import java.util.Scanner;

import Enum.TipoUsuario;
import Exception.DatosInvalidosException;
import Enum.EstadoHabitacion;

public class Main {

    private static void crearDatosIniciales(HotelManagerE manager) {
        System.out.println("No se encontraron datos. Iniciando configuración...");
        manager.inicializarDatos();
    }

    public static void main(String[] args) {

        HotelManagerE manager = new HotelManagerE();

        if (!manager.cargarDatos()) {
            System.out.println("No se encontraron datos persistentes.");
            crearDatosIniciales(manager);

            System.out.println("Sistema inicializado. Vuelva a ejecutar para cargar los datos.");
            return;
            // Primera ejecucion
        }

        System.out.println("Datos cargados correctamente. Iniciando sesión.");
        menuLogin(manager); // Menu de Inicio de Sesion

    }

    private static void menuLogin(HotelManagerE manager) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- BIENVENIDO AL SISTEMA DEL HOTEL ---");
            System.out.println("Ingrese username (o 'salir'):");
            String user = scanner.nextLine();
            if (user.equalsIgnoreCase("salir")) break;

            System.out.println("Ingrese password:");
            String pass = scanner.nextLine();

            if (manager.login(user, pass)) {
                if (manager.getUsuarioLogueado() instanceof Administrador) {
                    menuAdministrador(manager, scanner);
                } else if (manager.getUsuarioLogueado() instanceof Recepcionista) {
                    menuRecepcionista(manager, scanner);
                }
            }
        }
        System.out.println("Saliendo.");
        scanner.close();
    }

    private static void menuAdministrador(HotelManagerE manager, Scanner scanner) {
        String opcion = "";
        while (!opcion.equals("3")) {
            System.out.println("\n--- MENÚ ADMINISTRADOR ---");
            System.out.println("1. Realizar Backup (Guardar Datos)");
            System.out.println("2. Crear Nuevo Usuario");
            System.out.println("3. Logout");
            System.out.print("Opción: ");
            opcion = scanner.nextLine();

            try {
                switch (opcion) {
                    case "1":
                        manager.guardarDatos(); // guarda los datos en JSON
                        break;
                    case "2":
                        crearNuevoUsuario(manager, scanner); // crea un Usuario
                        break;
                    case "3":
                        manager.logout();
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (Exception e) {
                System.err.println("ERROR (Admin): " + e.getMessage());
            }
        }
    }

    private static void crearNuevoUsuario(HotelManagerE manager, Scanner scanner) throws Exception {
        System.out.println("\n--- CREAR NUEVO USUARIO ---");

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Apellido: ");
        String apellido = scanner.nextLine();

        System.out.print("DNI: ");
        int dni = Integer.parseInt(scanner.nextLine());

        System.out.print("Número de Celular: ");
        int numCel = Integer.parseInt(scanner.nextLine());

        System.out.print("Dirección (Número): ");
        int dirNum = Integer.parseInt(scanner.nextLine());

        System.out.print("Mail: ");
        String mail = scanner.nextLine();
        System.out.print("Username: ");
        String user = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();

        System.out.println("Tipo de Usuario (1: ADMINISTRADOR, 2: RECEPCIONISTA):");
        String tipoOpcion = scanner.nextLine();

        TipoUsuario tipo = null;

        if (tipoOpcion.equals("1")) {
            tipo = TipoUsuario.ADMINISTRADOR;
        } else if (tipoOpcion.equals("2")) {
            tipo = TipoUsuario.RECEPCIONISTA;
        } else {
            throw new DatosInvalidosException("Opción de tipo de usuario ('" + tipoOpcion + "') no es válida. Debe ser '1' o '2'.");
        }

        manager.crearUsuario(nombre, apellido, numCel, dni, dirNum, mail, user, pass, true, tipo);
        System.out.println("Usuario " + user + " (" + tipo.name() + ") creado con éxito.");
    }

    private static void menuRecepcionista(HotelManagerE manager, Scanner scanner) {
        String opcion = "";
        while (!opcion.equals("18")) {
            System.out.println("\n--- MENÚ RECEPCIONISTA ---");
            System.out.println("1. Realizar Reserva");
            System.out.println("2. Realizar Check-In");
            System.out.println("3. Realizar Check-Out");
            System.out.println("4. Agregar Consumo");
            System.out.println("5. Listar Consumo de ocupacion");
            System.out.println("6. Registrar nuevo Pasajero");
            System.out.println("7. Buscar pasajero por DNI");
            System.out.println("8. Listar todos los pasajeros");
            System.out.println("9. Actualizar Pasajero");
            System.out.println("10. Listar Reservas");
            System.out.println("11. Buscar Reserva por ID");
            System.out.println("12. Listar Ocupaciones");
            System.out.println("13. Buscar Ocupación por ID");
            System.out.println("14. Listar Habitaciones");
            System.out.println("15. Cambiar Estado de Habitación");
            System.out.println("16. Cambiar Habitación de Ocupación");
            System.out.println("17. Guardar Datos");
            System.out.println("18. Salir");

            System.out.print("Opción: ");
            opcion = scanner.nextLine();

            try {
                switch (opcion) {
                    case "1":
                        System.out.print("Ingrese DNI Pasajero: ");
                        int dni = Integer.parseInt(scanner.nextLine());
                        System.out.print("Ingrese ID Habitación: ");
                        int hab = Integer.parseInt(scanner.nextLine());

                        System.out.println("Ingrese FECHA DE INICIO: ");
                        LocalDate inicio = pedirFecha(scanner);
                        System.out.println("Ingrese FECHA DE FINAL: ");
                        LocalDate fin = pedirFecha(scanner);
                        manager.realizarReserva(dni, hab, inicio, fin);
                        break;
                    case "2":
                        System.out.print("Ingrese ID de Reserva: ");
                        int idRes = Integer.parseInt(scanner.nextLine());
                        manager.realizarCheckIn(idRes);
                        break;
                    case "3":
                        System.out.print("Ingrese ID Habitación para Check-Out: ");
                        int habOut = Integer.parseInt(scanner.nextLine());
                        manager.realizarCheckOut(habOut);
                        break;
                    case "4":
                        System.out.print("Ingrese ID Habitación: ");
                        int habConsumo = Integer.parseInt(scanner.nextLine());

                        System.out.print("Descripción del consumo (ej: Minibar, Room Service): ");
                        String descripcion = scanner.nextLine();

                        System.out.print("Monto: ");
                        double monto = Double.parseDouble(scanner.nextLine());

                        manager.agregarConsumo(habConsumo, descripcion, monto);
                        break;
                    case "5":
                        System.out.print("Ingrese ID Habitación: ");
                        int habID = Integer.parseInt(scanner.nextLine());
                        manager.listarConsumosDeOcupacion(habID);
                        break;
                    case "6":
                        System.out.println("\nREGISTRAR NUEVO PASAJERO");
                        System.out.print("Nombre: ");
                        String nombre = scanner.nextLine();
                        System.out.print("Apellido: ");
                        String apellido = scanner.nextLine();
                        System.out.print("DNI: ");
                        int dni1 = Integer.parseInt(scanner.nextLine());
                        System.out.print("Número de Celular: ");
                        int numCell = Integer.parseInt(scanner.nextLine());
                        System.out.print("Dirección (Número): ");
                        int dir = Integer.parseInt(scanner.nextLine());
                        System.out.print("Email: ");
                        String mail = scanner.nextLine();
                        System.out.print("Origen (Ciudad/País): ");
                        String origen = scanner.nextLine();
                        System.out.print("Domicilio de Origen: ");
                        String domOrigen = scanner.nextLine();

                        manager.registrarPasajero(nombre, apellido,numCell,dni1,dir,mail,origen,domOrigen);
                        break;
                    case "7":
                        System.out.print("\nIngrese DNI del pasajero: ");
                        int dniBuscar = Integer.parseInt(scanner.nextLine());
                        manager.buscarPasajero(dniBuscar);
                        break;
                    case "8":
                        manager.listarTodosPasajeros();
                        break;
                    case "9":
                        System.out.print("\nIngrese DNI del pasajero: ");
                        int dniBuscado = Integer.parseInt(scanner.nextLine());
                        System.out.print("Ingrese nuevo mail: ");
                        String nuevoMail = scanner.nextLine();
                        System.out.print("Ingrese nuevo telefono: ");
                        int nuevoTelefono = Integer.parseInt(scanner.nextLine());
                        System.out.print("Nueva domicilio: ");
                        String nuevaDireccion = scanner.nextLine();
                        manager.actualizarPasajero(dniBuscado, nuevoMail, nuevoTelefono, nuevaDireccion );
                        break;
                    case "10":
                        manager.listarReserva();
                        break;

                    case "11":
                        System.out.print("\nIngrese ID de la reserva: ");
                        int idReserva = Integer.parseInt(scanner.nextLine());
                        manager.buscarReserva(idReserva);
                        break;

                    case "12":
                        manager.listarOcupaciones();
                        break;

                    case "13":
                        System.out.print("\nIngrese ID de la ocupación: ");
                        int idOcupacion = Integer.parseInt(scanner.nextLine());
                        manager.buscarOcupacion(idOcupacion);
                        break;

                    case "14":
                        manager.listarHabitaciones();
                        break;

                    case "15":
                        System.out.print("\nIngrese ID de habitación: ");
                        int idBuscado = Integer.parseInt(scanner.nextLine());
                        System.out.println("\nSeleccione el nuevo estado:");
                        System.out.println("  1. LIBRE (Disponible)");
                        System.out.println("  2. LIMPIEZA");
                        System.out.println("  3. MANTENIMIENTO");
                        System.out.println("  4. FUERA_SERVICIO");
                        System.out.print("Opción de Estado: ");
                        String optEstado = scanner.nextLine();

                        EstadoHabitacion nuevoEstado = null;
                        String motivo = "";

                        switch (optEstado) {
                            case "1":
                                nuevoEstado = EstadoHabitacion.LIBRE;
                                motivo = "";
                                break;
                            case "2":
                                nuevoEstado = EstadoHabitacion.LIMPIEZA;
                                System.out.print("Ingrese motivo de limpieza: ");
                                motivo = scanner.nextLine();
                                break;
                            case "3":
                                nuevoEstado = EstadoHabitacion.MANTENIMIENTO;
                                System.out.print("Ingrese motivo de mantenimiento: ");
                                motivo = scanner.nextLine();
                                break;
                            case "4":
                                nuevoEstado = EstadoHabitacion.FUERA_SERVICIO;
                                System.out.print("Ingrese motivo de fuera de servicio: ");
                                motivo = scanner.nextLine();
                                break;
                            default:
                                throw new DatosInvalidosException("Opción de estado inválida.");
                        }
                        manager.cambiarEstadoHabitacion(idBuscado, nuevoEstado, motivo);
                        System.out.println("Estado de la habitación " + idBuscado + " cambiado exitosamente.");
                        break;

                    case "16":
                        System.out.println("\n--- CAMBIAR HABITACIÓN DE OCUPACIÓN ---");
                        System.out.print("Ingrese ID de habitación ACTUAL (ocupada): ");
                        int habActual = Integer.parseInt(scanner.nextLine());
                        System.out.print("Ingrese ID de habitación NUEVA (destino): ");
                        int habNueva = Integer.parseInt(scanner.nextLine());
                        manager.cambiarHabitacionOcupacion(habActual, habNueva);
                        break;

                    case "17":
                        manager.guardarDatosRecepcionista();
                        System.out.println("Datos guardados exitosamente.");
                        break;

                    case "18":
                        try {
                            System.out.println("\nGuardando cambios antes de cerrar sesión...");
                            manager.guardarDatosRecepcionista();
                        } catch (Exception e) {
                            System.err.println("Error al guardar: " + e.getMessage());
                            System.out.print("¿Desea cerrar sesión de todos modos? (S/N): ");
                            String respuesta = scanner.nextLine();
                            if (!respuesta.equalsIgnoreCase("S")) {
                                continue;
                            }
                        }
                        manager.logout();
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Debe ingresar un número válido.");
            } catch (Exception e) {
                System.out.println("ERROR (Recep): " + e.getMessage());
            }
        }
    }

    private static LocalDate pedirFecha(Scanner scanner) throws DatosInvalidosException {
        System.out.println("\n(Formato DD/MM/AAAA): ");
        try {
            System.out.print("  Día (DD): ");
            int dia = Integer.parseInt(scanner.nextLine());
            System.out.print("  Mes (MM): ");
            int mes = Integer.parseInt(scanner.nextLine());
            System.out.print("  Año (AAAA): ");
            int anio = Integer.parseInt(scanner.nextLine());

            return LocalDate.of(anio, mes, dia);
        } catch (NumberFormatException e) {
            throw new DatosInvalidosException("El formato de la fecha es incorrecto (día, mes y año deben ser números).");
        } catch (Exception e) {
            throw new DatosInvalidosException("La fecha ingresada no es una fecha válida o lógica.");
        }
    }
}