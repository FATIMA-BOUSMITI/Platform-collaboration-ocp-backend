package com.ocp.auth_service.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CreateUserRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void createUserRequestRequiresEmailProfileIdAndRoleId() {
        CreateUserRequest request = new CreateUserRequest();

        Set<String> messages = validator.validate(request).stream()
            .map(violation -> violation.getMessage())
            .collect(Collectors.toSet());

        assertThat(messages)
            .contains(
                "L'email est obligatoire ",
                "L'identifiant de l'utilisateur est obligatoire",
                "L'identifiant du rôle est obligatoire");
    }

    @Test
    void createUserRequestRejectsMalformedEmail() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("not-an-email");
        request.setUserId(UUID.randomUUID());
        request.setRoleId(UUID.randomUUID());

        assertThat(validator.validate(request))
            .extracting("message")
            .contains("Format d'email invalide");
    }
}
