package org.ex9.contractorservice.repository;

import org.ex9.contractorservice.model.Country;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends CrudRepository<Country, String> {

    List<Country> findAllByIsActiveTrue();

    @Query("INSERT INTO country(id, name, is_active) VALUES (:#{#country.id}, :#{#country.name}, :#{#country.isActive})")
    @Modifying
    void insert(@Param("country") Country country);

    @Query("UPDATE country SET is_active=false WHERE id=:id")
    @Modifying
    void deleteById(@Param("id") String id);

}
