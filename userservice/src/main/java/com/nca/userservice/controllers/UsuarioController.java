package com.nca.userservice.controllers;

import com.nca.userservice.dtos.UsuarioRequest;
import com.nca.userservice.dtos.UsuarioResponse;
import com.nca.userservice.dtos.UsuarioUpdateRequest;
import com.nca.userservice.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> post(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.cadastrar(request));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> get() {
        return ResponseEntity.ok(usuarioService.consultar());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> put(@PathVariable UUID id, @Valid @RequestBody UsuarioUpdateRequest request) {
        return ResponseEntity.ok(usuarioService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        usuarioService.excluir(id);
    }
}