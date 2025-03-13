package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException; // Importar la excepción
import com.example.demo.model.Deudor;
import com.example.demo.service.DeudorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> pagarCuota(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> updates
    ) {
        Logger logger = LoggerFactory.getLogger(DeudorController.class);

        try {
            logger.info("Iniciando pago de cuota para el deudor con ID: {}", id);
            if (updates != null) {
                logger.info("Datos recibidos en el cuerpo de la solicitud: {}", updates);
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
                Number montoCuotaSemanal = (Number) updates.get("montoCuotaSemanal");
                double nuevoMontoCuotaSemanal = montoCuotaSemanal.doubleValue();
                if (nuevoMontoCuotaSemanal <= 0) {
                    logger.error("El monto de la cuota semanal debe ser mayor que 0.");
                    throw new IllegalArgumentException("El monto de la cuota semanal debe ser mayor que 0.");
                }
                deudor.setMontoCuotaSemanal(nuevoMontoCuotaSemanal);
                logger.info("Monto de la cuota semanal actualizado a: {}", nuevoMontoCuotaSemanal);
            }

            // Resto de la lógica...
            return ResponseEntity.ok(deudorService.saveDeudor(deudor));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al procesar la solicitud", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }
}