package com.tienda.inventario;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;


@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final PedidoService pedidoService;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    public ProductoService(ProductoRepository productoRepository, PedidoService pedidoService, 
                           VentaRepository ventaRepository, DetalleVentaRepository detalleVentaRepository) {
        this.productoRepository = productoRepository;
        this.pedidoService = pedidoService;
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
    }

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    // CA1: Cálculo del Precio Final (con IVA) - CORREGIDO
    public BigDecimal calcularPrecioFinal(Producto producto) {
        // Usa el getter de Lombok (getTipoProducto)
        if (producto.getTipoProducto() == null) {
            return producto.getPrecioBase(); 
        }
        
        BigDecimal precioBase = producto.getPrecioBase();
        
        // Corrección de tipos: Usar "100" como String para crear un BigDecimal
        BigDecimal divisorCien = new BigDecimal("100"); 

        // Usa el getter de Lombok (getTipoProducto)
        BigDecimal iva = producto.getTipoProducto().getIvaPorcentaje()
            .divide(divisorCien, 4, RoundingMode.HALF_UP);
        
        // Corrección de tipos: Usar BigDecimal.ONE para representar 1
        return precioBase.multiply(BigDecimal.ONE.add(iva)).setScale(2, RoundingMode.HALF_UP);
    }

    // CA2, CA3, CA4, CA5: Lógica de Venta
    @Transactional
    public Venta vender(Long productoId, int cantidad) {
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado."));
            

        // CA2: Validación de Stock - Usa el getter de Lombok
        if (producto.getCantidadStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente. Disponibles: " + producto.getCantidadStock());
        }

        // CA1: Cálculo del Precio Final
        BigDecimal precioFinal = calcularPrecioFinal(producto);
        BigDecimal subtotal = precioFinal.multiply(new BigDecimal(cantidad));
        

        // 1. Crear Venta (CA4 - Encabezado)
        Venta venta = Venta.builder()
            .fechaVenta(LocalDateTime.now())
            .totalIngresos(subtotal)
            .build();
        venta = ventaRepository.save(venta);
        
        // 2. Crear DetalleVenta (CA4 - Detalle)
        DetalleVenta detalle = DetalleVenta.builder()
            .producto(producto)
            .cantidadVendida(cantidad)
            .precioUnidadFinal(precioFinal)
            .subtotal(subtotal)
            .venta(venta)
            .build();
        detalleVentaRepository.save(detalle);
        
        venta.setDetalles(Collections.singletonList(detalle)); 

        // 3. Actualizar Stock (CA3) - Usa el getter de Lombok
        producto.setCantidadStock(producto.getCantidadStock() - cantidad);
        productoRepository.save(producto);

        // 4. Chequeo de Pedido (CA5) - Usa los getters de Lombok
        if (producto.getCantidadStock() <= producto.getStockMinimoPedido()) {
            // Se asume una cantidad de pedido de 20 unidades por defecto
            pedidoService.crearPedido(producto, 20); 
        }

        return venta;
    }
}