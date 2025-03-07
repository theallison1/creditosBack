package com.example.demo.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Deudor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreDeudor;
    private double montoInicial;
    private double montoCuotaSemanal;
    private Date fechaInicio;
    private Date fechaUltimoPago;
    private Date fechaProximoPago; // Nuevo campo
    private double montoPendiente;
    private boolean cobrado;

    // Getters y Setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreDeudor() {
        return nombreDeudor;
    }

    public void setNombreDeudor(String nombreDeudor) {
        this.nombreDeudor = nombreDeudor;
    }

    public double getMontoInicial() {
        return montoInicial;
    }

    public void setMontoInicial(double montoInicial) {
        this.montoInicial = montoInicial;
    }

    public double getMontoCuotaSemanal() {
        return montoCuotaSemanal;
    }

    public void setMontoCuotaSemanal(double montoCuotaSemanal) {
        this.montoCuotaSemanal = montoCuotaSemanal;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaUltimoPago() {
        return fechaUltimoPago;
    }

    public void setFechaUltimoPago(Date fechaUltimoPago) {
        this.fechaUltimoPago = fechaUltimoPago;
    }

    public Date getFechaProximoPago() {
        return fechaProximoPago;
    }

    public void setFechaProximoPago(Date fechaProximoPago) {
        this.fechaProximoPago = fechaProximoPago;
    }

    public double getMontoPendiente() {
        return montoPendiente;
    }

    public void setMontoPendiente(double montoPendiente) {
        this.montoPendiente = montoPendiente;
    }

    public boolean isCobrado() {
        return cobrado;
    }

    public void setCobrado(boolean cobrado) {
        this.cobrado = cobrado;
    }
}