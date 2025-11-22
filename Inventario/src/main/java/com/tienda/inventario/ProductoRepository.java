package com.tienda.inventario;
import com.tienda.inventario.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
