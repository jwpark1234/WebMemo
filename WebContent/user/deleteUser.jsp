<%@page import="com.memo.UserDao"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
	request.setCharacterEncoding("UTF-8");

	String userId = null;
	if(session.getAttribute("userId") != null) {
		
		// 아이디를 받아와서 회원 회원삭제 후 세션 초기화
		userId = (String) session.getAttribute("userId");
		new UserDao().deleteUser(userId);
		session.invalidate();
	}
	response.sendRedirect("../main/index.jsp");
%>