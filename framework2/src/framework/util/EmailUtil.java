/*
 * @(#)EmailUtil.java
 */
package framework.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import com.sun.mail.util.MailSSLSocketFactory;


/**
 * JavaMail�� �̿��� ������ �߼��ϴ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class EmailUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private EmailUtil() {
	}

	/**
	 * �⺻ ���ڵ� ��
	 */
	private static final String DEFAULT_CHARSET = "euc-kr";

	//////////////////////////////////////////////////////////////////////////////////////////SMTP������ ������ �ʿ��� ���

	/**
	 * ������ SMTP ���� ������ ���Ͽ� ���ڸ����� �߼��Ѵ�.
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ���: EmailUtil.sendMailAuth("mail.xxx.co.kr", "25", "id", "password", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿");
	 *
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param smtpUser ������ SMTP ���� �������̵�
	 * @param smtpPassword ������ SMTP ���� ������й�ȣ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailAuth(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName) throws UnsupportedEncodingException, MessagingException {
		sendMailAuth(smtpHost, smtpPort, smtpUser, smtpPassword, subject, content, toEmail, fromEmail, fromName, DEFAULT_CHARSET, null);
	}

	/**
	 * ������ SMTP ���� ������ ���Ͽ� ���ڸ����� �߼��Ѵ�. (���ȿ���-SSL �� �ʿ��Ҷ�)
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ���: EmailUtil.sendMailAuthSSL("mail.xxx.co.kr", "465", "id", "password", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿");
	 *
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param smtpUser ������ SMTP ���� �������̵�
	 * @param smtpPassword ������ SMTP ���� ������й�ȣ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailAuthSSL(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName) throws UnsupportedEncodingException, MessagingException {
		sendMailAuthSSL(smtpHost, smtpPort, smtpUser, smtpPassword, subject, content, toEmail, fromEmail, fromName, DEFAULT_CHARSET, null);
	}

	/**
	 * ������ SMTP ���� ������ ���Ͽ� ���ڸ����� �߼��Ѵ�.
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ���: EmailUtil.sendMailAuth("mail.xxx.co.kr", "25", "id", "password", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿", "euc-kr");
	
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param smtpUser ������ SMTP ���� �������̵�
	 * @param smtpPassword ������ SMTP ���� ������й�ȣ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 * @param charset ���ڵ� ĳ���ͼ�
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailAuth(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName, String charset) throws UnsupportedEncodingException, MessagingException {
		sendMailAuth(smtpHost, smtpPort, smtpUser, smtpPassword, subject, content, toEmail, fromEmail, fromName, charset, null);
	}

	/**
	 * ������ SMTP ���� ������ ���Ͽ� ���ڸ����� �߼��Ѵ�. (���ȿ���-SSL �� �ʿ��Ҷ�)
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ���: EmailUtil.sendMailAuthSSL("mail.xxx.co.kr", "465", "id", "password", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿", "euc-kr");
	
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param smtpUser ������ SMTP ���� �������̵�
	 * @param smtpPassword ������ SMTP ���� ������й�ȣ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 * @param charset ���ڵ� ĳ���ͼ�
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailAuthSSL(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName, String charset) throws UnsupportedEncodingException, MessagingException {
		sendMailAuthSSL(smtpHost, smtpPort, smtpUser, smtpPassword, subject, content, toEmail, fromEmail, fromName, charset, null);
	}

	/**
	 * ������ SMTP ���� ������ ���Ͽ� ���ڸ����� �߼��Ѵ�.
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ���: EmailUtil.sendMailAuth("mail.xxx.co.kr", "25", "id", "password", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿", "euc-kr", new File[] { f1, f2 });
	
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param smtpUser ������ SMTP ���� �������̵�
	 * @param smtpPassword ������ SMTP ���� ������й�ȣ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 * @param charset ���ڵ� ĳ���ͼ�
	 * @param attachFiles ÷������ �迭
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailAuth(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName, String charset, File[] attachFiles) throws UnsupportedEncodingException, MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", smtpPort);
		props.put("mail.smtp.user", smtpUser);
		props.put("mail.smtp.auth", true);
		MyAuthenticator auth = new MyAuthenticator(smtpUser, smtpPassword);
		Session session = Session.getInstance(props, auth);
		sendMail(subject, content, toEmail, fromEmail, fromName, charset, attachFiles, session);
	}

	/**
	 * ������ SMTP ���� ������ ���Ͽ� ���ڸ����� �߼��Ѵ�. (���ȿ���-SSL �� �ʿ��Ҷ�)
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ���: EmailUtil.sendMailAuthSSL("mail.xxx.co.kr", "465", "id", "password", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿", "euc-kr", new File[] { f1, f2 });
	 *
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param smtpUser ������ SMTP ���� �������̵�
	 * @param smtpPassword ������ SMTP ���� ������й�ȣ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 * @param charset ���ڵ� ĳ���ͼ�
	 * @param attachFiles ÷������ �迭
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailAuthSSL(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName, String charset, File[] attachFiles) throws UnsupportedEncodingException, MessagingException {
		sendMailAuthSSL( smtpHost,  smtpPort,  smtpUser,  smtpPassword,  subject,  content,  toEmail,  fromEmail,  fromName,  charset,  attachFiles, false) ;
	}

	public static void sendMailAuthSSL(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName, String charset, File[] attachFiles, boolean debug) throws UnsupportedEncodingException, MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", smtpPort);
		props.put("mail.smtp.user", smtpUser);
		props.put("mail.smtp.auth", true);
		if(debug) props.put("mail.smtp.debug", true);
		
		props.put("mail.smtp.from", fromEmail);
		// tls ����(ssl �� ����� ��쿡�� �ּ�)
		props.put("mail.smtp.starttls.enable", true);
		// ssl ����(�̶��� ���� port 465)
		props.put("mail.smtp.ssl.enable", true);
		// ���̹� ������ ssl ����� ���� ���� ��� �߰�
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");
		//props.put("mail.smtp.ssl.checkserveridentity", false);
		//props.put("mail.smtp.ssl.trust", "smtp.worksmobile.com");

		props.put("mail.protocol.ssl.trust", "*");
		props.put("mail.smtp.socketFactory.port", smtpPort);
		//props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		try {
			MailSSLSocketFactory sf = new MailSSLSocketFactory();
			sf.setTrustAllHosts(true);
			props.put("mail.smtp.ssl.socketFactory", sf);
		} catch (GeneralSecurityException e) {
			throw new MessagingException("SSL ���� ����", e);
		}

		props.put("mail.smtp.socketFactory.fallback", false);
		
		MyAuthenticator auth = new MyAuthenticator(smtpUser, smtpPassword);
		Session session = Session.getInstance(props, auth);
		if(debug) session.setDebug(true);
		sendMail(subject, content, toEmail, fromEmail, fromName, charset, attachFiles, session);
	}

	//////////////////////////////////////////////////////////////////////////////////////////SMTP������ ������ �ʿ���� ���

	/**
	 * ������ SMTP ���� �������� ���ڸ����� �߼��Ѵ�.
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ���: EmailUtil.sendMailNoAuth("mail.xxx.co.kr", "25", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿");
	
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailNoAuth(String smtpHost, String smtpPort, String subject, String content, String toEmail, String fromEmail, String fromName) throws UnsupportedEncodingException, MessagingException {
		sendMailNoAuth(smtpHost, smtpPort, subject, content, toEmail, fromEmail, fromName, DEFAULT_CHARSET, null);
	}

	/**
	 * ������ SMTP ���� �������� ���ڸ����� �߼��Ѵ�. (���ȿ���-SSL �� �ʿ��Ҷ�)
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ���: EmailUtil.sendMailNoAuthSSL("mail.xxx.co.kr", "465", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿");
	
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailNoAuthSSL(String smtpHost, String smtpPort, String subject, String content, String toEmail, String fromEmail, String fromName) throws UnsupportedEncodingException, MessagingException {
		sendMailNoAuthSSL(smtpHost, smtpPort, subject, content, toEmail, fromEmail, fromName, DEFAULT_CHARSET, null);
	}

	/**
	 * ������ SMTP ���� �������� ���ڸ����� �߼��Ѵ�.
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ���: EmailUtil.sendMailNoAuth("mail.xxx.co.kr", "25", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿", "euc-kr");
	
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 * @param charset ���ڵ� ĳ���ͼ�
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailNoAuth(String smtpHost, String smtpPort, String subject, String content, String toEmail, String fromEmail, String fromName, String charset) throws UnsupportedEncodingException, MessagingException {
		sendMailNoAuth(smtpHost, smtpPort, subject, content, toEmail, fromEmail, fromName, charset, null);
	}

	/**
	 * ������ SMTP ���� �������� ���ڸ����� �߼��Ѵ�. (���ȿ���-SSL �� �ʿ��Ҷ�)
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ���: EmailUtil.sendMailNoAuthSSL("mail.xxx.co.kr", "465", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿", "euc-kr");
	
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 * @param charset ���ڵ� ĳ���ͼ�
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailNoAuthSSL(String smtpHost, String smtpPort, String subject, String content, String toEmail, String fromEmail, String fromName, String charset) throws UnsupportedEncodingException, MessagingException {
		sendMailNoAuthSSL(smtpHost, smtpPort, subject, content, toEmail, fromEmail, fromName, charset, null);
	}

	/**
	 * ������ SMTP ���� �������� ���ڸ����� �߼��Ѵ�.
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ���: EmailUtil.sendMailNoAuth("mail.xxx.co.kr", "25", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿", "euc-kr", new File[] { f1, f2 });
	
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 * @param charset ���ڵ� ĳ���ͼ�
	 * @param attachFiles ÷������ �迭
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailNoAuth(String smtpHost, String smtpPort, String subject, String content, String toEmail, String fromEmail, String fromName, String charset, File[] attachFiles) throws UnsupportedEncodingException, MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", smtpPort);

		Session session = Session.getInstance(props, null);
		sendMail(subject, content, toEmail, fromEmail, fromName, charset, attachFiles, session);
	}

	/**
	 * ������ SMTP ���� �������� ���ڸ����� �߼��Ѵ�. (���ȿ���-SSL �� �ʿ��Ҷ�)
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ���: EmailUtil.sendMailNoAuthSSL("mail.xxx.co.kr", "465", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿", "euc-kr", new File[] { f1, f2 });
	
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 * @param charset ���ڵ� ĳ���ͼ�
	 * @param attachFiles ÷������ �迭
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailNoAuthSSL(String smtpHost, String smtpPort, String subject, String content, String toEmail, String fromEmail, String fromName, String charset, File[] attachFiles) throws UnsupportedEncodingException, MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", smtpPort);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		Session session = Session.getInstance(props, null);
		sendMail(subject, content, toEmail, fromEmail, fromName, charset, attachFiles, session);
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private �޼ҵ� �� ��ü

	/**
	 * ���Ϲ߼� �� ÷������ ó��
	 */
	private static void sendMail(String subject, String content, String toEmail, String fromEmail, String fromName, String charset, File[] attachFiles, Session session) throws UnsupportedEncodingException, MessagingException {
		MimeMessage message = new MimeMessage(session);
		InternetAddress addr = new InternetAddress(fromEmail, fromName, charset);
		message.setFrom(addr);
		message.setSubject(subject);
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

		if (attachFiles == null) {
			message.setContent(content, "text/html; charset=" + charset);
		} else {
			Multipart multipart = new MimeMultipart();
			BodyPart messageBodyPart = new MimeBodyPart();

			//messageBodyPart.setText(content);
			messageBodyPart.setContent(content, "text/html; charset=" + charset);
			multipart.addBodyPart(messageBodyPart);

			for (File f : attachFiles) {
				BodyPart fileBodyPart = new MimeBodyPart();
				FileDataSource fds = new FileDataSource(f);
				fileBodyPart.setDataHandler(new DataHandler(fds));
				fileBodyPart.setFileName(MimeUtility.encodeText(f.getName(), charset, "B"));
				multipart.addBodyPart(fileBodyPart);
			}
			message.setContent(multipart);
		}
		Transport.send(message);
	}

	/**
	 * ���������� ���� ��ü
	 */
	private static class MyAuthenticator extends Authenticator {
		private String id;
		private String pw;

		public MyAuthenticator(String id, String pw) {
			this.id = id;
			this.pw = pw;
		}

		@Override
		protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
			return new javax.mail.PasswordAuthentication(id, pw);
		}
	}
}