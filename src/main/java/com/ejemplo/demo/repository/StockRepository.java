package com.ejemplo.demo.repository;

import com.ejemplo.demo.model.Stock;
import com.ejemplo.demo.model.StockId;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, StockId> {
    List<Stock> findByIdIdProducto(Long idProducto);

    List<Stock> findByIdIdProductoAndIdIdSucursalIn(Long idProducto, List<Long> sucursalIds);

    List<Stock> findByIdIdSucursalIn(List<Long> sucursalIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s WHERE s.id.idSucursal = :idSucursal AND s.id.idProducto = :idProducto")
    Optional<Stock> findForUpdate(@Param("idSucursal") Long idSucursal, @Param("idProducto") Long idProducto);
}
