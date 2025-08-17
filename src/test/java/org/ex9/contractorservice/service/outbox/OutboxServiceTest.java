package org.ex9.contractorservice.service.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ex9.contractorservice.dto.rabbit.ContractorDto;
import org.ex9.contractorservice.model.Contractor;
import org.ex9.contractorservice.repository.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxServiceTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OutboxService outboxService;

    @Test
    void saveEvent_shouldSerializeAndInsertEvent() throws Exception {
        Contractor contractor = Contractor.builder()
                .id("tst")
                .name("test contractor")
                .inn("3215644653218")
                .build();

        String json = "{\"id\":\"tst\",\"name\":\"test contractor\",\"inn\":\"3215644653218\"}";

        when(objectMapper.writeValueAsString(any(ContractorDto.class))).thenReturn(json);

        outboxService.saveEvent(contractor);

        verify(outboxEventRepository, times(1)).insert(argThat(event ->
                event.getType().equals("ContractorUpdate") &&
                        event.getPayload().equals(json) &&
                        !event.getIsPublish()
        ));
    }

}