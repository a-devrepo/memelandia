package com.nca.memeservice.components;

import com.nca.memeservice.enums.StatusMeme;
import com.nca.memeservice.repositories.MemeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class MemeResponseListener {

    private final MemeRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(MemeResponseListener.class);

    public MemeResponseListener(MemeRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "q.meme-validation-response")
    public void atualizarStatusMeme(Map<String, Object> payload) {
        try {
            UUID memeId = UUID.fromString(payload.get("memeId").toString());
            boolean usuarioExiste = (boolean) payload.get("existe");

            repository.findById(memeId).ifPresentOrElse(meme -> {
                if (usuarioExiste) {
                    meme.setStatus(StatusMeme.VALIDADO);
                    logger.info("Meme {} validado com sucesso!", memeId);
                } else {
                    meme.setStatus(StatusMeme.INVALIDO);
                    logger.warn("Meme {} marcado como INVÁLIDO. Usuário não encontrado.", memeId);
                }
                repository.save(meme);
            }, () -> logger.error("Meme ID {} não encontrado no banco para atualização de status", memeId));

        } catch (Exception e) {
            logger.error("Erro ao processar resposta de validação: {}", e.getMessage());
        }
    }
}