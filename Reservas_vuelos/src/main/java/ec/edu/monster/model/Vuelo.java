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
@XmlRootElement(name = "vuelo")
public class Vuelo {
    private int idVuelo;
    private String ciudadOrigen;
    private String ciudadDestino;
    private double valor;
    private Date horaSalida;
    private int asientosDisponibles;

    public Vuelo() {
    }

    public Vuelo(int idVuelo, String ciudadOrigen, String ciudadDestino, double valor, Date horaSalida, int asientosDisponibles) {
        this.idVuelo = idVuelo;
        this.ciudadOrigen = ciudadOrigen;
        this.ciudadDestino = ciudadDestino;
        this.valor = valor;
        this.horaSalida = horaSalida;
        this.asientosDisponibles = asientosDisponibles;
    }

    public int getIdVuelo() {
        return idVuelo;
    }

    public void setIdVuelo(int idVuelo) {
        this.idVuelo = idVuelo;
    }

    public String getCiudadOrigen() {
        return ciudadOrigen;
    }

    public void setCiudadOrigen(String ciudadOrigen) {
        this.ciudadOrigen = ciudadOrigen;
    }

    public String getCiudadDestino() {
        return ciudadDestino;
    }

    public void setCiudadDestino(String ciudadDestino) {
        this.ciudadDestino = ciudadDestino;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Date getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(Date horaSalida) {
        this.horaSalida = horaSalida;
    }

    public int getAsientosDisponibles() {
        return asientosDisponibles;
    }

    public void setAsientosDisponibles(int asientosDisponibles) {
        this.asientosDisponibles = asientosDisponibles;
    }
}
