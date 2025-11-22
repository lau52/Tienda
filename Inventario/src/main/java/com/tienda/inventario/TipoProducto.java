package com.tienda.inventario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data; 
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Data 
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    // Debe ser BigDecimal para evitar el error de 'double'
    private BigDecimal ivaPorcentaje; 
}