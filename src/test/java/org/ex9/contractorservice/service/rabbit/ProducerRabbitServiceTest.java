package org.ex9.contractorservice.service.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ex9.contractorservice.dto.rabbit.ContractorDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProducerRabbitServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProducerRabbitService producerRabbitService;

    @Test
    void sendContractor_shouldConvertJsonAndSend() throws Exception {
        String json = "{\"id\":\"1\",\"name\":\"Test\",\"inn\":\"123\"}";
        ContractorDto dto = ContractorDto.builder()
                .id("1")
                .name("Test")
                .inn("123")
                .build();

        when(objectMapper.readValue(json, ContractorDto.class)).thenReturn(dto);

        producerRabbitService.sendContractor(json);

        verify(rabbitTemplate, times(1)).convertAndSend(dto);
    }

}