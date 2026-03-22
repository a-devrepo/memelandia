package com.nca.memeservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

public record MemeUpdateRequestDTO(
        @NotBlank(message = "O nome não pode ser vazio")
        String nome,

        String descricao,

        @URL(message = "A URL deve ser válida")
        String urlArquivo,

        @NotNull(message = "A categoria deve ser informada")
        UUID categoriaId
) {}
