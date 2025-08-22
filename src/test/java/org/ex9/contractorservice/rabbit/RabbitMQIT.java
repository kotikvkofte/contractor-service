package org.ex9.contractorservice.rabbit;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class RabbitMQIT {

    @Container
    static RabbitMQContainer rabbitMQ = new RabbitMQContainer("rabbitmq:3.13.7-management");

    private static final String QUEUE_NAME = "test_queue";

    @Test
    void test_sendAndReceive() throws Exception {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(
                rabbitMQ.getHost(), rabbitMQ.getAmqpPort());
        connectionFactory.setUsername(rabbitMQ.getAdminUsername());
        connectionFactory.setPassword(rabbitMQ.getAdminPassword());

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setDefaultReceiveQueue(QUEUE_NAME);

        rabbitTemplate.execute(channel -> {
            channel.queueDeclare(QUEUE_NAME, false, false, true, null);
            return null;
        });

        BlockingQueue<String> receivedMessages = new ArrayBlockingQueue<>(1);

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames(QUEUE_NAME);
        container.setMessageListener(message -> receivedMessages.add(new String(message.getBody())));
        container.start();

        String msg = "Test message";
        rabbitTemplate.convertAndSend(QUEUE_NAME, msg);

        String received = receivedMessages.poll(5, TimeUnit.SECONDS);

        assertEquals(msg, received);

        container.stop();
        connectionFactory.destroy();
    }
}
