/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.service;

/**
 *
 * @author sebas
 */
import ec.edu.monster.db.AccesoDB;
import ec.edu.monster.model.Vuelo;
import ec.edu.monster.model.Usuario;
import ec.edu.monster.model.Boleto;
import ec.edu.monster.model.Compra;
import ec.edu.monster.model.Factura;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class VueloService {
private static final Logger LOGGER = Logger.getLogger(VueloService.class.getName());

public List<Vuelo> buscarVuelos(String ciudadOrigen, String ciudadDestino, Date fecha) {
        Connection cn = null;
        List<Vuelo> lista = new ArrayList<>();
        String sql = "SELECT id_vuelo, ciudad_origen, ciudad_destino, valor, hora_salida, asientos_disponibles " +
                     "FROM Vuelos " +
                     "WHERE ciudad_origen = ? AND ciudad_destino = ? AND DATE(hora_salida) = DATE(?)";

        try {
            cn = AccesoDB.getConnection();
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, ciudadOrigen);
            pstm.setString(2, ciudadDestino);
            pstm.setDate(3, new java.sql.Date(fecha.getTime()));
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                Vuelo vuelo = new Vuelo();
                vuelo.setIdVuelo(rs.getInt("id_vuelo"));
                vuelo.setCiudadOrigen(rs.getString("ciudad_origen"));
                vuelo.setCiudadDestino(rs.getString("ciudad_destino"));
                vuelo.setValor(rs.getDouble("valor"));
                vuelo.setHoraSalida(rs.getTimestamp("hora_salida"));
                vuelo.setAsientosDisponibles(rs.getInt("asientos_disponibles"));
                lista.add(vuelo);
            }
            rs.close();
            pstm.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar vuelos: " + e.getMessage());
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
            }
        }
        return lista;
    }

    public int comprarBoletos(int idUsuario, int idVuelo, int numeroAsientos) {
        Connection cn = null;
        try {
            cn = AccesoDB.getConnection();
            cn.setAutoCommit(false);

            // Step 1: Check flight availability
            String sql = "SELECT valor, asientos_disponibles FROM Vuelos WHERE id_vuelo = ? FOR UPDATE";
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setInt(1, idVuelo);
            ResultSet rs = pstm.executeQuery();
            if (!rs.next()) {
                throw new SQLException("ERROR: Vuelo no existe.");
            }
            double valor = rs.getDouble("valor");
            int asientosDisponibles = rs.getInt("asientos_disponibles");
            rs.close();
            pstm.close();

            if (asientosDisponibles < numeroAsientos) {
                throw new SQLException("ERROR: No hay suficientes asientos disponibles.");
            }

            // Step 2: Update available seats
            sql = "UPDATE Vuelos SET asientos_disponibles = asientos_disponibles - ? WHERE id_vuelo = ?";
            pstm = cn.prepareStatement(sql);
            pstm.setInt(1, numeroAsientos);
            pstm.setInt(2, idVuelo);
            pstm.executeUpdate();
            pstm.close();

            // Step 3: Insert tickets and one purchase
            List<Integer> boletoIds = new ArrayList<>();
            for (int i = 0; i < numeroAsientos; i++) {
                String numeroAsiento = generarNumeroAsiento(idVuelo, i + 1);

                // Insert into Boletos
                sql = "INSERT INTO Boletos (id_usuario, id_vuelo, fecha_compra, numero_asiento) " +
                      "VALUES (?, ?, SYSDATE(), ?)";
                pstm = cn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                pstm.setInt(1, idUsuario);
                pstm.setInt(2, idVuelo);
                pstm.setString(3, numeroAsiento);
                pstm.executeUpdate();

                rs = pstm.getGeneratedKeys();
                int idBoleto = 0;
                if (rs.next()) {
                    idBoleto = rs.getInt(1);
                    boletoIds.add(idBoleto);
                }
                rs.close();
                pstm.close();
            }

            // Insert one Compra for all tickets
            double montoTotal = valor * numeroAsientos; // Total before discount and IVA
            sql = "INSERT INTO Compras (id_usuario, id_boleto, fecha_compra, monto_total) " +
                  "VALUES (?, ?, SYSDATE(), ?)";
            pstm = cn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstm.setInt(1, idUsuario);
            pstm.setInt(2, boletoIds.get(0)); // Link to the first boleto
            pstm.setDouble(3, montoTotal);
            pstm.executeUpdate();

            // Get the generated id_compra
            rs = pstm.getGeneratedKeys();
            int idCompra = 0;
            if (rs.next()) {
                idCompra = rs.getInt(1);
            }
            rs.close();
            pstm.close();

            // Step 4: Insert one Factura for the purchase
            double iva = 16.0; // Fixed IVA
            double descuento = 0.0; // Determine discount based on numeroAsientos
            if (numeroAsientos >= 9) {
                descuento = 30.0;
            } else if (numeroAsientos >= 6) {
                descuento = 20.0;
            } else if (numeroAsientos >= 4) {
                descuento = 10.0;
            }
            int cantidad = numeroAsientos; // Total tickets in this purchase
            double precioUnitario = valor;
            double precioTotal = precioUnitario * cantidad * (1 + iva / 100) * (1 - descuento / 100);

            sql = "INSERT INTO Facturas (id_compra, agente_venta, descuento, iva, cantidad, precio_unitario, precio_total, fecha_emision) " +
                  "VALUES (?, ?, ?, ?, ?, ?, ?, SYSDATE())";
            pstm = cn.prepareStatement(sql);
            pstm.setInt(1, idCompra);
            pstm.setString(2, "Viajecitos S.A.");
            pstm.setDouble(3, descuento);
            pstm.setDouble(4, iva);
            pstm.setInt(5, cantidad);
            pstm.setDouble(6, precioUnitario);
            pstm.setDouble(7, precioTotal);
            pstm.executeUpdate();
            pstm.close();

            cn.commit();
            LOGGER.info("Compra exitosa: idCompra=" + idCompra + ", numeroAsientos=" + numeroAsientos + ", descuento=" + descuento + "%, precioTotal=" + precioTotal);
            return 1;
        } catch (SQLException e) {
            try {
                if (cn != null) cn.rollback();
            } catch (Exception ex) {
                LOGGER.severe("Error during rollback: " + ex.getMessage());
            }
            LOGGER.severe("Error al comprar boletos: " + e.getMessage());
            throw new RuntimeException("Error al comprar boletos: " + e.getMessage());
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                LOGGER.severe("Error closing connection: " + e.getMessage());
            }
        }
    }

    private String generarNumeroAsiento(int idVuelo, int index) {
        return "V" + idVuelo + "-" + (char)('A' + (index % 26)) + (index / 26 + 1);
    }

    public List<Boleto> mostrarBoletosUsuario(int idUsuario) {
        Connection cn = null;
        List<Boleto> lista = new ArrayList<>();
        String sql = "SELECT b.id_boleto, b.id_usuario, b.id_vuelo, b.fecha_compra, b.numero_asiento, v.valor " +
                     "FROM Boletos b " +
                     "INNER JOIN Vuelos v ON b.id_vuelo = v.id_vuelo " +
                     "WHERE b.id_usuario = ?";

        try {
            cn = AccesoDB.getConnection();
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setInt(1, idUsuario);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                Boleto boleto = new Boleto();
                boleto.setIdBoleto(rs.getInt("id_boleto"));
                boleto.setIdUsuario(rs.getInt("id_usuario"));
                boleto.setIdVuelo(rs.getInt("id_vuelo"));
                boleto.setFechaCompra(rs.getTimestamp("fecha_compra"));
                boleto.setNumeroAsiento(rs.getString("numero_asiento"));
                boleto.setValor(rs.getDouble("valor"));
                lista.add(boleto);
            }
            rs.close();
            pstm.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener boletos: " + e.getMessage());
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
            }
        }
        return lista;
    }

    public List<Vuelo> mostrarTodosVuelos() {
        Connection cn = null;
        List<Vuelo> lista = new ArrayList<>();
        String sql = "SELECT id_vuelo, ciudad_origen, ciudad_destino, valor, hora_salida, asientos_disponibles " +
                     "FROM Vuelos " +
                     "ORDER BY valor DESC";

        try {
            cn = AccesoDB.getConnection();
            PreparedStatement pstm = cn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                Vuelo vuelo = new Vuelo();
                vuelo.setIdVuelo(rs.getInt("id_vuelo"));
                vuelo.setCiudadOrigen(rs.getString("ciudad_origen"));
                vuelo.setCiudadDestino(rs.getString("ciudad_destino"));
                vuelo.setValor(rs.getDouble("valor"));
                vuelo.setHoraSalida(rs.getTimestamp("hora_salida"));
                vuelo.setAsientosDisponibles(rs.getInt("asientos_disponibles"));
                lista.add(vuelo);
            }
            rs.close();
            pstm.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener vuelos: " + e.getMessage());
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
            }
        }
        return lista;
    }

    public int registrarUsuario(String nombreUsuario, String apellidoUsuario, String cedula, String celular, String email, String contrasena) {
        Connection cn = null;
        try {
            cn = AccesoDB.getConnection();
            String sql = "INSERT INTO Usuarios (nombre_usuario, apellido_usuario, cedula, celular, email, contrasena) " +
                         "VALUES (?, ?, ?, ?, ?, SHA(?))";
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, nombreUsuario);
            pstm.setString(2, apellidoUsuario);
            pstm.setString(3, cedula);
            pstm.setString(4, celular);
            pstm.setString(5, email);
            pstm.setString(6, contrasena);
            pstm.executeUpdate();
            pstm.close();
            return 1;
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage());
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
            }
        }
    }

    public boolean login(String email, String contrasena) {
        Connection cn = null;
        boolean acceso = false;
        String sql = "SELECT COUNT(1) AS total " +
                     "FROM Usuarios " +
                     "WHERE email = ? AND contrasena = SHA(?)";

        try {
            cn = AccesoDB.getConnection();
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, email);
            pstm.setString(2, contrasena);
            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                acceso = rs.getInt("total") == 1;
            }
            rs.close();
            pstm.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error al validar login: " + e.getMessage());
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
            }
        }
        return acceso;
    }

    public int obtenerIdUsuarioPorEmail(String email) {
        Connection cn = null;
        int idUsuario = -1;
        String sql = "SELECT id_usuario FROM Usuarios WHERE email = ?";

        try {
            cn = AccesoDB.getConnection();
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, email);
            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                idUsuario = rs.getInt("id_usuario");
            }
            rs.close();
            pstm.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener ID de usuario: " + e.getMessage());
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
            }
        }
        return idUsuario;
    }
    
     public Factura obtenerFactura(int idCompra) {
        Connection cn = null;
        Factura factura = null;
        String sql = "SELECT id_factura, id_compra, agente_venta, descuento, iva, cantidad, precio_unitario, precio_total, fecha_emision " +
                     "FROM Facturas WHERE id_compra = ?";

        try {
            cn = AccesoDB.getConnection();
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setInt(1, idCompra);
            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                factura = new Factura();
                factura.setIdFactura(rs.getInt("id_factura"));
                factura.setIdCompra(rs.getInt("id_compra"));
                factura.setAgenteVenta(rs.getString("agente_venta"));
                factura.setDescuento(rs.getDouble("descuento"));
                factura.setIva(rs.getDouble("iva"));
                factura.setCantidad(rs.getInt("cantidad"));
                factura.setPrecioUnitario(rs.getDouble("precio_unitario"));
                factura.setPrecioTotal(rs.getDouble("precio_total"));
                factura.setFechaEmision(rs.getTimestamp("fecha_emision"));
            }
            rs.close();
            pstm.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener factura: " + e.getMessage());
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
            }
        }
        return factura;
    }
    public List<Factura> obtenerFacturasUsuarioPorFecha(int idUsuario, Date fecha) {
        Connection cn = null;
        List<Factura> lista = new ArrayList<>();
        String sql = "SELECT f.id_factura, f.id_compra, f.agente_venta, f.descuento, f.iva, f.cantidad, f.precio_unitario, f.precio_total, f.fecha_emision " +
                     "FROM Facturas f " +
                     "INNER JOIN Compras c ON f.id_compra = c.id_compra " +
                     "WHERE c.id_usuario = ? AND DATE(f.fecha_emision) = DATE(?)";

        try {
            LOGGER.info("Attempting to retrieve facturas for idUsuario: " + idUsuario + " and fecha: " + fecha);
            cn = AccesoDB.getConnection();
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setInt(1, idUsuario);
            pstm.setDate(2, new java.sql.Date(fecha.getTime()));
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                Factura factura = new Factura();
                factura.setIdFactura(rs.getInt("id_factura"));
                factura.setIdCompra(rs.getInt("id_compra"));
                factura.setAgenteVenta(rs.getString("agente_venta"));
                factura.setDescuento(rs.getDouble("descuento"));
                factura.setIva(rs.getDouble("iva"));
                factura.setCantidad(rs.getInt("cantidad"));
                factura.setPrecioUnitario(rs.getDouble("precio_unitario"));
                factura.setPrecioTotal(rs.getDouble("precio_total"));
                factura.setFechaEmision(rs.getTimestamp("fecha_emision"));
                lista.add(factura);
            }
            rs.close();
            pstm.close();
            LOGGER.info("Found " + lista.size() + " facturas for idUsuario: " + idUsuario + " and fecha: " + fecha);
        } catch (SQLException e) {
            LOGGER.severe("Error al obtener facturas for idUsuario " + idUsuario + ": " + e.getMessage());
            throw new RuntimeException("Error al obtener facturas: " + e.getMessage());
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                LOGGER.severe("Error closing connection: " + e.getMessage());
            }
        }
        return lista;
    }
}
