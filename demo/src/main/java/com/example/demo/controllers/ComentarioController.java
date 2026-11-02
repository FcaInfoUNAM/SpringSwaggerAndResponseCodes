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

import com.example.demo.model.Comment;
import com.example.demo.repo.comentarioRepo;
import com.example.demo.repo.productoRepo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Comentarios", description = "Operaciones para consultar y administrar comentarios")
public class ComentarioController {

    @Autowired
    comentarioRepo repo;

    @Autowired
    productoRepo repoProducto;

    @GetMapping("comentarios")
    @Operation(summary = "Listar comentarios", description = "Devuelve todos los comentarios registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de comentarios devuelta correctamente")
    public ResponseEntity<List<Comment>> findAllComentarios() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping("comentario")
    @Operation(summary = "Crear comentario", description = "Guarda un comentario nuevo.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Comentario creado"),
        @ApiResponse(responseCode = "400", description = "La solicitud contiene datos inválidos")
    })
    public ResponseEntity<Comment> addComentario(@RequestBody Comment comentario) {
        Comment saved = repo.save(comentario);
        URI location = URI.create("/comentario/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    @PostMapping("comentario/{productId}")
    @Operation(
            summary = "Crear comentario para un producto",
            description = "Guarda un comentario asociado al producto indicado.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Comentario creado"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto")
    })
    public ResponseEntity<Comment> addComentario(
            @RequestBody Comment comentario,
            @PathVariable String productId) {
        return repoProducto.findById(productId)
                .map(producto -> {
                    comentario.setProducto(producto);
                    Comment saved = repo.save(comentario);
                    URI location = URI.create("/comentario/" + saved.getId());
                    return ResponseEntity.created(location).body(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("comentarios/{productId}")
    @Operation(
            summary = "Listar comentarios de un producto",
            description = "Devuelve los comentarios asociados al producto indicado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comentarios devueltos correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto")
    })
    public ResponseEntity<Set<Comment>> getCommentsByProduct(@PathVariable String productId) {
        return repoProducto.findById(productId)
                .map(producto -> ResponseEntity.ok(producto.getComentarios()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("comentario/{id}")
    @Operation(summary = "Buscar comentario por ID", description = "Busca un comentario usando su identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comentario encontrado"),
        @ApiResponse(responseCode = "404", description = "No se encontró el comentario")
    })
    public ResponseEntity<Comment> getById(@PathVariable String id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("comentario/{id}")
    @Operation(summary = "Reemplazar comentario", description = "Reemplaza los datos de un comentario existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comentario actualizado"),
        @ApiResponse(responseCode = "404", description = "No se encontró el comentario")
    })
    public ResponseEntity<Comment> putComentario(
            @PathVariable String id,
            @RequestBody Comment comentario) {
        if (repo.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        comentario.setId(id);
        return ResponseEntity.ok(repo.save(comentario));
    }

    @DeleteMapping("comentario/{id}")
    @Operation(summary = "Eliminar comentario", description = "Elimina un comentario existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Comentario eliminado"),
        @ApiResponse(responseCode = "404", description = "No se encontró el comentario")
    })
    public ResponseEntity<Void> deleteComentario(@PathVariable String id) {
        if (repo.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
