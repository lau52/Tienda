package com.tienda.inventario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data; 
import lombok.NoArgsConstructor;
import java.math.BigDecimal; 

@Entity
@Data // <--- Genera todos los getters/setters que faltan (getTipoProducto, getCantidadStock, etc.)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private BigDecimal precioBase; // Usamos BigDecimal
    private int cantidadStock; 
    private int stockMinimoPedido; 

    @ManyToOne
    @JoinColumn(name = "tipo_producto_id")
    private TipoProducto tipoProducto; // <--- Clase requerida
}