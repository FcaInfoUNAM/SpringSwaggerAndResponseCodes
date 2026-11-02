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

import com.example.demo.model.Image;
import com.example.demo.repo.imagenRepo;
import com.example.demo.repo.productoRepo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Imágenes", description = "Operaciones para consultar y administrar imágenes")
public class ImagenController {

    @Autowired
    imagenRepo repo;

    @Autowired
    productoRepo repoProducto;

    @GetMapping("imagenes")
    @Operation(summary = "Listar imágenes", description = "Devuelve todas las imágenes registradas.")
    @ApiResponse(responseCode = "200", description = "Lista de imágenes devuelta correctamente")
    public ResponseEntity<List<Image>> findAllImagenes() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping("imagen")
    @Operation(summary = "Crear imagen", description = "Guarda una imagen nueva.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Imagen creada"),
        @ApiResponse(responseCode = "400", description = "La solicitud contiene datos inválidos")
    })
    public ResponseEntity<Image> addImagen(@RequestBody Image imagen) {
        Image saved = repo.save(imagen);
        URI location = URI.create("/imagen/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    @PostMapping("imagen/{productId}")
    @Operation(
            summary = "Crear imagen para un producto",
            description = "Guarda una imagen asociada al producto indicado.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Imagen creada"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto")
    })
    public ResponseEntity<Image> addImagen(
            @RequestBody Image imagen,
            @PathVariable String productId) {
        return repoProducto.findById(productId)
                .map(producto -> {
                    imagen.setProducto(producto);
                    Image saved = repo.save(imagen);
                    URI location = URI.create("/imagen/" + saved.getId());
                    return ResponseEntity.created(location).body(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("imagenes/producto/{productId}")
    @Operation(
            summary = "Listar imágenes de un producto",
            description = "Devuelve las imágenes asociadas al producto indicado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imágenes devueltas correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto")
    })
    public ResponseEntity<Set<Image>> getImagenesByProduct(@PathVariable String productId) {
        return repoProducto.findById(productId)
                .map(producto -> ResponseEntity.ok(producto.getImagenes()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("imagen/{id}")
    @Operation(summary = "Buscar imagen por ID", description = "Busca una imagen usando su identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagen encontrada"),
        @ApiResponse(responseCode = "404", description = "No se encontró la imagen")
    })
    public ResponseEntity<Image> getById(@PathVariable String id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("imagen/{id}")
    @Operation(summary = "Reemplazar imagen", description = "Reemplaza los datos de una imagen existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagen actualizada"),
        @ApiResponse(responseCode = "404", description = "No se encontró la imagen")
    })
    public ResponseEntity<Image> putImagen(@PathVariable String id, @RequestBody Image imagen) {
        if (repo.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        imagen.setId(id);
        return ResponseEntity.ok(repo.save(imagen));
    }

    @DeleteMapping("imagen/{id}")
    @Operation(summary = "Eliminar imagen", description = "Elimina una imagen existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Imagen eliminada"),
        @ApiResponse(responseCode = "404", description = "No se encontró la imagen")
    })
    public ResponseEntity<Void> deleteImagen(@PathVariable String id) {
        if (repo.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
