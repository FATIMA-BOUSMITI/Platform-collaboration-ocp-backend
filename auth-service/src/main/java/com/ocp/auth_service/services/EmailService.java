package com.ocp.auth_service.services;

import com.ocp.auth_service.entity.UserCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender mailSender;

	@Value("${app.frontend.url:http://localhost:5173}")
	private String frontendUrl;

	@Value("${spring.mail.username:}")
	private String senderAddress;

	public void sendResetPasswordEmail(String email, String token) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		if (!senderAddress.isBlank()) {
			message.setFrom(senderAddress);
		}
		message.setSubject("Réinitialisation de votre mot de passe");
		String resetLink = UriComponentsBuilder.fromUriString(frontendUrl)
			.pathSegment("reset-password")
			.queryParam("token", "{token}")
			.encode()
			.buildAndExpand(token)
			.toUriString();
		message.setText("""
			Bonjour,

			Une demande de réinitialisation du mot de passe a été effectuée pour votre compte.
			Cliquez sur le lien suivant pour choisir un nouveau mot de passe (valable 30 minutes) :

			%s

			Si vous n'êtes pas à l'origine de cette demande, ignorez cet e-mail.
			""".formatted(resetLink));

		mailSender.send(message);
	}
	public void sendWelcomeEmail(UserCredential user) {
		SimpleMailMessage message = new SimpleMailMessage();


		message.setTo(user.getEmail());
		message.setSubject("Bienvenue sur notre plateforme");

		message.setText("""
            Bonjour,

            Votre compte a été créé avec succès.

            Vous pouvez maintenant vous connecter à la plateforme.

            Cordialement,
            L'équipe OCP
            """);

		mailSender.send(message);
	}

	public void sendVerificationEmail(UserCredential user, String verificationToken) {
		String verificationLink =
			"http://localhost:3000/verify-account?token=" + verificationToken;

		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(user.getEmail());
		message.setSubject("Vérification de votre compte");

		message.setText("""
            Bonjour,

            Merci d'avoir créé un compte.

            Veuillez cliquer sur le lien suivant pour activer votre compte :

            %s

            Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.

            Cordialement,
            L'équipe OCP
            """.formatted(verificationLink));

		mailSender.send(message);

	}

}
