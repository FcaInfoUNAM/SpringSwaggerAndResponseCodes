package com.example.demo.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Producto;
import com.example.demo.repo.productoRepo;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;





@RestController
public class ProductoController {
@Autowired
productoRepo repo;
@GetMapping("productos")
@Operation(summary = "Listar los productos")
@ApiResponse(responseCode = "200", description = "Lista de productos")
  public ResponseEntity<List<Producto>> findAllProductos() {
    return ResponseEntity.ok(this.repo.findAll());
  }

@PostMapping("producto")
@Operation(summary = "Crear un producto")
@ApiResponse(responseCode = "201", description = "Producto creado")
  public ResponseEntity<Producto> addProducto(@RequestBody Producto producto) {
    Producto saved = repo.save(producto);
    return ResponseEntity.created(URI.create("/producto/" + saved.getId())).body(saved);
  }

@GetMapping("producto/{id}")
@Operation(summary = "Buscar un producto por ID")
@ApiResponse(responseCode = "200", description = "Producto encontrado")
@ApiResponse(responseCode = "404", description = "No se encontró el producto")
public ResponseEntity<Producto> getById(@PathVariable String id) {
    Optional<Producto> result = repo.findById(id);
    if (result.isEmpty()) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(result.get());
}


@PutMapping("producto/{id}")
@Operation(summary = "Actualizar un producto")
@ApiResponse(responseCode = "200", description = "Producto actualizado")
@ApiResponse(responseCode = "404", description = "No se encontró el producto")
public ResponseEntity<Producto> putProduct(@PathVariable String id, @RequestBody Producto product) {
    if (!repo.existsById(id)) {
        return ResponseEntity.notFound().build();
    }
    product.setId(id);
    return ResponseEntity.ok(repo.save(product));
}

@DeleteMapping("producto/{id}")
@Operation(summary = "Eliminar un producto")
@ApiResponse(responseCode = "204", description = "Producto eliminado")
@ApiResponse(responseCode = "404", description = "No se encontró el producto")
public ResponseEntity<Void> deleteMailbox(@PathVariable("id") String id) {
    if (!repo.existsById(id)) {
        return ResponseEntity.notFound().build();
    }
    repo.deleteById(id);
    return ResponseEntity.noContent().build();
}

}

