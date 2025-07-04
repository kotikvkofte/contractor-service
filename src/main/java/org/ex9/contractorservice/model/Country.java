package org.ex9.contractorservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@Table(name = "country")
public class Country {

    @Id
    private String id;

    @Column(value = "name")
    private String name;

    @Column(value = "is_active")
    @Builder.Default
    private Boolean isActive = true;

}
