package btg.fondos.service;

import org.springframework.stereotype.Service;

@Service
public class NotificacionService {
    public void enviar(String tipo, String mensaje) {
        if ("email".equalsIgnoreCase(tipo)) {
            System.out.println("Enviando email: " + mensaje);
        } else if ("sms".equalsIgnoreCase(tipo)) {
            System.out.println("Enviando SMS: " + mensaje);
        }
    }
}

