package com.nca.memeservice.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record MemeResponseDTO(
        UUID id,
        String nome,
        String descricao,
        String urlArquivo,
        LocalDateTime dataCadastro,
        UUID usuarioId,
        CategoriaResponseDTO categoria
) {}
