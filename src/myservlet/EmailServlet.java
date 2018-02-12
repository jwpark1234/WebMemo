package myservlet;


import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/EmailServlet")
public class EmailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String email = request.getParameter("email");
		String authNum = RandomNum(); // ������ 4�ڸ����� ������ȣ�� �޾ƿ´�.

		String result = sendEmail(email, authNum); // �̸��� �ּҿ� ������ȣ�� �����ϰ� ������� ���´�.
		response.getWriter().write(result); // response��ü�� ����
	}
	
	//���� �߻�
	public String RandomNum() {
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i < 4; i++) { //4�ڸ� �� ������
			int n = (int) (Math.random() * 10); 
			buffer.append(n);
		}
		return buffer.toString();
	}
	
	//���Ϻ�����
	public String sendEmail(String email, String authNum) {
		String host = "smtp.naver.com"; // smtp ����
		String subject = "ȸ�������� ���� ������ȣ�Դϴ�."; // ���� ����
		String fromName = "���޸�";
		String from = "cdu@naver.com";	// ������ �����ּ�
		String to1 = email; // �޴� �����ּ�

		String content = "������ȣ�� [" + authNum + "] �Դϴ�. �Է�â�� ��Ȯ�ϰ� �Է����ּ���.";

		System.out.println(email + ", " + authNum);
		
		try {
			Properties props = new Properties();
			//gmail smtp ��� ����
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.host", host);
			props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.port", "465");
			props.put("mail.smtp.user", from);
			props.put("mail.smtp.auth", "true");

			// ������ ������ ���������� ��ȿ�� �������� �Ǵ��� �� ���ϼ��� ��ü�� ����.
			Session mailSession = Session.getInstance(props, new javax.mail.Authenticator() {
				protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("cdu","arsenal12");
				}
			});

			// �޼��� ��ü�� ����
			Message msg = new MimeMessage(mailSession);
										//������ �����ּ�                                      ������ ���
			msg.setFrom(new InternetAddress(from, MimeUtility.encodeText(fromName,"UTF-8","B"))); 

			// �޴� �̸��� �ּ�
			InternetAddress[] address1 = { new InternetAddress(to1) };
			
			// �޼��� ��ü�� ���������� ������.
			msg.setRecipients(Message.RecipientType.TO, address1); // �޴»�� ����
			msg.setSubject(subject); // ���� ����
			msg.setSentDate(new java.util.Date()); // ������ ��¥ ����
			msg.setContent(content, "text/html;charset=euc-kr"); // ���뼳�� (HTML ����)

			Transport.send(msg); // ���� ������
			
			return authNum; // ������� ������ȣ!!
			
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "fail";
	}



}
