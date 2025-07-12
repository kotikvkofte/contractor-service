package org.ex9.contractorservice.repository;

import org.ex9.contractorservice.model.Industry;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link Industry} в базе данных.
 * Предоставляет методы для выполнения CRUD-операций и дополнительных запросов
 * для управления справочником производств. Все операции учитывают признак активности
 * записи ({@code is_active = true}).
 * @author Краковцев Артём
 */
public interface IndustryRepository extends CrudRepository<Industry, Integer> {

    /**
     * Находит все активные производства (где {@code is_active = true}).
     *
     * @return список активных производств
     */
    List<Industry> findAllByIsActiveTrue();

    /**
     * Находит активное производство по его идентификатору где {@code is_active = true}).
     * @param id уникальный идентификатор производства
     * @return активное производство
     */
    @Query("SELECT * FROM industry WHERE is_active=true AND id=:id ")
    Optional<Industry> findById(String id);

    /**
     * Выполняет логическое удаление производства по её идентификатору, устанавливая
     * {@code is_active = false}.
     *
     * @param id уникальный идентификатор производства
     */
    @Query("UPDATE industry SET is_active=false WHERE id=:id")
    @Modifying
    void deleteById(@Param("id") int id);

}
