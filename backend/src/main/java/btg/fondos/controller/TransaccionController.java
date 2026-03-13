package btg.fondos.controller;

import btg.fondos.model.Transaccion;
import btg.fondos.repository.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transacciones")
public class TransaccionController {

    @Autowired
    private TransaccionRepository transaccionRepository;

    @PostMapping
    public ResponseEntity<String> crearTransaccion(@RequestBody Transaccion transaccion) {
        transaccionRepository.save(transaccion);
        return ResponseEntity.ok("Transacción registrada con éxito");
    }
}

