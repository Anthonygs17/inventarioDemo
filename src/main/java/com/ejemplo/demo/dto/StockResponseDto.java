package com.ejemplo.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockResponseDto {
    private Long idSucursal;
    private Long idProducto;
    private Integer stockDisponible;
    private Integer ptoReorden;
    private String distrito;
    private boolean debajoPtoReorden;
}
