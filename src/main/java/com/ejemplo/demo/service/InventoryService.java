package com.ejemplo.demo.service;

import com.ejemplo.demo.dto.*;
import com.ejemplo.demo.exception.*;
import com.ejemplo.demo.model.*;
import com.ejemplo.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryService {
    private final SucursalRepository sucursalRepository;
    private final StockRepository stockRepository;
    private final MovimientoStockRepository movimientoRepo;

    public InventoryService(SucursalRepository sucursalRepository,
                            StockRepository stockRepository,
                            MovimientoStockRepository movimientoRepo) {
        this.sucursalRepository = sucursalRepository;
        this.stockRepository = stockRepository;
        this.movimientoRepo = movimientoRepo;
    }

    public List<Sucursal> getSucursales(Optional<String> distrito) {
        return distrito.map(s -> sucursalRepository.findByDistritoIgnoreCase(s))
                .orElseGet(() -> sucursalRepository.findAll());
    }

    public List<StockResponseDto> getStock(Optional<Long> productoOpt, Optional<String> distritoOpt) {
        List<Long> sucursalIds = null;
        if (distritoOpt.isPresent()) {
            List<Sucursal> sucursales = sucursalRepository.findByDistritoIgnoreCase(distritoOpt.get());
            sucursalIds = sucursales.stream().map(Sucursal::getIdSucursal).collect(Collectors.toList());
            if (sucursalIds.isEmpty()) return Collections.emptyList();
        }

        List<Stock> stocks;
        if (productoOpt.isPresent() && sucursalIds != null) {
            stocks = stockRepository.findByIdIdProductoAndIdIdSucursalIn(productoOpt.get(), sucursalIds);
        } else if (productoOpt.isPresent()) {
            stocks = stockRepository.findByIdIdProducto(productoOpt.get());
        } else if (sucursalIds != null) {
            stocks = stockRepository.findByIdIdSucursalIn(sucursalIds);
        } else {
            stocks = stockRepository.findAll();
        }

        // Enriquecer con distrito
        Map<Long, String> sucursalMap = sucursalRepository.findAll().stream()
                .collect(Collectors.toMap(Sucursal::getIdSucursal, Sucursal::getDistrito));

        return stocks.stream()
                .map(s -> {
                    StockResponseDto dto = new StockResponseDto();
                    dto.setIdSucursal(s.getId().getIdSucursal());
                    dto.setIdProducto(s.getId().getIdProducto());
                    dto.setStockDisponible(s.getStockDisponible());
                    dto.setPtoReorden(s.getPtoReorden());
                    dto.setDistrito(sucursalMap.get(s.getId().getIdSucursal()));
                    dto.setDebajoPtoReorden(s.getPtoReorden() != null && s.getStockDisponible() < s.getPtoReorden());
                    return dto;
                }).collect(Collectors.toList());
    }

    @Transactional
    public MovimientoResponse crearMovimiento(MovimientoRequest req) {
        // validar sucursal
        Sucursal sucursal = sucursalRepository.findById(req.getIdSucursal())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + req.getIdSucursal()));

        // buscar stock con lock para evitar race conditions
        Optional<Stock> stockOpt = stockRepository.findForUpdate(req.getIdSucursal(), req.getIdProducto());

        Stock stock;
        if (stockOpt.isPresent()) {
            stock = stockOpt.get();
        } else {
            // Si no existe, permitimos crear solamente si es INGRESO; para SALIDA -> error
            if (req.getTipo() == MovimientoTipo.SALIDA) {
                throw new InsufficientStockException("No existe stock para la sucursal/producto indicado");
            }
            stock = Stock.builder()
                    .id(new StockId(req.getIdSucursal(), req.getIdProducto()))
                    .stockDisponible(0)
                    .ptoReorden(10) // valor por defecto (puedes cambiarlo o recibirlo)
                    .build();
        }

        if (req.getTipo() == MovimientoTipo.INGRESO) {
            stock.setStockDisponible(stock.getStockDisponible() + req.getCantidad());
        } else { // SALIDA
            if (stock.getStockDisponible() < req.getCantidad()) {
                throw new InsufficientStockException("Stock insuficiente. Disponible=" + stock.getStockDisponible());
            }
            stock.setStockDisponible(stock.getStockDisponible() - req.getCantidad());
        }

        stockRepository.save(stock);

        MovimientoStock mov = MovimientoStock.builder()
                .idSucursal(req.getIdSucursal())
                .idProducto(req.getIdProducto())
                .tipo(req.getTipo())
                .cantidad(req.getCantidad())
                .fecha(LocalDateTime.now())
                .build();

        mov = movimientoRepo.save(mov);

        boolean alerta = stock.getPtoReorden() != null && stock.getStockDisponible() < stock.getPtoReorden();

        StockResponseDto stockDto = StockResponseDto.builder()
                .idSucursal(stock.getId().getIdSucursal())
                .idProducto(stock.getId().getIdProducto())
                .stockDisponible(stock.getStockDisponible())
                .ptoReorden(stock.getPtoReorden())
                .distrito(sucursal.getDistrito())
                .debajoPtoReorden(alerta)
                .build();

        return MovimientoResponse.builder()
                .movimiento(mov)
                .stock(stockDto)
                .alerta(alerta)
                .build();
    }
}
