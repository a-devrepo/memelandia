package com.nca.memeservice.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EX_MEME_USER_CHECK = "ex.memelandia.user-check";
    public static final String QUEUE_USER_CHECK = "q.user-verification";
    public static final String RK_USER_CHECK = "rk.user-check";

    @Bean
    public TopicExchange memeUserExchange() {
        return new TopicExchange(EX_MEME_USER_CHECK);
    }

    @Bean
    public Queue userCheckQueue() {
        return QueueBuilder.durable(QUEUE_USER_CHECK).build();
    }

    @Bean
    public Binding bindingUserCheck(Queue userCheckQueue, TopicExchange memeUserExchange) {
        return BindingBuilder.bind(userCheckQueue).to(memeUserExchange).with(RK_USER_CHECK);
    }

    @Bean
    @SuppressWarnings("removal")
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {

        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");

        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        converter.setClassMapper(classMapper);

        return converter;
    }

    @Bean
    public Queue responseQueue() {
        return QueueBuilder.durable("q.meme-validation-response").build();
    }

    @Bean
    public ContainerCustomizer<SimpleMessageListenerContainer> containerCustomizer() {
        return container -> container.setObservationEnabled(true); // Habilita rastreio no Consumer
    }
}