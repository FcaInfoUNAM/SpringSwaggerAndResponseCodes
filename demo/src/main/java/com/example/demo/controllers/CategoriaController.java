package com.example.demo.controllers;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Department;
import com.example.demo.model.Producto;
import com.example.demo.repo.categoriaRepo;
import com.example.demo.repo.productoRepo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Categorías", description = "Operaciones para consultar y administrar categorías")
public class CategoriaController {

    @Autowired
    categoriaRepo repo;

    @Autowired
    productoRepo repoProducto;

    @GetMapping("categorias")
    @Operation(summary = "Listar categorías", description = "Devuelve todas las categorías registradas.")
    @ApiResponse(responseCode = "200", description = "Lista de categorías devuelta correctamente")
    public ResponseEntity<List<Department>> findAllCategoria() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping("categoria")
    @Operation(summary = "Crear categoría", description = "Guarda una categoría nueva.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Categoría creada"),
        @ApiResponse(responseCode = "400", description = "La solicitud contiene datos inválidos")
    })
    public ResponseEntity<Department> addCategoria(@RequestBody Department categoria) {
        Department saved = repo.save(categoria);
        URI location = URI.create("/categoria/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    @PostMapping("addProductoCategoria/{productId}/{categoriaId}")
    @Operation(
            summary = "Asociar producto con categoría",
            description = "Agrega un producto existente a una categoría existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto asociado con la categoría"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto o la categoría")
    })
    public ResponseEntity<Department> addProductoCategoria(
            @PathVariable String productId,
            @PathVariable String categoriaId) {
        Producto producto = repoProducto.findById(productId).orElse(null);
        Department categoria = repo.findById(categoriaId).orElse(null);
        if (producto == null || categoria == null) {
            return ResponseEntity.notFound().build();
        }
        categoria.getProductos().add(producto);
        return ResponseEntity.ok(repo.save(categoria));
    }

    @GetMapping("categoria/producto/{productId}")
    @Operation(
            summary = "Listar categorías de un producto",
            description = "Devuelve las categorías asociadas al producto indicado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categorías devueltas correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto")
    })
    public ResponseEntity<Set<Department>> getDepartmentsByProduct(@PathVariable String productId) {
        return repoProducto.findById(productId)
                .map(producto -> ResponseEntity.ok(producto.getDeparments()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("categoria/{id}")
    @Operation(summary = "Buscar categoría por ID", description = "Busca una categoría usando su identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
        @ApiResponse(responseCode = "404", description = "No se encontró la categoría")
    })
    public ResponseEntity<Department> getById(@PathVariable String id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("categoria/{id}")
    @Operation(summary = "Reemplazar categoría", description = "Reemplaza los datos de una categoría existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría actualizada"),
        @ApiResponse(responseCode = "404", description = "No se encontró la categoría")
    })
    public ResponseEntity<Department> putDepartment(
            @PathVariable String id,
            @RequestBody Department categoria) {
        if (repo.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        categoria.setId(id);
        return ResponseEntity.ok(repo.save(categoria));
    }

    @DeleteMapping("categoria/{id}")
    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Categoría eliminada"),
        @ApiResponse(responseCode = "404", description = "No se encontró la categoría")
    })
    public ResponseEntity<Void> deleteCategoria(@PathVariable String id) {
        if (repo.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
