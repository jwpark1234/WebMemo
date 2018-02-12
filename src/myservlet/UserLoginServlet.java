package myservlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.memo.UserDao;

public class UserLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=UTF-8");
		
		String userId = req.getParameter("userId");
		String pw = req.getParameter("pw");
		
		// 아이디와 비번을 받아와서 로그인메소드에 전달하고 결과값을 response객체에 쓴다.
		resp.getWriter().write(new UserDao().login(userId, pw) + "");
	}
}
