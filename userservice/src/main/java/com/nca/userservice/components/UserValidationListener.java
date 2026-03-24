package com.nca.userservice.components;

import com.nca.userservice.repositories.UsuarioRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class UserValidationListener {

    private final UsuarioRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    public UserValidationListener(UsuarioRepository userRepository, RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "q.user-verification")
    public void processarValidacao(Map<String, String> mensagem) {
        UUID usuarioId = UUID.fromString(mensagem.get("usuarioId"));
        String memeId = mensagem.get("memeId");

        boolean existe = userRepository.existsById(usuarioId);

        Map<String, Object> resposta = Map.of(
                "memeId", memeId,
                "existe", existe
        );

        rabbitTemplate.convertAndSend("q.meme-validation-response", resposta);
    }
}