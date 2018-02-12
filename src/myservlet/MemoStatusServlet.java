package myservlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.memo.MemoDao;

public class MemoStatusServlet extends HttpServlet {
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
		
		String type = req.getParameter("type");
		int memoNum = Integer.parseInt(req.getParameter("memoNum"));
		String userId = req.getParameter("userId");
		
		String result = "";
		
		if(type.equals("favorite")) { // ���ã�� �߰�/����
			result = new MemoDao().favoriteMemo(memoNum);
		}
		else if(type.equals("sharedFav")) { // �������� �޸� ���ã�� �߰�/����
			result = new MemoDao().sharedFavMemo(memoNum, userId);
		}
		else if(type.equals("shareOn")) { // ������
			result = new MemoDao().shareMemo(memoNum, userId) + "";	
		}
		else if(type.equals("shareOff")) { // ��������
			result = new MemoDao().killShareMemo(memoNum, userId) + "";	
		}
		resp.getWriter().write(result);
	}
}
