package com.ejemplo.demo.controller;

import com.ejemplo.demo.dto.*;
import com.ejemplo.demo.model.Sucursal;
import com.ejemplo.demo.service.InventoryService;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Validated
public class InventoryController {
    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    // GET /api/sucursales?distrito=
    @GetMapping("/sucursales")
    public ResponseEntity<List<Sucursal>> getSucursales(@RequestParam(required = false) String distrito) {
        List<Sucursal> result = service.getSucursales(Optional.ofNullable(distrito));
        return ResponseEntity.ok(result);
    }

    // GET /api/stock?producto=&distrito=
    @GetMapping("/stock")
    public ResponseEntity<List<StockResponseDto>> getStock(
            @RequestParam(required = false) Long producto,
            @RequestParam(required = false) String distrito) {
        List<StockResponseDto> result = service.getStock(Optional.ofNullable(producto), Optional.ofNullable(distrito));
        return ResponseEntity.ok(result);
    }

    // POST /api/movimientos
    @PostMapping("/movimientos")
    public ResponseEntity<MovimientoResponse> crearMovimiento(@Valid @RequestBody MovimientoRequest req) {
        MovimientoResponse resp = service.crearMovimiento(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
}
