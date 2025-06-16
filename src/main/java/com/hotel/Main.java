package com.hotel;

import com.hotel.service.AuthService;
import com.hotel.service.RoomService;
import com.hotel.service.ReservationService;
import com.hotel.service.ReportService;
import com.hotel.model.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Map;

public class Main {
    private static final List<String> COMANDOS = Arrays.asList(
        "iniciar-sesion", "registrar", "salir",
        "ver-habitaciones", "hacer-reserva", "cancelar-reserva",
        "ver-reservas", "gestionar-habitaciones", "ver-reportes"
    );

    public static void main(String[] args) {
        try {
            Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();

            LineReader lector = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(new StringsCompleter(COMANDOS))
                .build();

            System.out.println("Bienvenido al Sistema de Reservas del Hotel");
            System.out.println("==========================================");

            while (true) {
                String linea = lector.readLine("hotel> ");
                if (linea == null || linea.equalsIgnoreCase("salir")) {
                    break;
                }

                procesarComando(linea.trim());
            }

        } catch (IOException e) {
            System.err.println("Error al inicializar la terminal: " + e.getMessage());
        }
    }

    private static void procesarComando(String comando) {
        String[] partes = comando.split("\\s+");
        String cmd = partes[0].toLowerCase();

        try {
            switch (cmd) {
                case "iniciar-sesion":
                    manejarInicioSesion();
                    break;
                case "registrar":
                    manejarRegistro();
                    break;
                case "ver-habitaciones":
                    if (AuthService.isAuthenticated()) {
                        manejarVerHabitaciones();
                    } else {
                        System.out.println("Debe iniciar sesión primero.");
                    }
                    break;
                case "hacer-reserva":
                    if (AuthService.isAuthenticated()) {
                        manejarHacerReserva();
                    } else {
                        System.out.println("Debe iniciar sesión primero.");
                    }
                    break;
                case "cancelar-reserva":
                    if (AuthService.isAuthenticated()) {
                        manejarCancelarReserva();
                    } else {
                        System.out.println("Debe iniciar sesión primero.");
                    }
                    break;
                case "ver-reservas":
                    if (AuthService.isAuthenticated()) {
                        manejarVerReservas();
                    } else {
                        System.out.println("Debe iniciar sesión primero.");
                    }
                    break;
                case "gestionar-habitaciones":
                    if (AuthService.hasRole("MANAGER")) {
                        manejarGestionHabitaciones();
                    } else {
                        System.out.println("No tiene permisos para esta operación.");
                    }
                    break;
                case "ver-reportes":
                    if (AuthService.hasRole("MANAGER")) {
                        manejarVerReportes();
                    } else {
                        System.out.println("No tiene permisos para esta operación.");
                    }
                    break;
                default:
                    System.out.println("Comando no reconocido. Comandos disponibles:");
                    COMANDOS.forEach(c -> System.out.println("  - " + c));
            }
        } catch (SQLException e) {
            System.err.println("Error en la operación: " + e.getMessage());
        }
    }

    private static void manejarInicioSesion() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Usuario: ");
        String usuario = scanner.nextLine();
        System.out.print("Contraseña: ");
        String contraseña = scanner.nextLine();

