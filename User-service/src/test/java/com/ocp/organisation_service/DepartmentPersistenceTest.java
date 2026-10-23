package com.ocp.organisation_service;

import com.ocp.organisation_service.entity.Departement;
import com.ocp.organisation_service.enums.DepartmentStatus;
import com.ocp.organisation_service.mappers.DepartementMapper;
import com.ocp.organisation_service.repository.DepartementRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties={"spring.jpa.hibernate.ddl-auto=create-drop", "logging.level.root=WARN", "debug=false"})
class DepartmentPersistenceTest {
    @Autowired DepartementRepository departments;
    @Autowired TestEntityManager em;

    @Test
    void creationAndUpdatePopulateRequiredTimestamps() {
        Departement department = new Departement();
        department.setName("QA department");
        department.setCode("QA_DEPARTMENT");
        departments.saveAndFlush(department);
        var id = department.getId();
        em.clear();
        var created = departments.findById(id).orElseThrow();
        assertThat(created.getCreatedAt()).isNotNull();
        assertThat(created.getUpdatedAt()).isEqualTo(created.getCreatedAt());
        var createdAt = created.getCreatedAt();
        created.setName("QA department updated");
        departments.saveAndFlush(created);
        em.clear();
        var updated = departments.findById(id).orElseThrow();
        assertThat(updated.getCreatedAt()).isEqualTo(createdAt);
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(createdAt);
        departments.delete(updated);
        departments.flush();
        assertThat(departments.existsById(id)).isFalse();
    }

    @Test
    void responsePreservesFieldsNeededToEditAnExistingDepartment() {
        Departement entity = new Departement();
        entity.setId(UUID.randomUUID());
        entity.setName("QA department");
        entity.setCode("QA_EDIT");
        entity.setDescription("Department description");
        entity.setManagerId(UUID.randomUUID());
        entity.setStatus(DepartmentStatus.ACTIVE);
        var response = Mappers.getMapper(DepartementMapper.class).toResponse(entity);
        assertThat(response.getCode()).isEqualTo(entity.getCode());
        assertThat(response.getDescription()).isEqualTo(entity.getDescription());
        assertThat(response.getManagerId()).isEqualTo(entity.getManagerId());
        assertThat(response.getStatus()).isEqualTo(entity.getStatus());
    }
}