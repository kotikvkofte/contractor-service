package org.ex9.contractorservice.repository;

import org.ex9.contractorservice.model.Industry;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IndustryRepository extends CrudRepository<Industry, Integer> {

    List<Industry> findAllByIsActiveTrue();

    @Query("UPDATE industry SET is_active=false WHERE id=:id")
    @Modifying
    void deleteById(@Param("id") int id);

}
