/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.controller;
import ec.edu.monster.model.Vuelo;
import ec.edu.monster.model.Boleto;
import ec.edu.monster.model.Factura;
import ec.edu.monster.service.VueloService;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author sebas
 */
@WebService(serviceName = "WSVuelo")
public class WSVuelo {
    @WebMethod(operationName = "buscarVuelos")
    @WebResult(name = "vuelo")
    public List<Vuelo> buscarVuelos(
            @WebParam(name = "ciudadOrigen") String ciudadOrigen,
            @WebParam(name = "ciudadDestino") String ciudadDestino,
            @WebParam(name = "fecha") Date fecha) {
        List<Vuelo> lista;
        try {
            VueloService service = new VueloService();
            lista = service.buscarVuelos(ciudadOrigen, ciudadDestino, fecha);
        } catch (Exception e) {
            lista = new ArrayList<>();
        }
        return lista;
    }

    @WebMethod(operationName = "comprarBoletos")
    @WebResult(name = "estado")
    public int comprarBoletos(
            @WebParam(name = "idUsuario") int idUsuario,
            @WebParam(name = "idVuelo") int idVuelo,
            @WebParam(name = "numeroAsientos") int numeroAsientos) {
        int estado;
        try {
            VueloService service = new VueloService();
            estado = service.comprarBoletos(idUsuario, idVuelo, numeroAsientos);
        } catch (Exception e) {
            estado = -1;
        }
        return estado;
    }

    @WebMethod(operationName = "mostrarBoletosUsuario")
    @WebResult(name = "boleto")
    public List<Boleto> mostrarBoletosUsuario(@WebParam(name = "idUsuario") int idUsuario) {
        List<Boleto> lista;
        try {
            VueloService service = new VueloService();
            lista = service.mostrarBoletosUsuario(idUsuario);
        } catch (Exception e) {
            lista = new ArrayList<>();
        }
        return lista;
    }

    @WebMethod(operationName = "mostrarTodosVuelos")
    @WebResult(name = "vuelo")
    public List<Vuelo> mostrarTodosVuelos() {
        List<Vuelo> lista;
        try {
            VueloService service = new VueloService();
            lista = service.mostrarTodosVuelos();
        } catch (Exception e) {
            lista = new ArrayList<>();
        }
        return lista;
    }

    @WebMethod(operationName = "registrarUsuario")
    @WebResult(name = "estado")
    public int registrarUsuario(
            @WebParam(name = "nombreUsuario") String nombreUsuario,
            @WebParam(name = "apellidoUsuario") String apellidoUsuario,
            @WebParam(name = "cedula") String cedula,
            @WebParam(name = "celular") String celular,
            @WebParam(name = "email") String email,
            @WebParam(name = "contrasena") String contrasena) {
        int estado;
        try {
            VueloService service = new VueloService();
            estado = service.registrarUsuario(nombreUsuario, apellidoUsuario, cedula, celular, email, contrasena);
        } catch (Exception e) {
            estado = -1;
        }
        return estado;
    }

    @WebMethod(operationName = "login")
    @WebResult(name = "acceso")
    public boolean login(
            @WebParam(name = "email") String email,
            @WebParam(name = "contrasena") String contrasena) {
        try {
            VueloService service = new VueloService();
            return service.login(email, contrasena);
        } catch (Exception e) {
            return false;
        }
    }

    @WebMethod(operationName = "obtenerIdUsuarioPorEmail")
    @WebResult(name = "idUsuario")
    public int obtenerIdUsuarioPorEmail(@WebParam(name = "email") String email) {
        try {
            VueloService service = new VueloService();
            return service.obtenerIdUsuarioPorEmail(email);
        } catch (Exception e) {
            return -1;
        }
    }
    
    @WebMethod(operationName = "obtenerFactura")
    @WebResult(name = "factura")
    public Factura obtenerFactura(@WebParam(name = "idCompra") int idCompra) {
        try {
            VueloService service = new VueloService();
            return service.obtenerFactura(idCompra);
        } catch (Exception e) {
            return null;
        }
    }
    
    @WebMethod(operationName = "obtenerFacturasUsuarioPorFecha")
    @WebResult(name = "factura")
    public List<Factura> obtenerFacturasUsuarioPorFecha(
            @WebParam(name = "idUsuario") int idUsuario,
            @WebParam(name = "fecha") Date fecha) {
        List<Factura> lista;
        try {
            VueloService service = new VueloService();
            lista = service.obtenerFacturasUsuarioPorFecha(idUsuario, fecha);
        } catch (Exception e) {
            lista = new ArrayList<>();
        }
        return lista;
    }
}
