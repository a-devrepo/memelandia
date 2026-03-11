package com.nca.userservice.services;

import com.nca.userservice.dtos.UsuarioRequest;
import com.nca.userservice.dtos.UsuarioResponse;
import com.nca.userservice.dtos.UsuarioUpdateRequest;
import com.nca.userservice.entities.Usuario;
import com.nca.userservice.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest request) {
        log.info("Tentativa de cadastro de usuário: {}", request.email());

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());

        usuario = usuarioRepository.save(usuario);
        log.info("Usuário cadastrado com sucesso. ID: {}", usuario.getId());

        return toResponse(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> consultar() {
        log.info("Consultando todos os usuários");
        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public UsuarioResponse atualizar(UUID id, UsuarioUpdateRequest request) {
        log.info("Atualizando usuário ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado para atualização. ID: {}", id);
                    return new IllegalArgumentException("Usuário não encontrado.");
                });

        usuario.setNome(request.nome());
        usuario = usuarioRepository.save(usuario);

        return toResponse(usuario);
    }

    @Transactional
    public void excluir(UUID id) {
        log.info("Excluindo usuário ID: {}", id);
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado para exclusão.");
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getDataCadastro()
        );
    }
}