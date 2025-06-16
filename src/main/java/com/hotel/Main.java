package com.hotel;

import com.hotel.config.DatabaseConfig;
import com.hotel.model.*;
import com.hotel.service.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ServicioCliente servicioCliente = new ServicioCliente();
    private static final ServicioHabitacion servicioHabitacion = new ServicioHabitacion();
    private static final ServicioReserva servicioReserva = new ServicioReserva();
    private static final ServicioReporte servicioReporte = new ServicioReporte();

    public static void main(String[] args) {
        try {
            // Verificar conexión a la base de datos
            try (Connection conn = DatabaseConfig.getConnection()) {
                System.out.println("Conexión exitosa a la base de datos");
            }

            while (true) {
                mostrarMenu();
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir el salto de línea

                switch (opcion) {
                    case 1:
                        gestionarClientes();
                        break;
                    case 2:
                        gestionarHabitaciones();
                        break;
                    case 3:
                        gestionarReservas();
                        break;
                    case 4:
                        generarReportes();
                        break;
                    case 5:
                        System.out.println("¡Hasta luego!");
                        return;
                    default:
                        System.out.println("Opción no válida");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión a la base de datos: " + e.getMessage());
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n=== SISTEMA DE GESTIÓN HOTELERA ===");
        System.out.println("1. Gestión de Clientes");
        System.out.println("2. Gestión de Habitaciones");
        System.out.println("3. Gestión de Reservas");
        System.out.println("4. Reportes");
        System.out.println("5. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void gestionarClientes() throws SQLException {
        while (true) {
            System.out.println("\n=== GESTIÓN DE CLIENTES ===");
            System.out.println("1. Registrar nuevo cliente");
            System.out.println("2. Buscar cliente");
            System.out.println("3. Actualizar cliente");
            System.out.println("4. Eliminar cliente");
            System.out.println("5. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            switch (opcion) {
                case 1:
                    registrarCliente();
                    break;
                case 2:
                    buscarCliente();
                    break;
                case 3:
                    actualizarCliente();
                    break;
                case 4:
                    eliminarCliente();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private static void registrarCliente() throws SQLException {
        System.out.println("\n=== REGISTRAR NUEVO CLIENTE ===");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Apellido: ");
        String apellido = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Teléfono: ");
        String telefono = scanner.nextLine();
        System.out.print("Dirección: ");
        String direccion = scanner.nextLine();

        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setEmail(email);
        cliente.setTelefono(telefono);
        cliente.setDireccion(direccion);

        servicioCliente.agregarCliente(cliente);
        System.out.println("Cliente registrado exitosamente");
    }

    private static void buscarCliente() throws SQLException {
        System.out.println("\n=== BUSCAR CLIENTE ===");
        System.out.print("Ingrese el ID del cliente: ");
        int idCliente = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        Cliente cliente = servicioCliente.obtenerClientePorId(idCliente);
        if (cliente != null) {
            System.out.println("\nInformación del cliente:");
            System.out.println("ID: " + cliente.getIdCliente());
            System.out.println("Nombre: " + cliente.getNombre());
            System.out.println("Apellido: " + cliente.getApellido());
            System.out.println("Email: " + cliente.getEmail());
            System.out.println("Teléfono: " + cliente.getTelefono());
            System.out.println("Dirección: " + cliente.getDireccion());
        } else {
            System.out.println("Cliente no encontrado");
        }
    }

    private static void actualizarCliente() throws SQLException {
        System.out.println("\n=== ACTUALIZAR CLIENTE ===");
        System.out.print("Ingrese el ID del cliente a actualizar: ");
        int idCliente = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        Cliente cliente = servicioCliente.obtenerClientePorId(idCliente);
        if (cliente != null) {
            System.out.print("Nuevo nombre (actual: " + cliente.getNombre() + "): ");
            String nombre = scanner.nextLine();
            System.out.print("Nuevo apellido (actual: " + cliente.getApellido() + "): ");
            String apellido = scanner.nextLine();
            System.out.print("Nuevo email (actual: " + cliente.getEmail() + "): ");
            String email = scanner.nextLine();
            System.out.print("Nuevo teléfono (actual: " + cliente.getTelefono() + "): ");
            String telefono = scanner.nextLine();
            System.out.print("Nueva dirección (actual: " + cliente.getDireccion() + "): ");
            String direccion = scanner.nextLine();

            cliente.setNombre(nombre);
            cliente.setApellido(apellido);
            cliente.setEmail(email);
            cliente.setTelefono(telefono);
            cliente.setDireccion(direccion);

            servicioCliente.actualizarCliente(cliente);
            System.out.println("Cliente actualizado exitosamente");
        } else {
            System.out.println("Cliente no encontrado");
        }
    }

    private static void eliminarCliente() throws SQLException {
        System.out.println("\n=== ELIMINAR CLIENTE ===");
        System.out.print("Ingrese el ID del cliente a eliminar: ");
        int idCliente = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        servicioCliente.eliminarCliente(idCliente);
        System.out.println("Cliente eliminado exitosamente");
    }

    private static void gestionarHabitaciones() throws SQLException {
        while (true) {
            System.out.println("\n=== GESTIÓN DE HABITACIONES ===");
            System.out.println("1. Ver habitaciones disponibles");
            System.out.println("2. Agregar nueva habitación");
            System.out.println("3. Actualizar habitación");
            System.out.println("4. Eliminar habitación");
            System.out.println("5. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            switch (opcion) {
                case 1:
                    verHabitacionesDisponibles();
                    break;
                case 2:
                    agregarHabitacion();
                    break;
                case 3:
                    actualizarHabitacion();
                    break;
                case 4:
                    eliminarHabitacion();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private static void verHabitacionesDisponibles() throws SQLException {
        System.out.println("\n=== HABITACIONES DISPONIBLES ===");
        List<Habitacion> habitaciones = servicioHabitacion.obtenerHabitacionesDisponibles();
        for (Habitacion habitacion : habitaciones) {
            System.out.println("\nHabitación #" + habitacion.getNumHabitacion());
            System.out.println("Piso: " + habitacion.getPiso());
            System.out.println("Tipo: " + habitacion.getTipoHabitacion());
            System.out.println("Estado: " + habitacion.getEstado());
        }
    }

    private static void agregarHabitacion() throws SQLException {
        System.out.println("\n=== AGREGAR NUEVA HABITACIÓN ===");
        System.out.print("Número de habitación: ");
        String numHabitacion = scanner.nextLine();
        System.out.print("Piso: ");
        int piso = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        System.out.print("ID de tipo de habitación: ");
        int idTipo = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        Habitacion habitacion = new Habitacion();
        habitacion.setNumHabitacion(numHabitacion);
        habitacion.setPiso(piso);
        habitacion.setIdTipo(idTipo);
        habitacion.setEstado("DISPONIBLE");
        habitacion.setDisponible(true);

        servicioHabitacion.agregarHabitacion(habitacion);
        System.out.println("Habitación agregada exitosamente");
    }

    private static void actualizarHabitacion() throws SQLException {
        System.out.println("\n=== ACTUALIZAR HABITACIÓN ===");
        System.out.print("Ingrese el número de habitación a actualizar: ");
        String numHabitacion = scanner.nextLine();

        Habitacion habitacion = servicioHabitacion.obtenerHabitacionPorId(numHabitacion);
        if (habitacion != null) {
            System.out.print("Nuevo piso (actual: " + habitacion.getPiso() + "): ");
            int piso = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea
            System.out.print("Nuevo ID de tipo (actual: " + habitacion.getIdTipo() + "): ");
            int idTipo = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea
            System.out.print("Nuevo estado (actual: " + habitacion.getEstado() + "): ");
            String estado = scanner.nextLine();

            habitacion.setPiso(piso);
            habitacion.setIdTipo(idTipo);
            habitacion.setEstado(estado);
            habitacion.setDisponible(estado.equals("DISPONIBLE"));

            servicioHabitacion.actualizarHabitacion(habitacion);
            System.out.println("Habitación actualizada exitosamente");
        } else {
            System.out.println("Habitación no encontrada");
        }
    }

    private static void eliminarHabitacion() throws SQLException {
        System.out.println("\n=== ELIMINAR HABITACIÓN ===");
        System.out.print("Ingrese el número de habitación a eliminar: ");
        String numHabitacion = scanner.nextLine();

        servicioHabitacion.eliminarHabitacion(numHabitacion);
        System.out.println("Habitación eliminada exitosamente");
    }

    private static void gestionarReservas() throws SQLException {
        while (true) {
            System.out.println("\n=== GESTIÓN DE RESERVAS ===");
            System.out.println("1. Crear nueva reserva");
            System.out.println("2. Ver reservas de un cliente");
            System.out.println("3. Cancelar reserva");
            System.out.println("4. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            switch (opcion) {
                case 1:
                    crearReserva();
                    break;
                case 2:
                    verReservasCliente();
                    break;
                case 3:
                    cancelarReserva();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private static void crearReserva() throws SQLException {
        System.out.println("\n=== CREAR NUEVA RESERVA ===");
        System.out.print("ID del cliente: ");
        int idCliente = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        System.out.print("ID del empleado: ");
        int idEmpleado = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        System.out.print("Fecha de inicio (YYYY-MM-DD): ");
        String fechaInicioStr = scanner.nextLine();
        System.out.print("Fecha de fin (YYYY-MM-DD): ");
        String fechaFinStr = scanner.nextLine();
        System.out.print("Número de huéspedes: ");
        int numHuespedes = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        Reserva reserva = new Reserva();
        reserva.setIdCliente(idCliente);
        reserva.setIdEmpleado(idEmpleado);
        reserva.setFechaInicio(java.time.LocalDate.parse(fechaInicioStr));
        reserva.setFechaFin(java.time.LocalDate.parse(fechaFinStr));
        reserva.setNumHuespedes(numHuespedes);
        reserva.setEstado("PENDIENTE");

        servicioReserva.crearReserva(reserva);
        System.out.println("Reserva creada exitosamente");
    }

    private static void verReservasCliente() throws SQLException {
        System.out.println("\n=== VER RESERVAS DE CLIENTE ===");
        System.out.print("Ingrese el ID del cliente: ");
        int idCliente = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        List<Reserva> reservas = servicioReserva.obtenerReservasPorCliente(idCliente);
        for (Reserva reserva : reservas) {
            System.out.println("\nReserva #" + reserva.getIdReserva());
            System.out.println("Fecha de inicio: " + reserva.getFechaInicio());
            System.out.println("Fecha de fin: " + reserva.getFechaFin());
            System.out.println("Estado: " + reserva.getEstado());
            System.out.println("Total: " + reserva.getTotalEstadia());
        }
    }

    private static void cancelarReserva() throws SQLException {
        System.out.println("\n=== CANCELAR RESERVA ===");
        System.out.print("Ingrese el ID de la reserva a cancelar: ");
        int idReserva = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        servicioReserva.cancelarReserva(idReserva);
        System.out.println("Reserva cancelada exitosamente");
    }

    private static void generarReportes() throws SQLException {
        while (true) {
            System.out.println("\n=== REPORTES ===");
            System.out.println("1. Estadísticas de ocupación");
            System.out.println("2. Ingresos por mes");
            System.out.println("3. Reservas por cliente");
            System.out.println("4. Habitaciones disponibles");
            System.out.println("5. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

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
                case 5:
                    return;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private static void mostrarEstadisticasOcupacion() throws SQLException {
        System.out.println("\n=== ESTADÍSTICAS DE OCUPACIÓN ===");
        var estadisticas = servicioReporte.obtenerEstadisticasOcupacion();
        var habitaciones = (List<Map<String, Object>>) estadisticas.get("estadisticasHabitaciones");
        
        for (var habitacion : habitaciones) {
            System.out.println("\nHabitación #" + habitacion.get("numHabitacion"));
            System.out.println("Piso: " + habitacion.get("piso"));
            System.out.println("Tipo: " + habitacion.get("tipoHabitacion"));
            System.out.println("Total de reservas: " + habitacion.get("totalReservas"));
            System.out.println("Total de días ocupada: " + habitacion.get("totalDiasOcupada"));
        }
    }

    private static void mostrarIngresosPorMes() throws SQLException {
        System.out.println("\n=== INGRESOS POR MES ===");
        var ingresos = servicioReporte.obtenerIngresosPorMes();
        var meses = (List<Map<String, Object>>) ingresos.get("ingresosMensuales");
        
        for (var mes : meses) {
            System.out.println("\nAño: " + mes.get("año"));
            System.out.println("Mes: " + mes.get("mes"));
            System.out.println("Total de ingresos: " + mes.get("totalIngresos"));
        }
    }

    private static void mostrarReservasClientes() throws SQLException {
        System.out.println("\n=== RESERVAS POR CLIENTE ===");
        var reservas = servicioReporte.obtenerReservasClientes();
        var clientes = (List<Map<String, Object>>) reservas.get("reservasClientes");
        
        for (var cliente : clientes) {
            System.out.println("\nCliente: " + cliente.get("nombre") + " " + cliente.get("apellido"));
            System.out.println("ID de reserva: " + cliente.get("idReserva"));
            System.out.println("Fecha de inicio: " + cliente.get("fechaInicio"));
            System.out.println("Fecha de fin: " + cliente.get("fechaFin"));
            System.out.println("Estado: " + cliente.get("estado"));
            System.out.println("Número de habitaciones: " + cliente.get("numHabitaciones"));
            System.out.println("Total: " + cliente.get("totalEstadia"));
        }
    }

    private static void mostrarHabitacionesDisponibles() throws SQLException {
        System.out.println("\n=== HABITACIONES DISPONIBLES ===");
        var habitaciones = servicioReporte.obtenerReporteHabitacionesDisponibles();
        var disponibles = (List<Map<String, Object>>) habitaciones.get("habitacionesDisponibles");
        
        for (var habitacion : disponibles) {
            System.out.println("\nHabitación #" + habitacion.get("numHabitacion"));
            System.out.println("Piso: " + habitacion.get("piso"));
            System.out.println("Tipo: " + habitacion.get("tipoHabitacion"));
            System.out.println("Capacidad: " + habitacion.get("capacidad"));
            System.out.println("Precio por noche: " + habitacion.get("precioPorNoche"));
            System.out.println("Estado: " + habitacion.get("estado"));
            System.out.println("Reservas actuales: " + habitacion.get("reservasActuales"));
        }
    }
} 