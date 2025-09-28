package com.ejemplo.demo.repository;

import com.ejemplo.demo.model.Stock;
import com.ejemplo.demo.model.StockId;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, StockId> {
    List<Stock> findByIdCodProducto(String codProducto);

    List<Stock> findByIdCodProductoAndIdIdSucursalIn(String codProducto, List<Long> sucursalIds);

    List<Stock> findByIdIdSucursalIn(List<Long> sucursalIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s WHERE s.id.idSucursal = :idSucursal AND s.id.codProducto = :codProducto")
    Optional<Stock> findForUpdate(@Param("idSucursal") Long idSucursal, @Param("codProducto") String codProducto);
}
