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
			resp.getWriter().write("0"); // ��� �׸��� �Է��ϼ���
			return;
		}
		
		/*******************���̵� ��ȿ�� �˻�******************/
		// ù���ڰ� �ҹ��ڰ� �ƴϰų� 4~12�ڰ� �ƴϸ� ����
		if( !( 'a' <= userId.charAt(0) && userId.charAt(0) <= 'z') || !( 4 <= userId.length() && userId.length() <= 12)) {
			resp.getWriter().write("-1");  // ��ȿ�� ���̵� �ƴմϴ�.
			return;
		}
		else {
			// �ҹ���, ���ڰ� �ƴ� ���ڰ� �ִ� ���̵�� ����
			for(int i = 1; i < userId.length(); i++) {
				if( !(('a' <= userId.charAt(i) && userId.charAt(i) <= 'z' ) || ('0' <= userId.charAt(i) && userId.charAt(i) <= '9' ))) {
					resp.getWriter().write("-1");  // ��ȿ�� ���̵� �ƴմϴ�.
					return;
				}
			}
		}
	
		/****************��й�ȣ ��ȿ�� �˻�*******************/
		// ��й�ȣ�� 8~12�ڰ� �ƴϸ� ����
		if(!(8 <= pw.length() && pw.length() <= 12)) {
			resp.getWriter().write("-3");  // ��й�ȣ�� 8~12�ڷ� �Է����ּ���.
			return;
		}
		
		/****************�̸� ��ȿ�� �˻�**********************/
		// �̸��� 2~10�ڰ� �ƴϸ� ����.
		if( !(2 <= name.length() && name.length() <= 10) ) {
			resp.getWriter().write("-4");  // �̸��� 2~10�ڷ� �Է����ּ���.
			return;
		}
			
		/****************��ȭ��ȣ ��ȿ�� �˻�**********************/
		// ��ȭ��ȣ�� 13�ڰ� �ƴϸ� ����
		if( !(phone.length() == 13) ) {
			resp.getWriter().write("-5");  // ��ȭ��ȣ�� 000-0000-0000�������� �Է����ּ���.
			return;
		}else {
			for(int i = 0; i < phone.length(); i++) {
				if(i == 3 || i == 8) { // 4��°, 10��° ���ڰ� -�� �ƴϸ� ����
					if(phone.charAt(i) != '-') {
						resp.getWriter().write("-5");  // ��ȭ��ȣ�� 000-0000-0000�������� �Է����ּ���.
						return;
					}
				}
				// ���ڰ� �ƴϸ� ����
				else if(!('0' <= phone.charAt(i) && phone.charAt(i) <= '9' )) {
					resp.getWriter().write("-5");  // ��ȭ��ȣ�� 000-0000-0000�������� �Է����ּ���.
					return;
				}
			}
		}
		
		// DTO ��ü�� �ֱ�
		dto.setUserId(userId);
		dto.setPw(pw);
		dto.setName(name);
		// �ּҰ��� %�� �����ڷ� ���ļ� �����Ѵ�. ȸ�������� ���� ���� split()�޼���� �Ľ��Ͽ� ������ش�.
		dto.setAddress(postcode + "%" + address1 + "%" + address2);
		dto.setEmail(email);
		dto.setPhone(phone);
		
		// ȸ���������� ������������ �Ǵ��Ͽ� �ش� �۾� ����
		String type = req.getParameter("type");
		if(type.equals("add"))  // ȸ������
			resp.getWriter().write(new UserDao().insertUser(dto) + "");
		else if(type.equals("edit")) // ȸ������ ����
			resp.getWriter().write(new UserDao().updateUser(dto) + "");
	}
}
