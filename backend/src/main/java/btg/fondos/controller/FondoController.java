package btg.fondos.controller;

import btg.fondos.model.Fondo;
import btg.fondos.model.Transaccion;
import btg.fondos.repository.FondoRepository;
import btg.fondos.service.FondoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fondos")
public class FondoController {

    @Autowired
    private FondoService fondoService;

    @Autowired
    private FondoRepository fondoRepository;

    @PostMapping("/{fondoId}/suscribir")
    public ResponseEntity<String> suscribir(
            @PathVariable String fondoId,
            @RequestParam String clienteId,
            @RequestParam Double monto) {

        String resultado = fondoService.suscribir(clienteId, fondoId, monto);

        if (resultado.startsWith("Error:")) {
            return ResponseEntity.badRequest().body(resultado);
        }

        return ResponseEntity.ok(resultado);
    }


    @PostMapping("/{fondoId}/cancelar")
    public ResponseEntity<String> cancelar(@PathVariable String fondoId,
                                           @RequestParam String clienteId,
                                           @RequestParam Double monto) throws Exception {
        return ResponseEntity.ok(fondoService.cancelar(clienteId, fondoId, monto));
    }

    @GetMapping("/transacciones/{clienteId}")
    public ResponseEntity<List<Transaccion>> historial(@PathVariable String clienteId) throws Exception {
        return ResponseEntity.ok(fondoService.historial(clienteId));
    }

    @PostMapping
    public ResponseEntity<String> crearFondo(@RequestBody Fondo fondo) {
        fondoRepository.save(fondo);
        return ResponseEntity.ok("Fondo creado con éxito");
    }
}
