package myservlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.memo.UserDao;
import com.memo.UserDto;

public class UserAddEditServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=UTF-8");
		
		UserDto dto = new UserDto();
					
		String userId = req.getParameter("userId");
		String pw = req.getParameter("pw");
		String pw_confirm = req.getParameter("pw_confirm");
		String name = req.getParameter("name");
		String postcode = req.getParameter("postcode");
		String address1 = req.getParameter("address1");
		String address2 = req.getParameter("address2");
		String email = req.getParameter("email");
		String phone = req.getParameter("phone");
		
		if(	userId == null || userId.equals("") || pw == null || pw.equals("")
			|| pw_confirm == null || pw_confirm.equals("") || name == null || name.equals("")
			|| postcode == null || postcode.equals("") || address1 == null || address1.equals("")
			|| address2 == null || address2.equals("") || email == null || email.equals("")
			|| phone == null || phone.equals("") ) {
			resp.getWriter().write("0"); // 모든 항목을 입력하세용
			return;
		}
		
		/*******************아이디 유효성 검사******************/
		// 첫글자가 소문자가 아니거나 4~12자가 아니면 오류
		if( !( 'a' <= userId.charAt(0) && userId.charAt(0) <= 'z') || !( 4 <= userId.length() && userId.length() <= 12)) {
			resp.getWriter().write("-1");  // 유효한 아이디가 아닙니다.
			return;
		}
		else {
			// 소문자, 숫자가 아닌 문자가 있는 아이디면 오류
			for(int i = 1; i < userId.length(); i++) {
				if( !(('a' <= userId.charAt(i) && userId.charAt(i) <= 'z' ) || ('0' <= userId.charAt(i) && userId.charAt(i) <= '9' ))) {
					resp.getWriter().write("-1");  // 유효한 아이디가 아닙니다.
					return;
				}
			}
		}
	
		/****************비밀번호 유효성 검사*******************/
		// 비밀번호가 8~12자가 아니면 오류
		if(!(8 <= pw.length() && pw.length() <= 12)) {
			resp.getWriter().write("-3");  // 비밀번호는 8~12자로 입력해주세요.
			return;
		}
		
		/****************이름 유효성 검사**********************/
		// 이름이 2~10자가 아니면 오류.
		if( !(2 <= name.length() && name.length() <= 10) ) {
			resp.getWriter().write("-4");  // 이름은 2~10자로 입력해주세요.
			return;
		}
			
		/****************전화번호 유효성 검사**********************/
		// 전화번호가 13자가 아니면 오류
		if( !(phone.length() == 13) ) {
			resp.getWriter().write("-5");  // 전화번호는 000-0000-0000형식으로 입력해주세요.
			return;
		}else {
			for(int i = 0; i < phone.length(); i++) {
				if(i == 3 || i == 8) { // 4번째, 10번째 글자가 -가 아니면 오류
					if(phone.charAt(i) != '-') {
						resp.getWriter().write("-5");  // 전화번호는 000-0000-0000형식으로 입력해주세요.
						return;
					}
				}
				// 숫자가 아니면 오류
				else if(!('0' <= phone.charAt(i) && phone.charAt(i) <= '9' )) {
					resp.getWriter().write("-5");  // 전화번호는 000-0000-0000형식으로 입력해주세요.
					return;
				}
			}
		}
		
		// DTO 객체에 넣기
		dto.setUserId(userId);
		dto.setPw(pw);
		dto.setName(name);
		// 주소값은 %를 구분자로 합쳐서 저장한다. 회원정보를 읽을 때는 split()메서드로 파싱하여 출력해준다.
		dto.setAddress(postcode + "%" + address1 + "%" + address2);
		dto.setEmail(email);
		dto.setPhone(phone);
		
		// 회원가입인지 정보수정인지 판단하여 해당 작업 수행
		String type = req.getParameter("type");
		if(type.equals("add"))  // 회원가입
			resp.getWriter().write(new UserDao().insertUser(dto) + "");
		else if(type.equals("edit")) // 회원정보 수정
			resp.getWriter().write(new UserDao().updateUser(dto) + "");
	}
}
