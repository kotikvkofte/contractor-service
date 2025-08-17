package org.ex9.contractorservice.repository;

import org.ex9.contractorservice.model.OutboxEvent;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для таблицы outbox_event.
 * @author Крковцев Артём
 */
@Repository
public interface OutboxEventRepository extends CrudRepository<OutboxEvent, UUID> {

    /**
     * Возвращает 100 неопубликованных событий, отсортированных по дате создания.
     */
    List<OutboxEvent> findTop100ByIsPublishFalseOrderByCreatedAtAsc();

    /**
     * Добавляет новое событие в таблицу.
     */
    @Query("INSERT INTO outbox_event(id, type, payload, is_publish, created_at) VALUES (:#{#outboxEvent.id}, :#{#outboxEvent.type}, :#{#outboxEvent.payload}, :#{#outboxEvent.isPublish}, :#{#outboxEvent.createdAt})")
    @Modifying
    void insert(@Param("outboxEvent") OutboxEvent outboxEvent);

}
