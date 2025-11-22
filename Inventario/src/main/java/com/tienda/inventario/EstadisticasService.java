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
    private final ProductoRepository productoRepository; 

    public EstadisticasService(DetalleVentaRepository detalleVentaRepository, 
                               VentaRepository ventaRepository,
                               ProductoRepository productoRepository) {
        this.detalleVentaRepository = detalleVentaRepository;
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    /**
     *  principales estadísticas de ventas e inventario.
     * @return Map<String, Object> con las métricas.
     */
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();

        // Obtener todos los datos necesarios
        List<Venta> todasLasVentas = ventaRepository.findAll();
        List<DetalleVenta> todosLosDetalles = detalleVentaRepository.findAll();
        List<Producto> todosLosProductos = productoRepository.findAll();

        // 1. Ingresos Totales (Venta::getTotalIngresos AHORA DISPONIBLE)
        BigDecimal ingresosTotales = todasLasVentas.stream()
            .map(Venta::getTotalIngresos)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        estadisticas.put("ingresosTotales", ingresosTotales.setScale(2, RoundingMode.HALF_UP));

        // 2. Total de Ventas (cantidad de transacciones)
        estadisticas.put("totalVentas", todasLasVentas.size());

        // 3. Stock Total Disponible (Producto::getCantidadStock AHORA DISPONIBLE)
        int stockTotal = todosLosProductos.stream()
            .mapToInt(Producto::getCantidadStock)
            .sum();
            
        estadisticas.put("stockTotal", stockTotal);

        // 4. Producto más vendido (por cantidad de unidades vendidas)
        Optional<Map.Entry<Producto, Long>> productoMasVendido = todosLosDetalles.stream()
            .collect(Collectors.groupingBy(DetalleVenta::getProducto,
                     Collectors.summingLong(DetalleVenta::getCantidadVendida)))
            .entrySet().stream()
            .max(Comparator.comparingLong(Map.Entry::getValue));

        if (productoMasVendido.isPresent()) {
            Map.Entry<Producto, Long> entry = productoMasVendido.get();
            Map<String, Object> detalleProducto = new HashMap<>();
            
            // Producto::getNombre AHORA DISPONIBLE
            detalleProducto.put("nombre", entry.getKey().getNombre());
            detalleProducto.put("unidadesVendidas", entry.getValue());
            
            estadisticas.put("productoMasVendido", detalleProducto);
        } else {
            estadisticas.put("productoMasVendido", "N/A");
        }

        // 5. Valor promedio de la venta (Ingresos Totales / Total de Transacciones)
        if (todasLasVentas.size() > 0) {
            BigDecimal totalVentasBD = new BigDecimal(todasLasVentas.size());
            BigDecimal promedioVenta = ingresosTotales.divide(totalVentasBD, 2, RoundingMode.HALF_UP);
            estadisticas.put("promedioVenta", promedioVenta);
        } else {
            estadisticas.put("promedioVenta", BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        }


        return estadisticas;
    }

}