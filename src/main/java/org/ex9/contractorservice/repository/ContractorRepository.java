package org.ex9.contractorservice.repository;

import org.ex9.contractorservice.model.Contractor;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностью {@link Contractor} в базе данных.
 * Предоставляет методы для выполнения CRUD-операций.
 *
 * @author Краковцев Артём
 */
@Repository
public interface ContractorRepository extends CrudRepository<Contractor, String> {

	/**
	 * Выполняет логическое удаление контрагента по его идентификатору, устанавливая
	 * {@code is_active = false}.
	 *
	 * @param id уникальный идентификатор контрагента
	 */
	@Query("UPDATE contractor SET is_active=false WHERE id=:id")
	@Modifying
	void deleteById(@Param("id") String id);

}
