package com.tienda.inventario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data; // CRUCIAL para getters/setters
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Data
@Builder // CRUCIAL para DetalleVenta.builder()
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private int cantidadVendida;
    private BigDecimal precioUnidadFinal; // ¡Asegúrate de que este campo exista!
    private BigDecimal subtotal;
}