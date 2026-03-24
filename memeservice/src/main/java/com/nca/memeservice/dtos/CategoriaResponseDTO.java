package com.nca.memeservice.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoriaResponseDTO(
        UUID id,
        String nome,
        String descricao,
        LocalDateTime dataCadastro
) {}