package org.ex9.contractorservice.service.outbox;

import lombok.RequiredArgsConstructor;
import org.ex9.contractorservice.model.OutboxEvent;
import org.ex9.contractorservice.repository.OutboxEventRepository;
import org.ex9.contractorservice.service.rabbit.ProducerRabbitService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для публикации событий из outbox в RabbitMQ.
 * Периодически выбирает неотправленные события и
 * публикует их в RabbitMQ, помечая как отправленные.
 * @author Крковцев Артём
 */
@Service
@RequiredArgsConstructor
public class OutboxPublisher {

    private final ProducerRabbitService producerRabbitService;
    private final OutboxEventRepository outboxEventRepository;

    /**
     * Публикация неотправленных событий в RabbitMQ.
     */
    @Scheduled(fixedRate = 5000)
    @Transactional
    public void publish() {
        List<OutboxEvent> outboxEvents = outboxEventRepository.findTop100ByIsPublishFalseOrderByCreatedAtAsc();

        outboxEvents.forEach(outboxEvent -> {
            producerRabbitService.sendContractor(outboxEvent.getPayload());
            outboxEvent.setIsPublish(true);
            outboxEvent.setPublishedAt(LocalDateTime.now());
            outboxEventRepository.save(outboxEvent);
        });
    }

}
