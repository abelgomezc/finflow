package ec.com.finflow.notifications.service;

import java.util.Map;

/**
 * Servicio de envío de emails.
 */
public interface EmailService {

    /**
     * Envía un email usando un template.
     *
     * @param to          Destinatario
     * @param subject     Asunto
     * @param templateName Nombre del template Thymeleaf
     * @param variables   Variables para el template
     */
    void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables);

    /**
     * Envía un email simple.
     *
     * @param to      Destinatario
     * @param subject Asunto
     * @param body    Cuerpo del mensaje
     */
    void sendSimpleEmail(String to, String subject, String body);
}
