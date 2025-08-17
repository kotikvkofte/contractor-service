package org.ex9.contractorservice.service.outbox;

import org.ex9.contractorservice.model.OutboxEvent;
import org.ex9.contractorservice.repository.OutboxEventRepository;
import org.ex9.contractorservice.service.rabbit.ProducerRabbitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherTest {

    @Mock
    private ProducerRabbitService producerRabbitService;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @InjectMocks
    private OutboxPublisher outboxPublisher;

    @Test
    void publish_shouldSendEventsAndMarkAsPublished() {
        OutboxEvent event = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .type("ContractorUpdate")
                .payload("{\"id\":\"tst\",\"name\":\"Test contractor\",\"inn\":\"546546532165\"}")
                .isPublish(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(outboxEventRepository.findTop100ByIsPublishFalseOrderByCreatedAtAsc())
                .thenReturn(List.of(event));

        outboxPublisher.publish();

        assertTrue(event.getIsPublish());
        assertNotNull(event.getPublishedAt());
        verify(producerRabbitService, times(1)).sendContractor(event.getPayload());
        verify(outboxEventRepository, times(1)).save(event);
    }

}