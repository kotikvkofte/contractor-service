package org.ex9.contractorservice.repository;

import org.ex9.contractorservice.model.OrgForm;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link OrgForm} в базе данных.
 * Предоставляет методы для выполнения CRUD-операций и дополнительных запросов
 * для управления справочником организационных форм. Все операции учитывают признак
 * активности записи ({@code is_active = true}).
 * @author Краковцев Артём
 */
public interface OrgFormRepository extends CrudRepository<OrgForm, Integer> {

    /**
     * Находит все активные организационные формы (где {@code is_active = true}).
     *
     * @return список активных организационных форм
     */
    List<OrgForm> findAllByIsActiveTrue();

    /**
     * Находит активную организационную форму по ее идентификатору где {@code is_active = true}).
     * @param id уникальный идентификатор организационной формы
     * @return активная организационная форма
     */
    @Query("SELECT * FROM org_form WHERE is_active=true AND id=:id ")
    Optional<OrgForm> findById(String id);

    /**
     * Выполняет логическое удаление организационной формы по её идентификатору,
     * устанавливая {@code is_active = false}.
     *
     * @param id уникальный идентификатор организационной формы
     */
    @Query("UPDATE org_form SET is_active=false WHERE id=:id")
    @Modifying
    void deleteById(@Param("id") int id);

}
