package btg.fondos.repository;

import btg.fondos.model.Transaccion;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransaccionRepository {

    @Autowired
    private Firestore firestore;

    public void save(Transaccion tx) {
        firestore.collection("transacciones").document(tx.getId()).set(tx);
    }

    public List<Transaccion> findByClienteId(String clienteId) throws Exception {
        List<QueryDocumentSnapshot> docs = firestore.collection("transacciones")
                .whereEqualTo("clienteId", clienteId).get().get().getDocuments();
        return docs.stream().map(d -> d.toObject(Transaccion.class)).toList();
    }
}

