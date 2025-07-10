package org.ex9.contractorservice.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Data
@Builder(toBuilder=true)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@Getter
@Table(name = "contractor")
public class Contractor {

    @Id
    private String id;

    @Column(value = "parent_id")
    private Contractor parent;

    @Column(value = "name")
    private String name;

    @Column(value = "name_full")
    private String nameFull;

    @Column(value = "inn")
    private String inn;

    @Column(value = "ogrn")
    private String ogrn;

    @Column(value = "country")
    private Country country;

    @Column(value = "industry")
    private Industry industry;

    @Column(value = "org_form")
    private OrgForm orgForm;

    @Column(value = "create_date")
    private Date createDate;

    @Column(value = "modify_date")
    private Date modifyDate;

    @Column(value = "create_user_id")
    private String createUserId;

    @Column(value = "modify_user_id")
    private String modifyUserId;

    @Builder.Default
    @Column(value = "is_active")
    private Boolean isActive = true;

}
