/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.model;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Date;
/**
 *
 * @author sebas
 */
@XmlRootElement(name = "factura")
public class Factura {
    private int idFactura;
    private int idCompra;
    private String agenteVenta;
    private double descuento;
    private double iva;
    private int cantidad;
    private double precioUnitario;
    private double precioTotal;
    private Date fechaEmision;

    public Factura() {
    }

    public Factura(int idFactura, int idCompra, String agenteVenta, double descuento, double iva, int cantidad, 
                   double precioUnitario, double precioTotal, Date fechaEmision) {
        this.idFactura = idFactura;
        this.idCompra = idCompra;
        this.agenteVenta = agenteVenta;
        this.descuento = descuento;
        this.iva = iva;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.precioTotal = precioTotal;
        this.fechaEmision = fechaEmision;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public String getAgenteVenta() {
        return agenteVenta;
    }

    public void setAgenteVenta(String agenteVenta) {
        this.agenteVenta = agenteVenta;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
}
