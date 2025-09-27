package com.ejemplo.demo.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {
    @EmbeddedId
    private StockId id;

    @Column(name = "stock_disponible")
    private Integer stockDisponible;

    @Column(name = "pto_reorden")
    private Integer ptoReorden;
}
