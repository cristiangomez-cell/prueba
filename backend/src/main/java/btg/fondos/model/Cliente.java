package btg.fondos.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Cliente {
    private String id;
    private String nombre;
    private Double saldo;
    private String preferenciaNotificacion;
    private List<FondoSuscrito> fondosSuscritos = new ArrayList<>();


}
