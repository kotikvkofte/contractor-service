package org.ex9.contractorservice.repository;

import org.ex9.contractorservice.model.OutboxEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий для таблицы outbox_event.
 * @author Крковцев Артём
 */
@Repository
public interface OutboxEventRepository extends CrudRepository<OutboxEvent, UUID> {
}
