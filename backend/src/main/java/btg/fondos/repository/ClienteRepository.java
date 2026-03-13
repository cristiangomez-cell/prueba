package btg.fondos.repository;

import btg.fondos.model.Cliente;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ClienteRepository {

    @Autowired
    private Firestore firestore;

    public Cliente findById(String clienteId) throws Exception {
        DocumentSnapshot snapshot = firestore.collection("clientes").document(clienteId).get().get();
        if (snapshot.exists()) {
            return snapshot.toObject(Cliente.class);
        }
        throw new RuntimeException("Cliente no encontrado");
    }

    public void save(Cliente cliente) {
        firestore.collection("clientes").document(cliente.getId()).set(cliente);
    }
}

