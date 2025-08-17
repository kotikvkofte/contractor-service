package org.ex9.contractorservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Конфигурация планировщика задач.
 *
 * Включает поддержку аннотации {@link org.springframework.scheduling.annotation.Scheduled}
 * для периодического выполнения задач (например, публикации сообщений из outbox).
 * @author Крковцев Артём
 */
@Configuration
@EnableScheduling
public class OutboxPublisherSchedulerConfig {
}
