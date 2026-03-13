package btg.fondos.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import btg.fondos.controller.FondoController;
import btg.fondos.model.Transaccion;
import btg.fondos.service.FondoService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class FondoControllerTest {

    @Mock
    private FondoService fondoService;

    @InjectMocks
    private FondoController fondoController;

    public FondoControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void suscribir_exitoso_debeRetornar200() {
        when(fondoService.suscribir("cliente1", "fondo1", 100000.0))
                .thenReturn("Suscripción realizada con éxito");

        ResponseEntity<String> response = fondoController.suscribir("fondo1", "cliente1", 100000.0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Suscripción realizada con éxito", response.getBody());
    }

    @Test
    void suscribir_error_debeRetornar400() {
        when(fondoService.suscribir("cliente1", "fondo1", 50000.0))
                .thenReturn("Error: Monto mínimo requerido: 75000");

        ResponseEntity<String> response = fondoController.suscribir("fondo1", "cliente1", 50000.0);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().startsWith("Error:"));
    }

    @Test
    void cancelar_exitoso_debeRetornar200() throws Exception {
        when(fondoService.cancelar("cliente1", "fondo1", 50000.0))
                .thenReturn("Cancelación realizada con éxito");

        ResponseEntity<String> response = fondoController.cancelar("fondo1", "cliente1", 50000.0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cancelación realizada con éxito", response.getBody());
    }

    @Test
    void historial_debeRetornarListaTransacciones() throws Exception {
        Transaccion tx = new Transaccion();
        tx.setId("tx1");
        tx.setClienteId("cliente1");
        tx.setFondoId("fondo1");
        tx.setTipo("apertura");
        tx.setMonto(75000.0);
        tx.setFecha(new java.util.Date());

        when(fondoService.historial("cliente1")).thenReturn(java.util.List.of(tx));

        ResponseEntity<java.util.List<Transaccion>> response = fondoController.historial("cliente1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("apertura", response.getBody().get(0).getTipo());
    }
}
