package com.nca.userservice.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_USER_CHECK = "q.user-verification";
    public static final String QUEUE_MEME_RESPONSE = "q.meme-validation-response";

    @Bean
    public Queue responseQueue() {
        return new Queue(QUEUE_MEME_RESPONSE, true);
    }

    @Bean
    @SuppressWarnings("removal")
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public ContainerCustomizer<SimpleMessageListenerContainer> containerCustomizer() {
        return container -> container.setObservationEnabled(true); // Habilita rastreio no Consumer
    }
}