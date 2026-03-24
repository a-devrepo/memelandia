package com.nca.memeservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaCreateRequestDTO(
        @NotBlank(message = "O nome da categoria é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nome,

        @Size(max = 500, message = "A descrição não pode exceder 500 caracteres")
        String descricao
) {}