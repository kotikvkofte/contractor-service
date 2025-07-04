package org.ex9.contractorservice.repository;

import org.ex9.contractorservice.model.Industry;
import org.ex9.contractorservice.model.OrgForm;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrgFormRepository extends CrudRepository<OrgForm, Integer> {

    List<OrgForm> findAllByIsActiveTrue();

    @Query("UPDATE org_form SET is_active=false WHERE id=:id")
    @Modifying
    void deleteById(@Param("id") int id);
}
