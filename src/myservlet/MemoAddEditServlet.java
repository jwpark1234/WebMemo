package myservlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.memo.MemoDao;
import com.memo.MemoDto;

public class MemoAddEditServlet extends HttpServlet {
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
		
		MemoDao dao = new MemoDao();
		MemoDto dto = new MemoDto();

		dto.setSubject(req.getParameter("subject"));
		dto.setContent(req.getParameter("content"));
		dto.setFavoriteYN("N");
		dto.setColor(req.getParameter("color"));
		
		// Ÿ�Կ� ���� ���/���� ����
		String type = req.getParameter("type");
		
		if(type.equals("write")) { // �� �޸� �ۼ�
			dto.setUserId(req.getParameter("userId"));
			int memoNum = dao.insertMemo(dto); // ���
			
			resp.getWriter().write(memoNum + "");
		}
		else if(type.equals("edit")) { // �޸� ����
			int memoNum = Integer.parseInt(req.getParameter("memoNum"));

			dto.setMemoNum(memoNum);
			String userId = dao.getMemo(memoNum).getUserId();
			dto.setUserId(userId);
			
			dao.updateMemo(dto); // ����!!
		}
		else if(type.equals("delete")) { // �޸� ����
			int memoNum = Integer.parseInt(req.getParameter("memoNum"));

			// 메모index를 받아와서 삭제한다.
			dao.deleteMemo(memoNum);
		}
	}
}
