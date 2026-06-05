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
			System.out.println("C: " + content);
			String content_sended = """
					<!DOCTYPE html>
					<html lang="zh-Hant">
					<body style="margin:0;padding:0;background:#f4f6f8;font-family:Arial,sans-serif;">

					<table width="100%%" cellpadding="0" cellspacing="0" border="0">
					<tr>
					<td align="center" style="padding:30px 15px;">

					<table width="520" cellpadding="0" cellspacing="0" border="0"
					style="
					background:#ffffff;
					border-radius:16px;
					overflow:hidden;
					box-shadow:0 2px 8px rgba(0,0,0,0.08);
					">

					<!-- Header -->
					<tr>
					<td
					style="
					background:#4f46e5;
					padding:25px;
					text-align:center;
					">

					<h1 style="
					margin:0;
					color:white;
					font-size:24px;
					">
					🏠 家庭生活管家
					</h1>

					</td>
					</tr>

					<!-- Content -->
					<tr>
					<td style="padding:35px;">

					<h2 style="
					margin-top:0;
					margin-bottom:20px;
					color:#111827;
					">
					%s
					</h2>

					<p style="
					color:#6b7280;
					line-height:1.8;
					margin-bottom:25px;
					">
					您好，這是一封系統通知信件。
					</p>

					<div
					style="
					background:#eef2ff;
					border:1px solid #c7d2fe;
					border-radius:12px;
					padding:20px;
					text-align:center;
					">

					<div style="
					font-size:14px;
					color:#6b7280;
					margin-bottom:10px;
					">
					通知內容
					</div>

					<div style="
					font-size:30px;
					font-weight:bold;
					letter-spacing:3px;
					color:#4f46e5;
					word-break:break-word;
					">
					%s
					</div>

					</div>

					<p style="
					margin-top:25px;
					font-size:13px;
					color:#9ca3af;
					">
					此郵件由系統自動發送，請勿直接回覆。
					</p>

					</td>
					</tr>

					<!-- Footer -->
					<tr>
					<td
					style="
					background:#f9fafb;
					padding:20px;
					text-align:center;
					font-size:12px;
					color:#9ca3af;
					">

					© 2026 Family Life

					</td>
					</tr>

					</table>

					</td>
					</tr>
					</table>

					</body>
					</html>
					""".formatted(subject, content);

			helper.setFrom("familyLifeTest123456@gmail.com");
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content_sended, true); // true = HTML

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
			helper.setTo(to);
			helper.setSubject("Email 驗證");

			String content = """
					<!DOCTYPE html>
					<html>
					<body style="margin:0;padding:0;background:#f4f6f8;font-family:Arial,sans-serif;">

					<table width="100%%" cellpadding="0" cellspacing="0">
					<tr>
					<td align="center">

					<table width="480" cellpadding="0" cellspacing="0"
					style="
					background:#ffffff;
					margin-top:40px;
					border-radius:12px;
					padding:24px;
					">

					<tr>
					<td style="font-size:24px;font-weight:bold;color:#111;">
					🏠 家庭生活管家
					</td>
					</tr>

					<tr>
					<td style="padding-top:15px;color:#555;">
					您好，這是一封 Email 驗證信。
					</td>
					</tr>

					<tr>
					<td align="center" style="padding:30px 0;">

					<div style="
					font-size:36px;
					font-weight:bold;
					letter-spacing:8px;
					color:#4f46e5;
					background:#eef2ff;
					padding:20px;
					border-radius:10px;
					">
					%s
					</div>

					</td>
					</tr>

					<tr>
					<td style="color:#666;">
					此驗證碼將於 <b>5 分鐘</b> 後失效。
					</td>
					</tr>

					<tr>
					<td style="padding-top:30px;font-size:12px;color:#999;">
					© 2026 Family Life
					</td>
					</tr>

					</table>

					</td>
					</tr>
					</table>

					</body>
					</html>
					""".formatted(code);

			helper.setText(code, true);

			mailSender.send(message);

		} catch (Exception e) {
			throw new RuntimeException("Email sending failed", e);
		}
	}
}
