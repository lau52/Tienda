package com.tienda.inventario;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EstadisticasService {

    private final DetalleVentaRepository detalleVentaRepository;
    private final VentaRepository ventaRepository;

    public EstadisticasService(DetalleVentaRepository detalleVentaRepository, VentaRepository ventaRepository) {
        this.detalleVentaRepository = detalleVentaRepository;
        this.ventaRepository = ventaRepository;
    }

    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();

        // (c) Ingresos totales - Solución con lambda en lugar de referencia al método
        BigDecimal ingresosTotales = ventaRepository.findAll().stream()
        
            .map(venta -> venta.getTotalIngresos()) 
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        estadisticas.put("ingresosTotales", ingresosTotales.setScale(2, RoundingMode.HALF_UP));
        
        // ... (resto de la lógica de estadísticas, asumiendo que ya funciona)
        // ...
        
        return estadisticas;
    }
}