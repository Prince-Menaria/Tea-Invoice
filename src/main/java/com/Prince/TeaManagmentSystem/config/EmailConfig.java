package com.Prince.TeaManagmentSystem.config;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailConfig {

	@Autowired
	JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	public String username;

	public String sendEmail(String to, String subject) throws IOException {

		try {

			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			// file attachment configuration
			FileSystemResource resource = new FileSystemResource(new File(
					"/home/indianic/Music/SpringExcelProject/TeaManagmentSystem/src/main/resources/data.pdf"));

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setFrom(username);
			helper.setText("hello Prince , your payment invoice here");
			helper.addAttachment(resource.getFilename(), resource, "application/pdf");

			javaMailSender.send(message);

			return "Email send successfull !!!";

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "error while sending email";

	}
}
