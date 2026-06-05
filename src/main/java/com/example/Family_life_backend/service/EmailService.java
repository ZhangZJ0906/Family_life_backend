package com.example.Family_life_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	public void sendMail(String to, String subject, String content) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom("familyLifeTest123456@gmail.com");
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content, true); // true = HTML

			mailSender.send(message);
		} catch (Exception e) {
			throw new RuntimeException("Email sending failed", e);
		}
	}

	public void sendVerificationCode(String to, String code) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom("familyLifeTest123456@gmail.com");
			helper.setTo("davids710119@gmail.com");
			helper.setSubject("Email 驗證");

			String content = """
					<h2>Email 驗證</h2>
					<p>您的驗證碼為：</p>
					<h1>%s</h1>
					<p>5分鐘內有效</p>
					""".formatted(code);

			helper.setText(content, true);

			mailSender.send(message);

		} catch (Exception e) {
			throw new RuntimeException("Email sending failed", e);
		}
	}
}
