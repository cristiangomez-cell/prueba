package btg.fondos.repository;

import btg.fondos.model.Cliente;
import btg.fondos.model.Fondo;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FondoRepository {

    @Autowired
    private Firestore firestore;

    public Fondo findById(String fondoId) throws Exception {
        DocumentSnapshot snapshot = firestore.collection("fondos").document(fondoId).get().get();
        if (snapshot.exists()) {
            return snapshot.toObject(Fondo.class);
        }
        throw new RuntimeException("Fondo no encontrado");
    }
    public void save(Fondo fondo) {
        firestore.collection("fondos").document(fondo.getId()).set(fondo);
    }
}

