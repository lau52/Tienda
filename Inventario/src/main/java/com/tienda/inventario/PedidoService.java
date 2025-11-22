package com.tienda.inventario;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.Builder;

import java.time.LocalDateTime;
@Builder
@Service // <-- La anotación @Service debe estar JUSTO antes de la clase
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    // Inyección de dependencia del Repositorio
    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    /**
     * Crea un registro de pedido cuando el stock de un producto es bajo.
     */
    @Transactional
   
    public void crearPedido(Producto producto, int cantidad) {
        
        Pedido nuevoPedido = Pedido.builder()
            .producto(producto)
            .cantidadPedida(cantidad)
            .fechaPedido(LocalDateTime.now())
            .estado(Pedido.EstadoPedido.PENDIENTE) 
            .build();
            
        pedidoRepository.save(nuevoPedido);
    }
}