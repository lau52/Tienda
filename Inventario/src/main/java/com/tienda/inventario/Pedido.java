package com.tienda.inventario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data; // CRUCIAL para getters/setters
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@Builder // CRUCIAL para Pedido.builder()
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private int cantidadPedida;
    private LocalDateTime fechaPedido;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    public enum EstadoPedido {
        PENDIENTE,
        COMPLETADO
    }
}