        User usuarioActual = AuthService.login(usuario, contraseña);
        if (usuarioActual != null) {
            System.out.println("Bienvenido, " + usuarioActual.getUsername() + "!");
        } else {
            System.out.println("Credenciales inválidas.");
        }
    }

    private static void manejarRegistro() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Usuario: ");
        String usuario = scanner.nextLine();
        System.out.print("Contraseña: ");
        String contraseña = scanner.nextLine();
        System.out.print("Correo electrónico: ");
        String correo = scanner.nextLine();
        System.out.print("Rol (CLIENTE/EMPLEADO/GERENTE): ");
        String rol = scanner.nextLine().toUpperCase();

        User nuevoUsuario = AuthService.register(usuario, contraseña, correo, rol);
        if (nuevoUsuario != null) {
            System.out.println("Usuario registrado exitosamente.");
        } else {
            System.out.println("Error al registrar usuario.");
        }
    }

    private static void manejarVerHabitaciones() throws SQLException {
        List<Room> habitaciones = RoomService.getAvailableRooms();
        System.out.println("\nHabitaciones Disponibles:");
        System.out.println("========================");
        for (Room habitacion : habitaciones) {
            System.out.printf("Habitación %s - Tipo: %s - Capacidad: %d - Precio: %.2f\n",
                habitacion.getRoomNumber(), habitacion.getRoomType(), 
                habitacion.getCapacity(), habitacion.getPricePerNight());
            if (habitacion.getFeatures() != null) {
                System.out.println("Características:");
                for (RoomFeature caracteristica : habitacion.getFeatures()) {
                    System.out.printf("  - %s: %s\n", 
                        caracteristica.getFeatureName(), caracteristica.getFeatureValue());
                }
            }
            System.out.println();
        }
    }

    private static void manejarHacerReserva() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Fecha de entrada (YYYY-MM-DD): ");
        LocalDate fechaEntrada = LocalDate.parse(scanner.nextLine());
        System.out.print("Fecha de salida (YYYY-MM-DD): ");
        LocalDate fechaSalida = LocalDate.parse(scanner.nextLine());
        System.out.print("Número de huéspedes: ");
        int numeroHuespedes = Integer.parseInt(scanner.nextLine());

        // Mostrar habitaciones disponibles
        List<Room> habitacionesDisponibles = RoomService.getAvailableRooms();
        System.out.println("\nHabitaciones disponibles para las fechas seleccionadas:");
        for (Room habitacion : habitacionesDisponibles) {
            System.out.printf("%d. Habitación %s - Tipo: %s - Capacidad: %d - Precio: %.2f\n",
                habitacion.getRoomId(), habitacion.getRoomNumber(), 
                habitacion.getRoomType(), habitacion.getCapacity(), 
                habitacion.getPricePerNight());
        }

        System.out.print("\nSeleccione el ID de la habitación: ");
        int idHabitacion = Integer.parseInt(scanner.nextLine());

        // Crear la reserva
        Reservation reserva = new Reservation();
        reserva.setClientId(AuthService.getCurrentUser().getUserId());
        reserva.setCheckInDate(fechaEntrada);
        reserva.setCheckOutDate(fechaSalida);
        reserva.setNumberOfGuests(numeroHuespedes);
        reserva.setStatus("PENDIENTE");

        // Agregar detalles de la reserva
        Room habitacionSeleccionada = RoomService.getRoomById(idHabitacion);
        ReservationDetail detalle = new ReservationDetail();
        detalle.setRoomId(idHabitacion);
        detalle.setPricePerNight(habitacionSeleccionada.getPricePerNight());
        reserva.getDetails().add(detalle);

        // Calcular precio total
        long dias = java.time.temporal.ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
        reserva.setTotalPrice(habitacionSeleccionada.getPricePerNight()
            .multiply(new java.math.BigDecimal(dias)));

        Reservation nuevaReserva = ReservationService.createReservation(reserva);
        if (nuevaReserva != null) {
            System.out.println("Reserva creada exitosamente.");
        } else {
            System.out.println("Error al crear la reserva.");
        }
    }

    private static void manejarCancelarReserva() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("ID de la reserva a cancelar: ");
        int idReserva = Integer.parseInt(scanner.nextLine());

        if (ReservationService.cancelReservation(idReserva)) {
            System.out.println("Reserva cancelada exitosamente.");
        } else {
            System.out.println("Error al cancelar la reserva.");
        }
    }

    private static void manejarVerReservas() throws SQLException {
        List<Reservation> reservas;
        if (AuthService.hasRole("EMPLOYEE") || AuthService.hasRole("MANAGER")) {
            reservas = ReservationService.getAllReservations();
        } else {
            reservas = ReservationService.getReservationsByClient(
                AuthService.getCurrentUser().getUserId());
        }

        System.out.println("\nReservas:");
        System.out.println("=========");
        for (Reservation reserva : reservas) {
            System.out.printf("ID: %d - Entrada: %s - Salida: %s - Estado: %s\n",
                reserva.getReservationId(),
                reserva.getCheckInDate(),
                reserva.getCheckOutDate(),
                reserva.getStatus());
            System.out.println("Habitaciones:");
            for (ReservationDetail detalle : reserva.getDetails()) {
                try {
                    Room habitacion = RoomService.getRoomById(detalle.getRoomId());
                    System.out.printf("  - Habitación %s - Precio por noche: %.2f\n",
                        habitacion.getRoomNumber(), detalle.getPricePerNight());
                } catch (SQLException e) {
                    System.out.println("  - Error al obtener detalles de la habitación");
                }
            }
            System.out.println();
        }
    }

    private static void manejarGestionHabitaciones() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nGestión de Habitaciones");
        System.out.println("1. Agregar habitación");
        System.out.println("2. Modificar habitación");
        System.out.println("3. Eliminar habitación");
        System.out.print("Seleccione una opción: ");
        
        int opcion = Integer.parseInt(scanner.nextLine());
        switch (opcion) {
            case 1:
                manejarAgregarHabitacion();
                break;
            case 2:
                manejarModificarHabitacion();
                break;
            case 3:
                manejarEliminarHabitacion();
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    private static void manejarAgregarHabitacion() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        Room habitacion = new Room();
        
        System.out.print("Número de habitación: ");
        habitacion.setRoomNumber(scanner.nextLine());
        System.out.print("Tipo de habitación: ");
        habitacion.setRoomType(scanner.nextLine());
        System.out.print("Capacidad: ");
        habitacion.setCapacity(Integer.parseInt(scanner.nextLine()));
        System.out.print("Precio por noche: ");
        habitacion.setPricePerNight(new java.math.BigDecimal(scanner.nextLine()));
        System.out.print("Descripción: ");
        habitacion.setDescription(scanner.nextLine());
        habitacion.setStatus("DISPONIBLE");

        if (RoomService.addRoom(habitacion)) {
            System.out.println("Habitación agregada exitosamente.");
        } else {
            System.out.println("Error al agregar la habitación.");
        }
    }

    private static void manejarModificarHabitacion() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("ID de la habitación a modificar: ");
        int idHabitacion = Integer.parseInt(scanner.nextLine());

        Room habitacion = RoomService.getRoomById(idHabitacion);
        if (habitacion == null) {
            System.out.println("Habitación no encontrada.");
            return;
        }

        System.out.print("Nuevo número de habitación [" + habitacion.getRoomNumber() + "]: ");
        String numeroHabitacion = scanner.nextLine();
        if (!numeroHabitacion.isEmpty()) habitacion.setRoomNumber(numeroHabitacion);

        System.out.print("Nuevo tipo de habitación [" + habitacion.getRoomType() + "]: ");
        String tipoHabitacion = scanner.nextLine();
        if (!tipoHabitacion.isEmpty()) habitacion.setRoomType(tipoHabitacion);

        System.out.print("Nueva capacidad [" + habitacion.getCapacity() + "]: ");
        String capacidad = scanner.nextLine();
        if (!capacidad.isEmpty()) habitacion.setCapacity(Integer.parseInt(capacidad));

        System.out.print("Nuevo precio por noche [" + habitacion.getPricePerNight() + "]: ");
        String precio = scanner.nextLine();
        if (!precio.isEmpty()) habitacion.setPricePerNight(new java.math.BigDecimal(precio));

        System.out.print("Nueva descripción [" + habitacion.getDescription() + "]: ");
        String descripcion = scanner.nextLine();
        if (!descripcion.isEmpty()) habitacion.setDescription(descripcion);

        if (RoomService.updateRoom(habitacion)) {
            System.out.println("Habitación actualizada exitosamente.");
        } else {
            System.out.println("Error al actualizar la habitación.");
        }
    }

    private static void manejarEliminarHabitacion() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("ID de la habitación a eliminar: ");
        int idHabitacion = Integer.parseInt(scanner.nextLine());

        if (RoomService.deleteRoom(idHabitacion)) {
            System.out.println("Habitación eliminada exitosamente.");
        } else {
            System.out.println("Error al eliminar la habitación.");
        }
    }

    private static void manejarVerReportes() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nReportes Disponibles");
        System.out.println("1. Estadísticas de ocupación");
        System.out.println("2. Ingresos por mes");
        System.out.println("3. Reservas de clientes");
        System.out.println("4. Habitaciones disponibles");
        System.out.print("Seleccione una opción: ");
        
        int opcion = Integer.parseInt(scanner.nextLine());
        switch (opcion) {
            case 1:
                mostrarEstadisticasOcupacion();
                break;
            case 2:
                mostrarIngresosPorMes();
                break;
            case 3:
                mostrarReservasClientes();
                break;
            case 4:
                mostrarHabitacionesDisponibles();
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    private static void mostrarEstadisticasOcupacion() throws SQLException {
        Map<String, Object> estadisticas = ReportService.getRoomOccupancyStats();
        List<Map<String, Object>> estadisticasHabitaciones = 
            (List<Map<String, Object>>) estadisticas.get("roomStats");
        
        System.out.println("\nEstadísticas de Ocupación");
        System.out.println("========================");
        for (Map<String, Object> habitacion : estadisticasHabitaciones) {
            System.out.printf("Habitación %s (%s):\n",
                habitacion.get("roomNumber"), habitacion.get("roomType"));
            System.out.printf("  Total de reservas: %d\n", habitacion.get("totalReservations"));
            System.out.printf("  Días ocupados: %d\n", habitacion.get("totalDaysOccupied"));
            System.out.println();
        }
    }

    private static void mostrarIngresosPorMes() throws SQLException {
        Map<String, Object> ingresos = ReportService.getRevenueByMonth();
        List<Map<String, Object>> ingresosMensuales = 
            (List<Map<String, Object>>) ingresos.get("monthlyRevenue");
        
        System.out.println("\nIngresos por Mes");
        System.out.println("===============");
        for (Map<String, Object> mes : ingresosMensuales) {
            System.out.printf("%d-%02d: %.2f\n",
                mes.get("year"), mes.get("month"), mes.get("totalRevenue"));
        }
    }

    private static void mostrarReservasClientes() throws SQLException {
        Map<String, Object> reservas = ReportService.getClientReservations();
        List<Map<String, Object>> reservasClientes = 
            (List<Map<String, Object>>) reservas.get("clientReservations");
        
        System.out.println("\nReservas de Clientes");
        System.out.println("===================");
        for (Map<String, Object> reserva : reservasClientes) {
            System.out.printf("Cliente: %s %s\n",
                reserva.get("firstName"), reserva.get("lastName"));
            System.out.printf("Reserva #%d: %s a %s\n",
                reserva.get("reservationId"),
                reserva.get("checkInDate"),
                reserva.get("checkOutDate"));
            System.out.printf("Estado: %s - Habitaciones: %d - Total: %.2f\n",
                reserva.get("status"),
                reserva.get("numberOfRooms"),
                reserva.get("totalPrice"));
            System.out.println();
        }
    }

    private static void mostrarHabitacionesDisponibles() throws SQLException {
        Map<String, Object> habitaciones = ReportService.getAvailableRoomsReport();
        List<Map<String, Object>> habitacionesDisponibles = 
            (List<Map<String, Object>>) habitaciones.get("availableRooms");
        
        System.out.println("\nHabitaciones Disponibles");
        System.out.println("=======================");
        for (Map<String, Object> habitacion : habitacionesDisponibles) {
            System.out.printf("Habitación %s - Tipo: %s\n",
                habitacion.get("roomNumber"), habitacion.get("roomType"));
            System.out.printf("Capacidad: %d - Precio: %.2f\n",
                habitacion.get("capacity"), habitacion.get("pricePerNight"));
            System.out.printf("Estado: %s - Reservas actuales: %d\n",
                habitacion.get("status"), habitacion.get("currentReservations"));
            System.out.println();
        }
    }
} 