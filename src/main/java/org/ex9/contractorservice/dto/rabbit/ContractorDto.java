package org.ex9.contractorservice.dto.rabbit;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для передачи данных о контрагенте между сервисами через RabbitMQ.
 * @author Крковцев Артём
 */
@Data
@Builder
public class ContractorDto {

    /** Уникальный идентификатор контрагента */
    private String id;

    /** Имя контрагента */
    private String name;

    /** ИНН контрагента */
    private String inn;

    /** Дата и время последнего изменения данных */
    private LocalDateTime modifyDateTime;

}
