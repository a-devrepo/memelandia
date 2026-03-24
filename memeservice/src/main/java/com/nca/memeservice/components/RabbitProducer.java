package com.nca.memeservice.components;

import com.nca.memeservice.configurations.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class RabbitProducer {

    private final RabbitTemplate rabbitTemplate;

    public RabbitProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void solicitarValidacaoUsuario(UUID usuarioId, UUID memeId) {
        Map<String, String> payload = Map.of(
                "usuarioId", usuarioId.toString(),
                "memeId", memeId.toString()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EX_MEME_USER_CHECK,
                RabbitMQConfig.RK_USER_CHECK,
                payload
        );
    }
}
