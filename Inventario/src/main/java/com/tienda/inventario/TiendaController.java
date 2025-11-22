package com.tienda.inventario;
import com.tienda.inventario.modelos.Estadisticas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173") // Asumiendo que el frontend React corre en 5173
public class TiendaController {

    private final ProductoService productoService;

    @Autowired
    public TiendaController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // --- GETTERS (Lectura de datos) ---

    @GetMapping("/productos")
    public List<Producto> obtenerProductos() {
        return productoService.obtenerTodosLosProductos();
    }

    @GetMapping("/estadisticas")
    public Estadisticas obtenerEstadisticas() {
        return productoService.obtenerEstadisticas();
    }
    
    // --- POST/PUT (Escritura de datos) ---

    // DTO para recibir la cantidad de la venta
    public static class VentaRequest {
        private int cantidad;
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    }

    @PostMapping("/productos/vender/{id}")
    public ResponseEntity<?> venderProducto(@PathVariable Long id, @RequestBody VentaRequest ventaRequest) {
        
        // Se llama al método findById que ya está definido en la interfaz ProductoService
        Optional<Producto> productoOptional = productoService.findById(id);

        if (productoOptional.isEmpty()) {
            // Si no se encuentra el producto, retorna un 404
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "Producto no encontrado con ID: " + id));
        }

        Producto producto = productoOptional.get();

        // Si la cantidad es inválida, retorna un 400 antes de intentar la venta
        if (ventaRequest.getCantidad() <= 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "La cantidad a vender debe ser positiva."));
        }
        
      
        // Se llama al método procesarVenta que ya está definido en la interfaz ProductoService
        try {
            Venta resultadoVenta = productoService.procesarVenta(producto, ventaRequest.getCantidad());
            // Retorna un 200 OK con el objeto Venta que contiene los detalles de la transacción
            return ResponseEntity.ok(resultadoVenta);
        } catch (IllegalArgumentException e) {
            //  Stock insuficiente y retorna un 400 Bad Request
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }
    
    @PostMapping("/productos/guardar")
    public Producto guardarProducto(@RequestBody Producto producto) {
        return productoService.guardarProducto(producto);
    }
}