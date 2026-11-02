package com.example.demo.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Producto;
import com.example.demo.repo.productoRepo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Productos", description = "Operaciones para consultar y administrar productos")
public class ProductoController {

    @Autowired
    productoRepo repo;

    @GetMapping("productos")
    @Operation(summary = "Listar productos", description = "Devuelve todos los productos registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de productos devuelta correctamente")
    public ResponseEntity<List<Producto>> findAllProductos() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping("producto")
    @Operation(summary = "Crear producto", description = "Guarda un producto nuevo.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado"),
        @ApiResponse(responseCode = "400", description = "La solicitud contiene datos inválidos")
    })
    public ResponseEntity<Producto> addProducto(@RequestBody Producto producto) {
        Producto saved = repo.save(producto);
        URI location = URI.create("/producto/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    @GetMapping("producto/{id}")
    @Operation(summary = "Buscar producto por ID", description = "Busca un producto usando su identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto")
    })
    public ResponseEntity<Producto> getById(@PathVariable String id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("producto/{id}")
    @Operation(summary = "Reemplazar producto", description = "Reemplaza los datos de un producto existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto")
    })
    public ResponseEntity<Producto> putProduct(@PathVariable String id, @RequestBody Producto product) {
        if (repo.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        product.setId(id);
        return ResponseEntity.ok(repo.save(product));
    }

    @DeleteMapping("producto/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto eliminado"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto")
    })
    public ResponseEntity<Void> deleteProducto(@PathVariable String id) {
        if (repo.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
