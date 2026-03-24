package com.nca.memeservice.services;

import com.nca.memeservice.components.RabbitProducer;
import com.nca.memeservice.dtos.CategoriaResponseDTO;
import com.nca.memeservice.dtos.MemeCreateRequestDTO;
import com.nca.memeservice.dtos.MemeResponseDTO;
import com.nca.memeservice.dtos.MemeUpdateRequestDTO;
import com.nca.memeservice.entities.Categoria;
import com.nca.memeservice.entities.Meme;
import com.nca.memeservice.enums.StatusMeme;
import com.nca.memeservice.repositories.MemeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MemeService {

    private final MemeRepository repository;
    private final CategoriaService categoriaService;
    private final RabbitProducer memeProducer;

    public MemeService(MemeRepository repository, CategoriaService categoriaService, RabbitProducer memeProducer) {
        this.repository = repository;
        this.categoriaService = categoriaService;
        this.memeProducer = memeProducer;
    }

    @Transactional
    public MemeResponseDTO cadastrar(MemeCreateRequestDTO dto) {

        Categoria categoria = categoriaService.buscarEntidade(dto.categoriaId());

        Meme meme = new Meme();
        meme.setNome(dto.nome());
        meme.setDescricao(dto.descricao());
        meme.setUrlArquivo(dto.urlArquivo());
        meme.setUsuarioId(dto.usuarioId());
        meme.setCategoria(categoria);
        meme.setStatus(StatusMeme.PENDENTE);

        meme = repository.save(meme);

        memeProducer.solicitarValidacaoUsuario(meme.getUsuarioId(), meme.getId());

        return mapToResponse(meme);
    }

    @Transactional
    public MemeResponseDTO atualizar(UUID id, MemeUpdateRequestDTO dto) {

        Meme meme = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meme não encontrado"));

        if (!meme.getCategoria().getId().equals(dto.categoriaId())) {
            meme.setCategoria(categoriaService.buscarEntidade(dto.categoriaId()));
        }

        meme.setNome(dto.nome());
        meme.setDescricao(dto.descricao());
        meme.setUrlArquivo(dto.urlArquivo());

        return mapToResponse(repository.save(meme));
    }

    private MemeResponseDTO mapToResponse(Meme meme) {

        CategoriaResponseDTO categoriaDTO = new CategoriaResponseDTO(
                meme.getCategoria().getId(),
                meme.getCategoria().getNome(),
                meme.getCategoria().getDescricao(),
                meme.getCategoria().getDataCadastro()
        );

        return new MemeResponseDTO(
                meme.getId(),
                meme.getNome(),
                meme.getDescricao(),
                meme.getUrlArquivo(),
                meme.getDataCadastro(),
                meme.getUsuarioId(),
                categoriaDTO
        );
    }
}