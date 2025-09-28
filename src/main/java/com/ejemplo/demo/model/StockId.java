package com.ejemplo.demo.model;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Column;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockId implements Serializable {
    @Column(name = "id_sucursal")
    private Long idSucursal;

    @Column(name = "cod_producto")
    private String codProducto;
}
