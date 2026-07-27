package com.ocp.auth_service.config;
import com.ocp.auth_service.Repository.UserCredentialRepository;
import com.ocp.auth_service.entity.UserCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor



public class DataInitializer {
	private final UserCredentialRepository userCredentialRepository;
	private final PasswordEncoder passwordEncoder;
	@Bean
	CommandLineRunner init() {

		return args -> {

			if (userCredentialRepository.findByEmail("fatimabousmiti9@gmail.com").isEmpty()) {

				UserCredential admin = UserCredential.builder()
					.userId(UUID.randomUUID())
					.email("fatimabousmiti9@gmail.com")
					.passwordHash(passwordEncoder.encode("Admin123"))
					.enabled(true)
					.accountLocked(false)
					.failedAttempts(0)
					.build();

				userCredentialRepository.save(admin);

				System.out.println("Admin account created successfully");
			}

		};
	}
}

