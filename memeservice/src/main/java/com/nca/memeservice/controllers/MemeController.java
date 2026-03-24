package com.nca.memeservice.controllers;

import com.nca.memeservice.dtos.MemeCreateRequestDTO;
import com.nca.memeservice.dtos.MemeResponseDTO;
import com.nca.memeservice.dtos.MemeUpdateRequestDTO;
import com.nca.memeservice.services.MemeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/memes")
public class MemeController {

    private final MemeService service;

    public MemeController(MemeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MemeResponseDTO> cadastrar(@RequestBody @Valid MemeCreateRequestDTO dto) {
        MemeResponseDTO response = service.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemeResponseDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid MemeUpdateRequestDTO dto) {
        MemeResponseDTO response = service.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/memealeatorio")
    public ResponseEntity<MemeResponseDTO> obterAleatorio() {
        return ResponseEntity.ok(service.buscarAleatorio());
    }
}