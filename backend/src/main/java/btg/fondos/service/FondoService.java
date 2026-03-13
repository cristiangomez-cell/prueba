package btg.fondos.service;

import btg.fondos.model.Cliente;
import btg.fondos.model.Fondo;
import btg.fondos.model.FondoSuscrito;
import btg.fondos.model.Transaccion;
import btg.fondos.repository.ClienteRepository;
import btg.fondos.repository.FondoRepository;
import btg.fondos.repository.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.time.LocalTime.now;

@Service
public class FondoService {

    @Autowired
    private ClienteRepository clienteRepo;
    @Autowired
    private FondoRepository fondoRepo;
    @Autowired
    private TransaccionRepository transaccionRepo;
    @Autowired
    private NotificacionService notificacionService;

    public String suscribir(String clienteId, String fondoId, Double monto) {
        Cliente cliente;
        Fondo fondo;

        try {
            cliente = clienteRepo.findById(clienteId);
        } catch (Exception e) {
            return "Error: Cliente no encontrado";
        }

        try {
            fondo = fondoRepo.findById(fondoId);
        } catch (Exception e) {
            return "Error: Fondo no encontrado";
        }

        if (monto < fondo.getMontoMinimo()) {
            return "Error: Monto mínimo requerido: " + fondo.getMontoMinimo();
        }
        if (cliente.getSaldo() < monto) {
            return "Error: No tiene saldo disponible para vincularse al fondo " + fondo.getNombre();
        }

        // Actualizar saldo y suscripción
        cliente.setSaldo(cliente.getSaldo() - monto);
        FondoSuscrito fs = new FondoSuscrito();
        fs.setId(UUID.randomUUID().toString());
        fs.setFondoId(fondoId);
        fs.setMontoVinculado(monto);
        cliente.getFondosSuscritos().add(fs);
        clienteRepo.save(cliente);

        // Registrar transacción
        Transaccion tx = new Transaccion();
        tx.setId(UUID.randomUUID().toString());
        tx.setClienteId(clienteId);
        tx.setFondoId(fondoId);
        tx.setTipo("apertura");
        tx.setMonto(monto);
        tx.setFecha(new Date());
        transaccionRepo.save(tx);

        // Notificación
        notificacionService.enviar(cliente.getPreferenciaNotificacion(),
                "Suscripción realizada al fondo " + fondo.getNombre());

        return "Suscripción realizada con éxito";
    }


    public String cancelar(String clienteId, String fondoId, Double monto) throws Exception {
        Cliente cliente = clienteRepo.findById(clienteId);

        cliente.setSaldo(cliente.getSaldo() + monto);
        clienteRepo.save(cliente);

        Transaccion tx = new Transaccion();
        tx.setId(UUID.randomUUID().toString());
        tx.setClienteId(clienteId);
        tx.setFondoId(fondoId);
        tx.setTipo("cancelacion");
        tx.setMonto(monto);
        tx.setFecha(new Date());
        transaccionRepo.save(tx);

        notificacionService.enviar(cliente.getPreferenciaNotificacion(),
                "Cancelación realizada del fondo " + fondoId);

        return "Cancelación realizada con éxito";
    }

    public List<Transaccion> historial(String clienteId) throws Exception {
        return transaccionRepo.findByClienteId(clienteId);
    }
}

