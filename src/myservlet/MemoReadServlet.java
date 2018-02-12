package myservlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.memo.MemoDao;
import com.memo.MemoDto;

import net.sf.json.JSONObject;

public class MemoReadServlet extends HttpServlet {
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
		
		int memoNum = Integer.parseInt(req.getParameter("memoNum"));
		
		MemoDto dto = new MemoDao().getMemo(memoNum);
		
		// 읽어온 dto객체를 JSON객체에 저장하여 response객체에 전달하면 jquery에서 객체로 쉽게 받을 수 있다.
		JSONObject obj = new JSONObject();
		obj.put("userId",dto.getUserId());
		obj.put("subject",dto.getSubject());
		obj.put("content",dto.getContent());
		obj.put("regedate",dto.getRegdate());
		obj.put("favoriteYN",dto.getFavoriteYN());
		obj.put("color", dto.getColor());
		
		resp.setContentType("application/x-json;charset=UTF-8");
		resp.getWriter().print(obj.toString());

	}
}
