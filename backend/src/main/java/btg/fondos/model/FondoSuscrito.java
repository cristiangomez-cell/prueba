package btg.fondos.model;

import lombok.Data;


import java.util.Date;

@Data
public class FondoSuscrito {
    private String id;
    private String fondoId;
    private Double montoVinculado;
    private Date fechaSuscripcion;
}