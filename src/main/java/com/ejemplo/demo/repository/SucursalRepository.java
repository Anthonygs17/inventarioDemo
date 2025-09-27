package com.ejemplo.demo.repository;

import com.ejemplo.demo.model.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    List<Sucursal> findByDistritoIgnoreCase(String distrito);
}
