package btg.fondos.model;

import lombok.Data;


import java.util.Date;

@Data
public class Transaccion {
    private String id;
    private String clienteId;
    private String fondoId;
    private String tipo;
    private Double monto;
    private Date fecha;
}
