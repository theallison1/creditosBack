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
        // Buscar el deudor por ID
        Deudor deudor = deudorService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deudor no encontrado"));

        // Validar que el monto pendiente no sea cero o negativo
        if (deudor.getMontoPendiente() <= 0) {
            throw new IllegalStateException("El deudor ya no tiene deuda pendiente.");
        }

        // Actualizar el monto pendiente
        deudor.setMontoPendiente(deudor.getMontoPendiente() - deudor.getMontoCuotaSemanal());

        // Si el monto pendiente es cero o menor, marcarlo como cobrado
        if (deudor.getMontoPendiente() <= 0) {
            deudor.setMontoPendiente(0); // Asegurarse de que no sea negativo
            deudor.setCobrado(true); // Marcar como cobrado
        }

        // Actualizar la fecha del último pago
        deudor.setFechaUltimoPago(LocalDate.now());

        // Actualizar la fecha de próximo pago si se proporciona en el cuerpo de la solicitud
        if (updates != null && updates.containsKey("fechaProximoPago")) {
            String fechaProximoPagoStr = (String) updates.get("fechaProximoPago");
            LocalDate fechaProximoPago = LocalDate.parse(fechaProximoPagoStr);
            deudor.setFechaProximoPago(fechaProximoPago);
        } else {
            // Calcular la fecha del próximo pago (7 días después) si no se proporciona
            deudor.setFechaProximoPago(LocalDate.now().plusDays(7));
        }

        // Actualizar el monto de la cuota semanal si se proporciona en el cuerpo de la solicitud
        if (updates != null && updates.containsKey("montoCuotaSemanal")) {
            double nuevoMontoCuotaSemanal = (double) updates.get("montoCuotaSemanal");
            deudor.setMontoCuotaSemanal(nuevoMontoCuotaSemanal);
        }

        // Guardar los cambios
        Deudor updatedDeudor = deudorService.saveDeudor(deudor);

        return ResponseEntity.ok(updatedDeudor);
    }
}