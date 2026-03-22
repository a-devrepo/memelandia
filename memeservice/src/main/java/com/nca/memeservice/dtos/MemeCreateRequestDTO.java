package com.nca.memeservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

public record MemeCreateRequestDTO(
        @NotBlank(message = "O nome do meme é obrigatório")
        String nome,

        String descricao,

        @NotBlank(message = "A URL do arquivo é obrigatória")
        @URL(message = "A URL deve ser válida")
        String urlArquivo,

        @NotNull(message = "O ID do usuário é obrigatório")
        UUID usuarioId,

        @NotNull(message = "O ID da categoria é obrigatório")
        UUID categoriaId
) {}
