package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException; // Importar la excepción
import com.example.demo.model.ApiResponse;
import com.example.demo.model.Deudor;
import com.example.demo.service.DeudorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;
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

    private static final Logger logger = LoggerFactory.getLogger(DeudorController.class);

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

    // Obtener deudores activos
    @GetMapping("/activos")
    public ResponseEntity<List<Deudor>> obtenerDeudoresActivos() {
        List<Deudor> deudoresActivos = deudorService.obtenerDeudoresActivos();
        return ResponseEntity.ok(deudoresActivos);
    }

    @GetMapping("/historial")
    public ResponseEntity<ApiResponse<List<Deudor>>> obtenerHistorialDeudores() {
        try {
            logger.info("Obteniendo el historial de deudores");

            // Obtener todos los deudores con su historial de pagos
            List<Deudor> deudores = deudorService.getAllDeudores();

            // Formatear la respuesta
            ApiResponse<List<Deudor>> response = new ApiResponse<>(true, "Datos obtenidos correctamente", deudores);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener el historial de deudores", e);
            ApiResponse<List<Deudor>> response = new ApiResponse<>(false, "Error interno del servidor", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}/pagar-cuota")
    public ResponseEntity<?> pagarCuota(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> updates
    ) {
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

            // Actualizar el monto pendiente
            double montoPendienteAnterior = deudor.getMontoPendiente();
            deudor.setMontoPendiente(montoPendienteAnterior - deudor.getMontoCuotaSemanal());
            logger.info("Monto pendiente actualizado. Anterior: {}, Nuevo: {}", montoPendienteAnterior, deudor.getMontoPendiente());

            // Si el monto pendiente es cero o menor, marcarlo como cobrado
            if (deudor.getMontoPendiente() <= 0) {
                deudor.setMontoPendiente(0);
                deudor.setCobrado(true);
                logger.info("Deuda completada. Monto pendiente ajustado a 0 y marcado como cobrado.");
            }

            // Actualizar la fecha del último pago
            deudor.setFechaUltimoPago(LocalDate.now());
            logger.info("Fecha del último pago actualizada a: {}", deudor.getFechaUltimoPago());

            // Actualizar la fecha de próximo pago si se proporciona en el cuerpo de la solicitud
            if (updates != null && updates.containsKey("fechaProximoPago")) {
                try {
                    String fechaProximoPagoStr = (String) updates.get("fechaProximoPago");
                    LocalDate fechaProximoPago = LocalDate.parse(fechaProximoPagoStr);
                    deudor.setFechaProximoPago(fechaProximoPago);
                    logger.info("Fecha del próximo pago actualizada a: {}", fechaProximoPago);
                } catch (DateTimeParseException e) {
                    logger.error("El valor de fechaProximoPago no es una fecha válida.");
                    throw new IllegalArgumentException("El valor de fechaProximoPago no es una fecha válida.");
                }
            } else {
                LocalDate fechaProximoPagoDefault = LocalDate.now().plusDays(7);
                deudor.setFechaProximoPago(fechaProximoPagoDefault);
                logger.info("No se proporcionó fecha de próximo pago. Usando fecha por defecto: {}", fechaProximoPagoDefault);
            }

            // Guardar los cambios
            Deudor updatedDeudor = deudorService.saveDeudor(deudor);
            logger.info("Deudor actualizado y guardado: {}", updatedDeudor);

            return ResponseEntity.ok(updatedDeudor);
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