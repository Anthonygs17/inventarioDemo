package com.ejemplo.demo.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_sucursal")
    private Long idSucursal;

    @Column(name = "id_producto")
    private Long idProducto;

    @Enumerated(EnumType.STRING)
    private MovimientoTipo tipo;

    private Integer cantidad;

    private LocalDateTime fecha;
}
