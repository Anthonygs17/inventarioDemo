package com.ejemplo.demo.dto;

import com.ejemplo.demo.model.MovimientoStock;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoResponse {
    private MovimientoStock movimiento;
    private StockResponseDto stock;
    private boolean alerta; // true si queda por debajo del pto de reorden
}
