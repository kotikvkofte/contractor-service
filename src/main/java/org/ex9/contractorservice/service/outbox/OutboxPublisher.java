package org.ex9.contractorservice.service.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.ex9.contractorservice.dto.rabbit.ContractorDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для публикации событий из outbox в RabbitMQ.
 *
 * @author Крковцев Артём
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class OutboxPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.confirm-time:1000}")
    private Long confirmTime;

    /**
     * Отправляет данные о новом/обновленном контрагенте в RabbitMQ.
     *
     * @param dto объект с информацией о контрагенте
     */
    @Transactional
    public void publish(ContractorDto dto) {
        try {
            rabbitTemplate.invoke(channel -> {
                channel.convertAndSend(dto);
                channel.waitForConfirmsOrDie(confirmTime);
                return null;
            });

        } catch (Exception e) {
            log.error("Error while sending outbox message", e);
            throw new RuntimeException(e);
        }
    }

}
