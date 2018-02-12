<%@page import="com.memo.*"%>
<%@page import="java.sql.Timestamp"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%
	request.setCharacterEncoding("UTF-8");
%>
<!-- 순서1 Reply.jsp(댓글작성 디자인 화면)페이지에서 요청받은 작성한 글내용을 request영역에서 꺼내오기 -->
<!-- 순서2 request영역에서 꺼내온 작성한 글내용을! DTO의 변수에 저장 -->
<%-- <jsp:useBean id="dto" class="com.bean.board.BoardDto"/> --%>
<%-- <jsp:setProperty property="*" name="dto"/> --%>

<%
	BoardDto dto = new BoardDto();
	dto.setName(request.getParameter("name"));
	if(session.getAttribute("userId") != null) {
		UserDao dao = new UserDao();
		String name = dao.getUser((String)session.getAttribute("userId")).getName();
		dto.setName(name);
	}
	dto.setEmail(request.getParameter("email"));
	dto.setSubject(request.getParameter("subject"));
	dto.setContent(request.getParameter("content"));
	dto.setPw(request.getParameter("pass"));
	
	int no = Integer.parseInt(request.getParameter("no"));
	String keyField = request.getParameter("keyField");
	String keyWord = request.getParameter("keyWord");

%>

<% 
	//dto에 글쓴 시간 따로 저장 => insert sql에 now()로 대신 쓸 수 있다.
	//dto.setRegdate(new Timestamp(System.currentTimeMillis()));
%>

<!-- 순서3 DB에 작성한 글내용을 insert하기 위해 DAO객체 생성 -->
<%-- <jsp:useBean id="dao" class="com.bean.board.BoardDao"/> --%>
<% 
	BoardDao dao = new BoardDao();

	BoardDto parentDto = dao.getBoard(no, false);
	
	// replyBoard() 메소드로!!
	// 게시판 DB에 우리가 작성한 글내용정보를 저장하고 있는 DTO객체를 전달!!!
	dto.setPos(parentDto.getPos());
	dto.setDepth(parentDto.getDepth());
	dto.setNum(no);
	dao.replyBoard(dto);
	
	response.sendRedirect("List.jsp");
%>