// Archivo: src/main/java/com/SIDC/backend/services/EmailService.java
package com.SIDC.backend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Método original (por si lo usas en otro lado)
    public void enviarCorreo(String to, String subject, String text) {
        // ... tu código original ...
    }

    // 👇 NUEVO MÉTODO PARA ADJUNTAR LA FACTURA PDF
    public void enviarFacturaConAdjunto(String to, String subject, String text, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // El 'true' indica que este correo lleva contenido multipart (adjuntos)
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            // Convertimos los bytes del PDF en un archivo adjunto
            ByteArrayResource pdfAttachment = new ByteArrayResource(pdfBytes);
            helper.addAttachment("Factura_SIDC.pdf", pdfAttachment);

            mailSender.send(message);
            System.out.println("Correo con factura enviado a: " + to);

        } catch (MessagingException e) {
            System.err.println("Error al adjuntar el PDF en el correo: " + e.getMessage());
        }
    }
}