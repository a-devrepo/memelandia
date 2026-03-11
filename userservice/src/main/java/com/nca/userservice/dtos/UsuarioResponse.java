package com.nca.userservice.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String nome,
        String email,
        LocalDateTime dataCadastro
) {}