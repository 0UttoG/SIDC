package com.SIDC.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarFacturaSimple(String destinatario, String cuerpo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Factura de Venta - SIDC");
        message.setText(cuerpo);

        mailSender.send(message);
        System.out.println("Correo enviado exitosamente a: " + destinatario);
    }
}