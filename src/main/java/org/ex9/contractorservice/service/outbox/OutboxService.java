package org.ex9.contractorservice.service.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.ex9.contractorservice.dto.rabbit.ContractorDto;
import org.ex9.contractorservice.enums.EventType;
import org.ex9.contractorservice.mapper.ContractorMapper;
import org.ex9.contractorservice.model.Contractor;
import org.ex9.contractorservice.model.OutboxEvent;
import org.ex9.contractorservice.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Сервис для сохранения событий в базу данных.
 *
 * <p>При изменении контрагента формирует DTO, сериализует его в JSON
 * и сохраняет как новое событие в таблицу outbox_event.</p>
 * @author Крковцев Артём
 */
@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    /**
     * Сохраняет событие изменения контрагента.
     *
     * @param contractor сущность контрагента
     */
    @Transactional
    public OutboxEvent saveEvent(Contractor contractor) {
        ContractorDto contractorDto = ContractorMapper.toRabbitDto(contractor);

        OutboxEvent outboxEvent;
        try {
           String json = objectMapper.writeValueAsString(contractorDto);
            outboxEvent = OutboxEvent.builder()
                    .type(EventType.CONTRACTOR_UPDATE)
                    .payload(json)
                    .isPublish(false)
                    .createdAt(LocalDateTime.now())
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return outboxEventRepository.save(outboxEvent);
    }

    /**
     * Помечает событие как опубликованное.
     * @param outboxEvent событие которое нужно пометить
     */
    @Transactional
    public void markAsPublished(OutboxEvent outboxEvent) {
        outboxEvent.setIsPublish(true);
        outboxEvent.setPublishedAt(LocalDateTime.now());
        outboxEventRepository.save(outboxEvent);
    }

}
