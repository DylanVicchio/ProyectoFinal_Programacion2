import java.time.LocalDate;
import java.util.Scanner;
import Enum.TipoUsuario;
import Exception.DatosInvalidosException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    private static void crearDatosIniciales(HotelManagerE manager) {
        System.out.println("No se encontraron datos. Iniciando configuración...");
        manager.inicializarDatos();
    }

    public static void main(String[] args) {

        HotelManagerE manager = new HotelManagerE();

        if(!manager.cargarDatos()){
            System.out.println("No se encontraron datos persistentes.");
            crearDatosIniciales(manager);

            System.out.println("Sistema inicializado. Vuelva a ejecutar para cargar los datos.");
            return;
        }

        System.out.println("Datos cargados correctamente. Iniciando sesión.");
        menuLogin(manager);

    }

    private static void menuLogin(HotelManagerE manager) {
        Scanner scanner = new Scanner(System.in);
        while(true) {
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
                        manager.guardarDatos();
                        break;
                    case "2":
                        crearNuevoUsuario(manager, scanner);
                        break;
                    case "3":
                        manager.logout();
                        System.out.println("Sesión cerrada.");
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
        while (!opcion.equals("4")) {
            System.out.println("\n--- MENÚ RECEPCIONISTA ---");
            System.out.println("1. Realizar Reserva");
            System.out.println("2. Realizar Check-In");
            System.out.println("3. Realizar Check-Out");
            System.out.println("4. Logout");
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
                        manager.logout();
                        System.out.println("Sesión cerrada.");
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
        System.out.println( "\n(Formato DD/MM/AAAA): ");
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