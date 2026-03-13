package btg.fondos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import btg.fondos.model.Cliente;
import btg.fondos.model.Fondo;
import btg.fondos.model.Transaccion;
import btg.fondos.repository.ClienteRepository;
import btg.fondos.repository.FondoRepository;
import btg.fondos.repository.TransaccionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FondoServiceTest {

    @Mock
    private ClienteRepository clienteRepo;

    @Mock
    private FondoRepository fondoRepo;

    @Mock
    private TransaccionRepository transaccionRepo;

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private FondoService fondoService;



    @Test
    void suscribir_clienteNoExiste_debeRetornarError() throws Exception {
        when(clienteRepo.findById("clienteX")).thenThrow(new RuntimeException("Cliente no encontrado"));

        String resultado = fondoService.suscribir("clienteX", "fondo1", 100000.0);

        assertTrue(resultado.startsWith("Error: Cliente no encontrado"));
    }

    @Test
    void suscribir_fondoNoExiste_debeRetornarError() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId("cliente1");
        cliente.setSaldo(200000.0);
        cliente.setFondosSuscritos(new ArrayList<>());

        when(clienteRepo.findById("cliente1")).thenReturn(cliente);
        when(fondoRepo.findById("fondoX")).thenThrow(new RuntimeException("Fondo no encontrado"));

        String resultado = fondoService.suscribir("cliente1", "fondoX", 100000.0);

        assertTrue(resultado.startsWith("Error: Fondo no encontrado"));
    }

    @Test
    void suscribir_montoInsuficiente_debeRetornarError() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId("cliente1");
        cliente.setSaldo(200000.0);
        cliente.setFondosSuscritos(new ArrayList<>());

        Fondo fondo = new Fondo();
        fondo.setId("fondo1");
        fondo.setMontoMinimo(75000.0);

        when(clienteRepo.findById("cliente1")).thenReturn(cliente);
        when(fondoRepo.findById("fondo1")).thenReturn(fondo);

        String resultado = fondoService.suscribir("cliente1", "fondo1", 50000.0);

        assertTrue(resultado.startsWith("Error: Monto mínimo requerido"));
        assertTrue(cliente.getFondosSuscritos().isEmpty());
    }

    @Test
    void suscribir_saldoInsuficiente_debeRetornarError() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId("cliente1");
        cliente.setSaldo(50000.0);
        cliente.setFondosSuscritos(new ArrayList<>());

        Fondo fondo = new Fondo();
        fondo.setId("fondo1");
        fondo.setMontoMinimo(75000.0);

        when(clienteRepo.findById("cliente1")).thenReturn(cliente);
        when(fondoRepo.findById("fondo1")).thenReturn(fondo);

        String resultado = fondoService.suscribir("cliente1", "fondo1", 75000.0);

        assertTrue(resultado.startsWith("Error: No tiene saldo disponible"));
        assertTrue(cliente.getFondosSuscritos().isEmpty());
    }

    @Test
    void suscribir_exitoso_debeActualizarSaldoYGuardar() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId("cliente1");
        cliente.setSaldo(200000.0);
        cliente.setPreferenciaNotificacion("email");
        cliente.setFondosSuscritos(new ArrayList<>());

        Fondo fondo = new Fondo();
        fondo.setId("fondo1");
        fondo.setNombre("FPV_BTG_PACTUAL_RECAUDADORA");
        fondo.setMontoMinimo(75000.0);

        when(clienteRepo.findById("cliente1")).thenReturn(cliente);
        when(fondoRepo.findById("fondo1")).thenReturn(fondo);

        String resultado = fondoService.suscribir("cliente1", "fondo1", 100000.0);

        assertEquals("Suscripción realizada con éxito", resultado);
        assertEquals(100000.0, cliente.getSaldo());
        assertEquals(1, cliente.getFondosSuscritos().size());
        assertEquals("fondo1", cliente.getFondosSuscritos().get(0).getFondoId());

        verify(clienteRepo).save(cliente);
        verify(transaccionRepo).save(any(Transaccion.class));
        verify(notificacionService).enviar(eq("email"), contains("Suscripción realizada"));
    }

    // ---------- PRUEBAS DE CANCELACIÓN ----------

    @Test
    void cancelar_exitoso_debeActualizarSaldoYGuardarTransaccion() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId("cliente1");
        cliente.setSaldo(100000.0);
        cliente.setPreferenciaNotificacion("email");
        cliente.setFondosSuscritos(new ArrayList<>());

        when(clienteRepo.findById("cliente1")).thenReturn(cliente);

        String resultado = fondoService.cancelar("cliente1", "fondo1", 50000.0);

        assertEquals("Cancelación realizada con éxito", resultado);
        assertEquals(150000.0, cliente.getSaldo());

        verify(clienteRepo).save(cliente);
        verify(transaccionRepo).save(any(Transaccion.class));
        verify(notificacionService).enviar(eq("email"), contains("Cancelación realizada"));
    }

    @Test
    void cancelar_clienteNoExiste_debeLanzarExcepcion() throws Exception {
        when(clienteRepo.findById("clienteX")).thenThrow(new RuntimeException("Cliente no encontrado"));

        assertThrows(RuntimeException.class, () -> fondoService.cancelar("clienteX", "fondo1", 50000.0));
    }



    @Test
    void historial_debeRetornarListaTransacciones() throws Exception {
        Transaccion tx1 = new Transaccion();
        tx1.setId(UUID.randomUUID().toString());
        tx1.setClienteId("cliente1");
        tx1.setFondoId("fondo1");
        tx1.setTipo("apertura");
        tx1.setMonto(75000.0);
        tx1.setFecha(new Date());

        Transaccion tx2 = new Transaccion();
        tx2.setId(UUID.randomUUID().toString());
        tx2.setClienteId("cliente1");
        tx2.setFondoId("fondo1");
        tx2.setTipo("cancelacion");
        tx2.setMonto(50000.0);
        tx2.setFecha(new Date());

        List<Transaccion> lista = List.of(tx1, tx2);

        when(transaccionRepo.findByClienteId("cliente1")).thenReturn(lista);

        List<Transaccion> resultado = fondoService.historial("cliente1");

        assertEquals(2, resultado.size());
        assertEquals("apertura", resultado.get(0).getTipo());
        assertEquals("cancelacion", resultado.get(1).getTipo());
    }
}
