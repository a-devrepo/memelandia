package com.nca.userservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateRequest(
        @NotBlank(message = "O nome não pode estar em branco para atualização.")
        @Size(min = 3, max = 255, message = "O nome deve ter entre 3 e 255 caracteres.")
        String nome
) {}