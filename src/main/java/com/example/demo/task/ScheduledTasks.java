package com.example.demo.task;

import com.example.demo.model.Deudor;
import com.example.demo.repository.DeudorRepository;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ScheduledTasks {

    @Autowired
    private DeudorRepository deudorRepository;

    @Autowired
    private EmailService emailService;

    // Ejecutar todos los días a las 9:00 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkPendingPayments() {
        Date today = new Date();
        List<Deudor> deudores = deudorRepository.findAll();

        for (Deudor deudor : deudores) {
            if (deudor.getFechaProximoPago() != null && deudor.getFechaProximoPago().equals(today)) {
                if (!deudor.isCobrado()) {
                    String subject = "Recordatorio de pago pendiente";
                    String text = "Hola " + deudor.getNombreDeudor() + ",\n\n"
                            + "Este es un recordatorio de que tienes un pago pendiente de $" + deudor.getMontoCuotaSemanal()
                            + " que debía ser pagado hoy.\n\n"
                            + "Por favor, realiza el pago lo antes posible.\n\n"
                            + "Saludos,\n"
                            + "Equipo de Gestión de Deudores";
                    emailService.sendReminderEmail("correo_del_deudor@example.com", subject, text);
                }
            }
        }
    }
}