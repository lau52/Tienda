import React, { useState, useEffect, useCallback } from 'react';

// --- CONFIGURACIÓN ---
// URL de tu Backend Spring Boot. 
const API_BASE_URL = 'http://localhost:8080/api'; 

const App = () => {
    // --- ESTADOS ---
    const [productos, setProductos] = useState([]);
    const [estadisticas, setEstadisticas] = useState({
        ingresosTotales: '0',
        productoMasVendido: 'N/A',
        productoMenosVendidos: 'N/A',
        promedioVentas: '0'
    });
    const [loading, setLoading] = useState(true);
    const [mensaje, setMensaje] = useState(null); // Para mostrar errores o éxitos

    // --- FUNCIONES DE API ---

    // Cargar datos iniciales (Productos y Estadísticas)
    const cargarDatos = useCallback(async () => {
        setLoading(true);
        try {
            // Hacemos las dos peticiones en paralelo
            const [resProductos, resEstadisticas] = await Promise.all([
                fetch(`${API_BASE_URL}/productos`),
                fetch(`${API_BASE_URL}/estadisticas`)
            ]);

            if (!resProductos.ok || !resEstadisticas.ok) {
                // Verificar si hay un error 404, que suele ser problema de configuración de Spring Boot o Cors
                if (resProductos.status === 404 || resEstadisticas.status === 404) {
                    throw new Error("Rutas API no encontradas (Error 404). Verifica que Spring Boot esté activo y en el puerto 8080.");
                }
                throw new Error("Error al conectar con el servidor. Código: " + resProductos.status);
            }

            const dataProductos = await resProductos.json();
            const dataEstadisticas = await resEstadisticas.json();

            setProductos(dataProductos);
            setEstadisticas(dataEstadisticas);
            setMensaje(null); 
        } catch (error) {
            console.error("Error cargando datos:", error);
            setMensaje({ tipo: 'error', texto: `Error de conexión: ${error.message}` });
        } finally {
            setLoading(false);
        }
    }, []);

    // Ejecutar carga al iniciar
    useEffect(() => {
        cargarDatos();
    }, [cargarDatos]);

    // Función para Vender
    const handleVender = async (productoId, cantidad) => {
        if (!cantidad || cantidad <= 0) {
            setMensaje({ tipo: 'error', texto: 'Por favor ingrese una cantidad válida.' });
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/productos/vender/${productoId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ cantidad: parseInt(cantidad) }),
            });

            if (response.ok) {
                const resultado = await response.json();
                setMensaje({ tipo: 'exito', texto: `¡Venta exitosa! ID: ${resultado.ventaId}. Total: $${resultado.totalIngresos}` });
                cargarDatos(); // Recargar la tabla y estadísticas
            } else {
                const errorData = await response.json();
                // Usamos el sistema de mensajes para mostrar el error de negocio del backend
                setMensaje({ tipo: 'error', texto: `Error en la venta: ${errorData.message}` });
            }
        } catch (error) {
            console.error("Error en venta:", error);
            // Usamos el sistema de mensajes para mostrar el error de conexión
            setMensaje({ tipo: 'error', texto: 'Error de conexión al intentar vender. Verifique que el backend esté activo.' });
        }
    };

    // Función que simula la revisión de pedidos (el backend lo hace)
    const handlePedir = () => {
        setMensaje({ tipo: 'info', texto: 'El sistema ya verifica y crea pedidos automáticamente si el stock baja del límite mínimo.' });
    };

    // --- CÁLCULOS AUXILIARES ---
    const calcularPrecioFinal = (producto) => {
        if (!producto.tipoProducto || !producto.precioBase) return 0;
        // Asumiendo que ivaPorcentaje ya viene en formato 0.xx (ej. 0.19)
        const iva = producto.tipoProducto.ivaPorcentaje || 0; 
        return (producto.precioBase * (1 + iva)).toFixed(2);
    };

    // --- RENDERIZADO ---

    return (
        <div className="min-h-screen bg-gray-100 p-8 font-sans text-gray-800">
            <div className="max-w-6xl mx-auto bg-white shadow-xl rounded-xl overflow-hidden">
                
                {/* Encabezado */}
                <div className="bg-blue-700 p-6 text-white">
                    <h1 className="text-3xl font-extrabold flex items-center gap-3">
                        🏪 Sistema de Inventario
                    </h1>
                    <p className="opacity-90 mt-1">Integración Spring Boot & React.</p>
                </div>

                {/* Mensajes de Estado */}
                {mensaje && (
                    <div className={`p-4 font-medium ${
                        mensaje.tipo === 'error' ? 'bg-red-200 text-red-800' : 
                        mensaje.tipo === 'exito' ? 'bg-green-200 text-green-800' : 
                        'bg-blue-200 text-blue-800'
                    }`}>
                        {mensaje.texto}
                    </div>
                )}

                <div className="p-8 grid gap-10">
                    
                    {/* SECCIÓN 1: TABLA DE PRODUCTOS */}
                    <section>
                        <div className="flex justify-between items-center mb-5">
                            <h2 className="text-2xl font-bold text-gray-700 border-l-4 border-blue-500 pl-3">
                                📦 Inventario y Ventas
                            </h2>
                            <button 
                                onClick={cargarDatos}
                                className="bg-gray-200 hover:bg-gray-300 text-gray-700 px-4 py-2 rounded-lg transition shadow-md flex items-center gap-2 text-sm"
                            >
                                ↻ Recargar Datos
                            </button>
                        </div>

                        <div className="overflow-x-auto border rounded-lg shadow-inner">
                            <table className="min-w-full text-left border-collapse">
                                <thead>
                                    <tr className="bg-gray-100 text-gray-600 uppercase text-xs font-semibold tracking-wider">
                                        <th className="py-3 px-4">Producto</th>
                                        <th className="py-3 px-4 text-center">Stock</th>
                                        <th className="py-3 px-4 text-center">Mínimo</th>
                                        <th className="py-3 px-4 text-center">Tipo</th>
                                        <th className="py-3 px-4 text-right">Precio Base</th>
                                        <th className="py-3 px-4 text-right font-bold text-blue-600">Precio Final</th>
                                        <th className="py-3 px-4 text-center">Acción</th>
                                    </tr>
                                </thead>
                                <tbody className="text-gray-700 text-sm divide-y divide-gray-200">
                                    {loading ? (
                                        <tr><td colSpan="7" className="text-center py-10 text-lg text-gray-500">Cargando inventario...</td></tr>
                                    ) : productos.length === 0 ? (
                                        <tr><td colSpan="7" className="text-center py-10 text-lg text-red-500">No hay productos disponibles.</td></tr>
                                    ) : (
                                        productos.map((p) => (
                                            <FilaProducto 
                                                key={p.id} 
                                                producto={p} 
                                                calcularPrecio={calcularPrecioFinal}
                                                onVender={handleVender}
                                            />
                                        ))
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </section>

                    {/* SECCIÓN 2: ESTADÍSTICAS Y OPERACIONES */}
                    <div className="grid md:grid-cols-3 gap-8">
                        
                        {/* Indicadores Clave */}
                        <EstadisticaCard 
                            title="Ingresos Totales"
                            value={`$${estadisticas.ingresosTotales}`}
                            color="green"
                        />
                        <EstadisticaCard 
                            title="Promedio de Venta"
                            value={`$${estadisticas.promedioVentas}`}
                            color="purple"
                        />
                        <EstadisticaCard 
                            title="Producto Más Vendido"
                            value={estadisticas.productoMasVendido}
                            color="orange"
                            isText
                        />

                    </div>
                </div>
                
                <div className="bg-gray-100 p-4 text-center text-gray-500 text-xs border-t">
                    © 2025 Sistema de Gestión de Inventarios | Desarrollado con Spring Boot y React
                </div>
            </div>
        </div>
    );
};

// Componente para la Fila del Producto
const FilaProducto = ({ producto, calcularPrecio, onVender }) => {
    const [cantidad, setCantidad] = useState(1);
    const precioFinal = calcularPrecio(producto);
    const necesitaPedido = producto.cantidadStock <= producto.stockMinimoPedido;

    return (
        <tr className="hover:bg-blue-50 transition duration-150">
            <td className="py-3 px-4 font-medium text-gray-900">{producto.nombre}</td>
            <td className="py-3 px-4 text-center">
                <span className={`font-extrabold ${necesitaPedido ? 'text-red-600' : 'text-green-600'}`}>
                    {producto.cantidadStock}
                </span>
            </td>
            <td className="py-3 px-4 text-center text-gray-500 text-xs">
                {producto.stockMinimoPedido}
            </td>
            <td className="py-3 px-4 text-center text-xs">
                <span className="bg-blue-100 text-blue-800 py-1 px-2 rounded-full font-semibold">
                    {producto.tipoProducto ? producto.tipoProducto.nombre : 'General'}
                </span>
            </td>
            <td className="py-3 px-4 text-right text-gray-500">${producto.precioBase}</td>
            <td className="py-3 px-4 text-right font-extrabold text-blue-700">${precioFinal}</td>
            <td className="py-3 px-4 text-center flex justify-center items-center gap-2">
                <input 
                    type="number" 
                    min="1" 
                    max={producto.cantidadStock}
                    value={cantidad}
                    onChange={(e) => setCantidad(Math.max(1, parseInt(e.target.value) || 1))}
                    className="w-16 border border-gray-300 rounded-lg px-2 py-1 text-center focus:border-blue-500 transition"
                    disabled={producto.cantidadStock === 0}
                />
                <button 
                    onClick={() => onVender(producto.id, cantidad)}
                    disabled={producto.cantidadStock === 0 || cantidad > producto.cantidadStock}
                    className={`text-white px-3 py-1 rounded-lg text-sm font-semibold shadow-md transition transform hover:scale-105 ${
                        producto.cantidadStock === 0 
                        ? 'bg-gray-400 cursor-not-allowed' 
                        : 'bg-green-600 hover:bg-green-700'
                    }`}
                >
                    Vender
                </button>
            </td>
        </tr>
    );
};

// Componente para las Tarjetas de Estadísticas
const EstadisticaCard = ({ title, value, color, isText = false }) => {
    let bgColor = '';
    let textColor = '';
    
    switch (color) {
        case 'green':
            bgColor = 'bg-green-500';
            textColor = 'text-green-800';
            break;
        case 'purple':
            bgColor = 'bg-purple-500';
            textColor = 'text-purple-800';
            break;
        case 'orange':
            bgColor = 'bg-orange-500';
            textColor = 'text-orange-800';
            break;
        default:
            bgColor = 'bg-gray-500';
            textColor = 'text-gray-800';
    }

    return (
        <div className={`p-6 rounded-xl shadow-lg ${bgColor} bg-opacity-10 border-l-4 border-${color}-500`}>
            <p className="text-sm font-medium text-gray-500">{title}</p>
            <p className={`mt-1 ${textColor} font-extrabold ${isText ? 'text-xl' : 'text-3xl'}`}>
                {value}
            </p>
        </div>
    );
};

export default App;