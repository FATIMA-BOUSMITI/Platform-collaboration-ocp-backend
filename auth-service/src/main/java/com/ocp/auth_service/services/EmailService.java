package com.ocp.auth_service.services;

import com.ocp.auth_service.entity.UserCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender mailSender;


	public void sendResetPasswordEmail(String email, String token) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(email);
		message.setSubject("Reset Password");
		message.setText("http://localhost:3000/reset-password?token=" + token);

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
