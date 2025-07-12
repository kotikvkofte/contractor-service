package org.ex9.contractorservice.repository;

import org.ex9.contractorservice.model.Country;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link Country} в базе данных.
 * Предоставляет методы для выполнения CRUD-операций и дополнительных запросов
 * для управления справочником стран. Все операции учитывают признак активности
 * записи ({@code is_active = true}).
 * @author Краковцев Артём
 */
@Repository
public interface CountryRepository extends CrudRepository<Country, String> {

    /**
     * Находит все активные страны (где {@code is_active = true}).
     *
     * @return список активных стран
     */
    List<Country> findAllByIsActiveTrue();

    /**
     * Находит активную страну по ее идентификатору где {@code is_active = true}).
      * @param id уникальный идентификатор страны
     * @return активную страну
     */
    @Query("SELECT * FROM country WHERE is_active=true AND id=:id ")
    Optional<Country> findById(String id);

    /**
     * Вставляет новую запись о стране в таблицу {@code country}.
     *
     * @param country сущность страны для вставки
     */
    @Query("INSERT INTO country(id, name, is_active) VALUES (:#{#country.id}, :#{#country.name}, :#{#country.isActive})")
    @Modifying
    void insert(@Param("country") Country country);

    /**
     * Выполняет логическое удаление страны по её идентификатору, устанавливая
     * {@code is_active = false}.
     *
     * @param id уникальный идентификатор страны
     */
    @Query("UPDATE country SET is_active=false WHERE id=:id")
    @Modifying
    void deleteById(@Param("id") String id);

}
