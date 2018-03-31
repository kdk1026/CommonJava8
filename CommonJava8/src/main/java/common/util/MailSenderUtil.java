package common.util;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.util.properties.PropertiesUtil;

public class MailSenderUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(MailSenderUtil.class);
	
	private MailSenderUtil() {
		super();
	}
	
	private static final String MAIL_PROP_FILE = "mail.properties";
	
	/**
	 * mail.host = smtp.gmail.com
	 * mail.port = 465
	 * mail.from = webmaster@test.co.kr
	 * mail.username = 메일 계정 아이디
	 * mail.password = 메일 계정 비밀번호
	 */
	private static Properties getMailProperties() {
		return PropertiesUtil.getPropertiesClasspath(MAIL_PROP_FILE);
	}
	
	private static Properties getProperties() {
		final Properties mailProps = getMailProperties();
		Properties props = new Properties();
		props.put("mail.smtp.host", mailProps.getProperty("mail.host"));

		props.put("mail.smtp.port", mailProps.getProperty("mail.port"));
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");

		// Google Gmail 필수
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.enable", "true");

		return props;
	}
	
	/**
	 * 첩부 파일 메일 발송
	 * @param mailTos
	 * @param mailSubject
	 * @param mailMsg
	 * @param attachFile
	 */
	public static boolean sendmail(String[] mailTos, String mailSubject, String mailMsg, File attachFile) {
		boolean isSuccess = false;
		
		final Properties mailProps = getMailProperties();
		Properties props = getProperties();
		
		Authenticator auth = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(
						mailProps.getProperty("mail.username"), 
						mailProps.getProperty("mail.password")
				);
			}
		};
		
		Session session = Session.getDefaultInstance(props, auth);
		MimeMessage message = new MimeMessage(session);
		
		try {
			message.setFrom(new InternetAddress(mailProps.getProperty("mail.from")));
			
			for (String s : mailTos) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(s));				
			}
			
			message.setSubject(mailSubject);
			
			if (attachFile != null) {
				MimeBodyPart bodypart = new MimeBodyPart();
				bodypart.setContent(mailMsg, "text/html; charset=UTF-8");
				
				// 첨부파일
				DataSource source = new FileDataSource(attachFile);
				MimeBodyPart attachPart = new MimeBodyPart();
				attachPart.setDataHandler(new DataHandler(source));
				attachPart.setFileName(attachFile.getName());
				
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(bodypart);
				multipart.addBodyPart(attachPart);
				
				message.setContent(multipart);
			} else {
				message.setContent(mailMsg, "text/html; charset=UTF-8");
			}
			
			Transport.send(message);
			isSuccess = true;
			
		} catch (MessagingException e) {
			logger.error("", e);
		}
		
		return isSuccess;
	}
	
	/**
	 * 일반 메일 발송
	 * @param mailTos
	 * @param mailSubject
	 * @param mailMsg
	 */
	public static boolean sendmail(String[] mailTos, String mailSubject, String mailMsg) {
		return sendmail(mailTos, mailSubject, mailMsg, null);
	}
	
	/**
	 * 첩부 파일 메일 발송 - Commons Email 사용
	 * @param mailTos
	 * @param mailSubject
	 * @param mailMsg
	 * @param attachFile
	 */
	public static boolean sendmailCommons(String[] mailTos, String mailSubject, String mailMsg, File attachFile) {
		boolean isSuccess = false;
		
		final Properties mailProps = getMailProperties();
		Properties props = getProperties();
		
		HtmlEmail email = new HtmlEmail();
		email.setHostName(mailProps.getProperty("mail.host"));
		email.setSmtpPort(Integer.parseInt(mailProps.getProperty("mail.port")));
		email.setDebug(Boolean.parseBoolean(props.getProperty("mail.debug")));
		
		email.setAuthentication(mailProps.getProperty("mail.username"), mailProps.getProperty("mail.password"));
		
		email.setStartTLSEnabled(Boolean.parseBoolean(props.getProperty("mail.smtp.starttls.enable")));
		email.setSSLOnConnect(Boolean.parseBoolean(props.getProperty("mail.smtp.ssl.enable")));
		
		try {
			email.setFrom(mailProps.getProperty("mail.from"));
			email.addTo(mailTos);
			email.setSubject(mailSubject);
			email.setHtmlMsg(mailMsg);
			email.setCharset(Charset.forName("UTF-8").toString());
			
			if (attachFile != null) {
				// 첨부파일
				EmailAttachment attachment = new EmailAttachment();
			    attachment.setPath(attachFile.getAbsolutePath());
			    attachment.setDisposition(EmailAttachment.ATTACHMENT);
			    attachment.setName(attachFile.getName());

			    email.attach(attachment);
			}
			
			email.send();
			isSuccess = true;
			
		} catch (EmailException e) {
			logger.error("", e);
		}
		
		return isSuccess;
	}
	
	/**
	 * 일반 메일 발송 - Commons Email 사용
	 * @param mailTos
	 * @param mailSubject
	 * @param mailMsg
	 */
	public static boolean sendmailCommons(String[] mailTos, String mailSubject, String mailMsg) {
		return sendmailCommons(mailTos, mailSubject, mailMsg, null);
	}
	
}
