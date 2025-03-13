package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException; // Importar la excepción
import com.example.demo.model.Deudor;
import com.example.demo.service.DeudorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/deudores")
public class DeudorController {

    @Autowired
    private DeudorService deudorService;

    // Crear un nuevo deudor
    @PostMapping
    public Deudor createDeudor(@RequestBody Deudor deudor) {
        return deudorService.saveDeudor(deudor);
    }

    // Obtener todos los deudores
    @GetMapping
    public List<Deudor> getAllDeudores() {
        return deudorService.getAllDeudores();
    }


    @PutMapping("/{id}/pagar-cuota")
    public ResponseEntity<Deudor> pagarCuota(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> updates // Parámetros opcionales
    ) {
        // Inicializar el Logger
        Logger logger = LoggerFactory.getLogger(DeudorController.class); // Cambia "DeudorController" por el nombre de tu clase

        // Log: Información de entrada
        logger.info("Iniciando pago de cuota para el deudor con ID: {}", id);
        if (updates != null) {
            logger.info("Datos recibidos en el cuerpo de la solicitud: {}", updates);
        } else {
            logger.info("No se recibieron datos adicionales en el cuerpo de la solicitud.");
        }

        // Buscar el deudor por ID
        Deudor deudor = deudorService.findById(id)
                .orElseThrow(() -> {
                    logger.error("Deudor no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Deudor no encontrado");
                });

        // Validar que el monto pendiente no sea cero o negativo
        if (deudor.getMontoPendiente() <= 0) {
            logger.warn("El deudor con ID {} ya no tiene deuda pendiente. Monto pendiente: {}", id, deudor.getMontoPendiente());
            throw new IllegalStateException("El deudor ya no tiene deuda pendiente.");
        }

        // Actualizar el monto de la cuota semanal si se proporciona en el cuerpo de la solicitud
        if (updates != null && updates.containsKey("montoCuotaSemanal")) {
            double nuevoMontoCuotaSemanal = (double) updates.get("montoCuotaSemanal");
            logger.info("Actualizando monto de la cuota semanal a: {}", nuevoMontoCuotaSemanal);
            deudor.setMontoCuotaSemanal(nuevoMontoCuotaSemanal);
        } else {
            logger.info("No se proporcionó un nuevo monto de cuota semanal. Usando el valor actual: {}", deudor.getMontoCuotaSemanal());
        }

        // Actualizar el monto pendiente usando el monto de la cuota semanal (original o modificado)
        double montoPendienteAnterior = deudor.getMontoPendiente();
        deudor.setMontoPendiente(montoPendienteAnterior - deudor.getMontoCuotaSemanal());
        logger.info("Monto pendiente actualizado. Anterior: {}, Nuevo: {}", montoPendienteAnterior, deudor.getMontoPendiente());

        // Si el monto pendiente es cero o menor, marcarlo como cobrado
        if (deudor.getMontoPendiente() <= 0) {
            deudor.setMontoPendiente(0); // Asegurarse de que no sea negativo
            deudor.setCobrado(true); // Marcar como cobrado
            logger.info("Deuda completada. Monto pendiente ajustado a 0 y marcado como cobrado.");
        }

        // Actualizar la fecha del último pago
        deudor.setFechaUltimoPago(LocalDate.now());
        logger.info("Fecha del último pago actualizada a: {}", deudor.getFechaUltimoPago());

        // Actualizar la fecha de próximo pago si se proporciona en el cuerpo de la solicitud
        if (updates != null && updates.containsKey("fechaProximoPago")) {
            String fechaProximoPagoStr = (String) updates.get("fechaProximoPago");
            LocalDate fechaProximoPago = LocalDate.parse(fechaProximoPagoStr);
            deudor.setFechaProximoPago(fechaProximoPago);
            logger.info("Fecha del próximo pago actualizada a: {}", fechaProximoPago);
        } else {
            // Calcular la fecha del próximo pago (7 días después) si no se proporciona
            LocalDate fechaProximoPagoDefault = LocalDate.now().plusDays(7);
            deudor.setFechaProximoPago(fechaProximoPagoDefault);
            logger.info("No se proporcionó fecha de próximo pago. Usando fecha por defecto: {}", fechaProximoPagoDefault);
        }

        // Guardar los cambios
        Deudor updatedDeudor = deudorService.saveDeudor(deudor);
        logger.info("Deudor actualizado y guardado: {}", updatedDeudor);

        return ResponseEntity.ok(updatedDeudor);
    }
}