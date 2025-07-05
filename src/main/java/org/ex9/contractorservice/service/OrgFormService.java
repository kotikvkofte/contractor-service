package org.ex9.contractorservice.service;

import org.ex9.contractorservice.dto.orgform.OrgFormRequestDto;
import org.ex9.contractorservice.dto.orgform.OrgFormResponseDto;
import org.ex9.contractorservice.exception.OrgFormNotFoundException;
import org.ex9.contractorservice.mapper.OrgFormMapper;
import org.ex9.contractorservice.model.OrgForm;
import org.ex9.contractorservice.repository.OrgFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrgFormService {

    private final OrgFormRepository repository;

    @Autowired
    public OrgFormService(OrgFormRepository repository) {

        this.repository = repository;

    }

    public List<OrgFormResponseDto> findAll() {

        var orgForms = repository.findAllByIsActiveTrue();

        return orgForms.stream().map(OrgFormMapper::toDto).toList();
    }

    public OrgFormResponseDto findById(int id) {
        var orgForm = repository.findById(id).orElseThrow(() -> new OrgFormNotFoundException("OrgForm with id " + id + " not found"));
        return OrgFormMapper.toDto(orgForm);
    }

    public OrgFormResponseDto save(OrgFormRequestDto request) {
        OrgForm orgForm = OrgFormMapper.toOrgForm(request);

        if (orgForm.getId() != null && !repository.existsById(orgForm.getId())) {
            throw new OrgFormNotFoundException("OrgForm with id " + orgForm.getId() + " not found");
        }
        var newOrUpdatedOrgForm = repository.save(orgForm);

        return OrgFormMapper.toDto(newOrUpdatedOrgForm);
    }

    public void delete(int id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new OrgFormNotFoundException("OrgForm with id " + id + " not found");
        }
    }

}
