package com.tienda.inventario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    @Query("SELECT d.producto.nombre, SUM(d.cantidadVendida) AS totalVendido " +
           "FROM DetalleVenta d GROUP BY d.producto.nombre ORDER BY totalVendido DESC")
    List<Object[]> findProductosVendidosStats();

    @Query("SELECT SUM(d.cantidadVendida) FROM DetalleVenta d")
    Long sumTotalUnidadesVendidas();
}