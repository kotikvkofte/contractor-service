package org.ex9.contractorservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ex9.contractorservice.enums.EventType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность outbox-таблицы для реализации паттерна Inbox-Outbox.
 * Хранит события, которые должны быть опубликованы в очередь RabbitMQ.
 * @author Крковцев Артём
 */
@Table(name = "outbox_event")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    /** Уникальный идентификатор события */
    @Id
    private UUID id;

    /** Тип события */
    @Column(value = "type")
    private EventType type;

    /** Данные события в формате JSON */
    @Column(value = "payload")
    private String payload;

    /** Флаг, было ли событие опубликовано событие*/
    @Column(value = "is_publish")
    private Boolean isPublish;

    /** Время создания события (для очистки данных, например через месяц)*/
    @Column(value = "created_at")
    private LocalDateTime createdAt;

    /** Время публикации события */
    @Column(value = "published_at")
    private LocalDateTime publishedAt;

}
