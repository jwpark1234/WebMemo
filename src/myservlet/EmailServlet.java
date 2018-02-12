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
		String authNum = RandomNum(); // 랜덤값 4자리수를 인증번호로 받아온다.

		String result = sendEmail(email, authNum); // 이메일 주소와 인증번호를 전달하고 결과값을 얻어온다.
		response.getWriter().write(result); // response객체에 전달
	}
	
	//난수 발생
	public String RandomNum() {
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i < 4; i++) { //4자리 수 랜던값
			int n = (int) (Math.random() * 10); 
			buffer.append(n);
		}
		return buffer.toString();
	}
	
	//메일보내기
	public String sendEmail(String email, String authNum) {
		String host = "smtp.naver.com"; // smtp 서버
		String subject = "회원가입을 위한 인증번호입니다."; // 메일 제목
		String fromName = "웹메모";
		String from = "cdu@naver.com";	// 보내는 메일주소
		String to1 = email; // 받는 메일주소

		String content = "인증번호는 [" + authNum + "] 입니다. 입력창에 정확하게 입력해주세요.";

		System.out.println(email + ", " + authNum);
		
		try {
			Properties props = new Properties();
			//gmail smtp 사용 설정
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.host", host);
			props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.port", "465");
			props.put("mail.smtp.user", from);
			props.put("mail.smtp.auth", "true");

			// 보내는 메일의 계정정보로 유효한 계정인지 판단한 후 메일세션 객체를 얻어옴.
			Session mailSession = Session.getInstance(props, new javax.mail.Authenticator() {
				protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("cdu","arsenal12");
				}
			});

			// 메세지 객체를 얻어옴
			Message msg = new MimeMessage(mailSession);
										//보내는 메일주소                                      보내는 사람
			msg.setFrom(new InternetAddress(from, MimeUtility.encodeText(fromName,"UTF-8","B"))); 

			// 받는 이메일 주소
			InternetAddress[] address1 = { new InternetAddress(to1) };
			
			// 메세지 객체에 메일정보를 세팅함.
			msg.setRecipients(Message.RecipientType.TO, address1); // 받는사람 설정
			msg.setSubject(subject); // 제목 설정
			msg.setSentDate(new java.util.Date()); // 보내는 날짜 설정
			msg.setContent(content, "text/html;charset=euc-kr"); // 내용설정 (HTML 형식)

			Transport.send(msg); // 메일 보내기
			
			return authNum; // 결과값은 인증번호!!
			
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "fail";
	}



}
