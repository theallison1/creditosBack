package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "deudores") // Especifica el nombre de la tabla en la base de datos
public class Deudor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_deudor", nullable = false, length = 100) // Especifica nombre de columna y longitud
    private String nombreDeudor;

    @Column(name = "monto_inicial", nullable = false)
    private double montoInicial;

    @Column(name = "monto_cuota_semanal", nullable = false)
    private double montoCuotaSemanal;

    @Temporal(TemporalType.DATE) // Especifica el tipo de fecha (solo fecha, sin hora)
    @Column(name = "fecha_inicio", nullable = false)
    private Date fechaInicio;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_ultimo_pago")
    private LocalDate fechaUltimoPago;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_proximo_pago")
    private LocalDate fechaProximoPago;

    @Column(name = "monto_pendiente", nullable = false)
    private double montoPendiente;

    @Column(name = "cobrado", nullable = false)
    private boolean cobrado;

    // Constructor vacío (requerido por JPA)
    public Deudor() {
    }

    // Constructor con parámetros (opcional, pero útil)
    public Deudor(String nombreDeudor, double montoInicial, double montoCuotaSemanal, Date fechaInicio, double montoPendiente, boolean cobrado) {
        this.nombreDeudor = nombreDeudor;
        this.montoInicial = montoInicial;
        this.montoCuotaSemanal = montoCuotaSemanal;
        this.fechaInicio = fechaInicio;
        this.montoPendiente = montoPendiente;
        this.cobrado = cobrado;
    }

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

    public LocalDate getFechaUltimoPago() {
        return fechaUltimoPago;
    }

    public void setFechaUltimoPago(LocalDate fechaUltimoPago) {
        this.fechaUltimoPago = fechaUltimoPago;
    }

    public LocalDate getFechaProximoPago() {
        return fechaProximoPago;
    }

    public void setFechaProximoPago(LocalDate fechaProximoPago) {
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

    // Método toString (opcional, pero útil para debugging)
    @Override
    public String toString() {
        return "Deudor{" +
                "id=" + id +
                ", nombreDeudor='" + nombreDeudor + '\'' +
                ", montoInicial=" + montoInicial +
                ", montoCuotaSemanal=" + montoCuotaSemanal +
                ", fechaInicio=" + fechaInicio +
                ", fechaUltimoPago=" + fechaUltimoPago +
                ", fechaProximoPago=" + fechaProximoPago +
                ", montoPendiente=" + montoPendiente +
                ", cobrado=" + cobrado +
                '}';
    }
}