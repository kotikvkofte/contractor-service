package org.ex9.contractorservice.service.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.ex9.contractorservice.dto.rabbit.ContractorDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Сервис для отправки сообщений в RabbitMQ.
 *
 * <p>Выполняет десериализацию строки в {@link ContractorDto}
 * и отправляет RabbitMQ используя {@link RabbitTemplate}.</p>
 * @author Крковцев Артём
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class ProducerRabbitService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Формирует и отправляет данные о новом/обновленном контрагенте в RabbitMQ.
     *
     * @param stringDto строка с данными о контрагенте
     */
    public void sendContractor(String stringDto) {
        try {
            ContractorDto dto = objectMapper.readValue(stringDto, ContractorDto.class);
            rabbitTemplate.convertAndSend(dto);

            log.info("Sending contractor: {} to Rabbit", dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
