/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.model;

/**
 *
 * @author sebas
 */
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "boleto")
public class Boleto {
     private int idBoleto;
    private int idUsuario;
    private int idVuelo;
    private Date fechaCompra;
    private String numeroAsiento;
    private double valor; 

    public Boleto() {
    }

    public Boleto(int idBoleto, int idUsuario, int idVuelo, Date fechaCompra, String numeroAsiento, double valor) {
        this.idBoleto = idBoleto;
        this.idUsuario = idUsuario;
        this.idVuelo = idVuelo;
        this.fechaCompra = fechaCompra;
        this.numeroAsiento = numeroAsiento;
        this.valor=valor; 
    }

    public int getIdBoleto() {
        return idBoleto;
    }

    public void setIdBoleto(int idBoleto) {
        this.idBoleto = idBoleto;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdVuelo() {
        return idVuelo;
    }

    public void setIdVuelo(int idVuelo) {
        this.idVuelo = idVuelo;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getNumeroAsiento() {
        return numeroAsiento;
    }

    public void setNumeroAsiento(String numeroAsiento) {
        this.numeroAsiento = numeroAsiento;
    }
    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
