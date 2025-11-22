package com.tienda.inventario;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Permite peticiones desde el puerto 3000 (típico de React/Vite) o cualquier origen ('*')
        // Si usas un puerto diferente para React, cámbialo aquí.
        registry.addMapping("/api/**")
                // Agregamos http://127.0.0.1:3000 y el comodín "*" como fallback
                .allowedOrigins("http://localhost:3000", "http://127.0.0.1:3000", "*") 
                // Aseguramos que los métodos OPTIONS, GET, POST, PUT, DELETE estén permitidos
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") 
                .allowedHeaders("*") // Permite todos los headers
                .allowCredentials(true); // Permite credenciales (útil para cookies/auth si se implementa)
    }
}