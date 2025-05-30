package common.test;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2020. 11. 6. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class TestMail {

	@Test
	public void test() {
		final String username = "daekwang1026@gmail.com";
		final String password = "your_password"; // 구글 계정 비밀번호

        Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("daekwang1026@gmail.com")		// 받는사람
            );
            message.setSubject("메일 테스트");
            message.setText("메일 테스트입니다.");

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        assertTrue(true);
	}

}
