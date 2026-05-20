/*
 * @(#)EmailUtil.java
 */
package framework.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeUtility;
import org.eclipse.angus.mail.util.MailSSLSocketFactory;


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

	public static void sendMailAuthSSL(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String[] bccEmails, String fromEmail, String fromName) throws UnsupportedEncodingException, MessagingException {
		sendMailAuthSSL(smtpHost, smtpPort, smtpUser, smtpPassword, subject, content, toEmail, bccEmails, fromEmail, fromName, DEFAULT_CHARSET, null);
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

	public static void sendMailAuthSSL(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String[] bccEmails, String fromEmail, String fromName, String charset) throws UnsupportedEncodingException, MessagingException {
		sendMailAuthSSL(smtpHost, smtpPort, smtpUser, smtpPassword, subject, content, toEmail, bccEmails, fromEmail, fromName, charset, null);
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
	public static void sendMailAuthSSL(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String[] bccEmails, String fromEmail, String fromName, String charset, File[] attachFiles) throws UnsupportedEncodingException, MessagingException {
		// �������� �߰�. 
		sendMailAuthSSL( smtpHost,  smtpPort,  smtpUser,  smtpPassword,  subject,  content,  toEmail, bccEmails,  fromEmail,  fromName,  charset,  attachFiles, false) ;
	}

	public static void sendMailAuthSSL(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName, String charset, File[] attachFiles) throws UnsupportedEncodingException, MessagingException {
		sendMailAuthSSL( smtpHost,  smtpPort,  smtpUser,  smtpPassword,  subject,  content,  toEmail, null,  fromEmail,  fromName,  charset,  attachFiles, false) ;
	}

	public static void sendMailAuthSSL(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail,String[] bccEmails, String fromEmail, String fromName, String charset, File[] attachFiles, boolean debug) throws UnsupportedEncodingException, MessagingException {
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
		
		sendMail(subject, content, toEmail, fromEmail, fromName, charset, attachFiles, session, bccEmails);
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

	private static void sendMail ( String subject, String content, String toEmail, String fromEmail, String fromName, String charset, File[] attachFiles, Session session) throws UnsupportedEncodingException, MessagingException {
		sendMail(subject, content, toEmail, fromEmail, fromName, charset, attachFiles, session, null);
	}
	
	/**
	 * ���Ϲ߼� �� ÷������ ó��
	 */
	private static void sendMail(String subject, String content, String toEmail, String fromEmail, String fromName, String charset, File[] attachFiles, Session session ,String[] bccEmails) throws UnsupportedEncodingException, MessagingException {
		MimeMessage message = new MimeMessage(session);
		InternetAddress addr = new InternetAddress(fromEmail, fromName, charset);
		message.setFrom(addr);
		message.setSubject(subject);
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

		 // BCC(���� ����) �߰�
	    if (bccEmails != null) {
	        for (String bccEmail : bccEmails) {
	            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bccEmail));
	        }
	    }

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
		protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
			return new jakarta.mail.PasswordAuthentication(id, pw);
		}
	}
}