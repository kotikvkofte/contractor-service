package org.ex9.contractorservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * Конфигурация RabbitMQ.
 *
 * Создаёт exchange, настраивает {@link RabbitTemplate}.
 * Все сообщения публикуются в exchange {@code contractors_contractor_exchange}
 * с routing key {@code contractor.updated}.
 * @author Крковцев Артём
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "contractors_contractor_exchange";
    public static final String ROUTING_KEY = "contractor.updated";

    /**
     * Создаёт exchange для сообщений о контрагентах.
     */
    @Bean
    public TopicExchange contractorsExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    /**
     * Конвертер для сообщений в JSON.
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * RabbitTemplate для отправки сообщений.
     * Устанавливает exchange, routing key и message converter.
     * Добавляет заголовок messageId.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setExchange(EXCHANGE);
        template.setRoutingKey(ROUTING_KEY);
        template.setMessageConverter(converter);

        //messageId
        template.setBeforePublishPostProcessors(message -> {
            if (message.getMessageProperties().getMessageId() == null) {
                message.getMessageProperties().setMessageId(UUID.randomUUID().toString());
            }
            return message;
        });

        return template;
    }

}
