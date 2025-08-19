package org.ex9.contractorservice.service.outbox;

import org.ex9.contractorservice.dto.rabbit.ContractorDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OutboxPublisher outboxPublisher;

    @Test
    void sendContractor_shouldConvertJsonAndSend() throws Exception {
        ContractorDto dto = ContractorDto.builder()
                .id("1")
                .name("Test")
                .inn("123")
                .build();

        doAnswer(invocationOnMock -> null).when(rabbitTemplate).invoke(any(RabbitTemplate.OperationsCallback.class));

        outboxPublisher.publish(dto);

        verify(rabbitTemplate, times(1)).invoke(any(RabbitTemplate.OperationsCallback.class));
    }

}