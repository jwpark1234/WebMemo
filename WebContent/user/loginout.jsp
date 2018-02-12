<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	request.setCharacterEncoding("UTF-8");

	// 이미 로그인한 상태라면 세션값 초기화
	if(session.getAttribute("userId") != null)
		session.invalidate();
	else { // 비로그인 상태라면 세션값 세팅
		String userId = request.getParameter("userId");
		session.setAttribute("userId", userId);
	}
	response.sendRedirect("../main/index.jsp");
%>