package com.ejemplo.demo.dto;

import com.ejemplo.demo.model.MovimientoTipo;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoRequest {
    @NotNull
    private Long idSucursal;

    @NotNull
    private Long idProducto;

    @NotNull
    private MovimientoTipo tipo; // INGRESO o SALIDA

    @NotNull
    @Min(1)
    private Integer cantidad;
}
