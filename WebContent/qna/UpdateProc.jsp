<%@page import="com.memo.BoardDao"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%
	request.setCharacterEncoding("UTF-8");

	// 1. Update.jsp페이지
	// (글을 수정하기 위해 글내용을 한번더 뿌려주고 뿌려준 내용에서 글을 수정한 후 
	// 수정한 글 내용을 request영역에 저장하여 현재 UpdateProc.jsp로 request영역을 전달시킨 페이지)
	// 에서 전달한 request영역의 데이터 꺼내오기
	int no = Integer.parseInt(request.getParameter("no"));
	
	// 2. request영역에서 꺼내온 수정한!! 글 내용을 저장할 !! BoardDto객체 생성
	// 3. 생성한 BoardDto 객체에 request영역에서 꺼내온 수정할 데이터들 저장
	// 4. 수정할 글내용을 저장하고 있는 BoardDto객체를 DB와 연결하여 Update하기 위한 BoardDao객체 생성
	// 5. 수정할 글번호를 통해 DB에 있는 request영역에서 꺼내온 수정할! 	
%>
	<jsp:useBean id="dto" class="com.memo.BoardDto"/>
	<jsp:setProperty property="*" name="dto"/>
	<jsp:useBean id="dao" class="com.memo.BoardDao"/>

<%
	//6. Update.jsp에서 작성한 패스워드값을 String paramPass변수에 저장
	String new_pass = request.getParameter("new_pass");
	// 7. 만약에 Update.jsp에서 작성한  패스퉈드와 DB에서 select한 패스워드가 다를때
	//8. "입력하신 비밀번호가 올바르지 않습니다."<--- 경고 메세지 창띄우기 
	//    이전 페이지(Update.jsp)로 이동!

	//9 else구문 ~~Update.jsp에서 작성한 패스워드와  DB에서 select한 패스워드가 같을때~~ 
	
	BoardDao passDao = new BoardDao();
	String pass = passDao.getBoard(no, false).getPw();
		
	if(new_pass.equals(pass)) {
		//10.  DB작업할 updateBoard()메소드 호출하여 글 수정하기!
		dto.setNum(no);
		dao.updateBoard(dto);
		
		//11. 페이지 흐름변화를 List.jsp로 이동 되도록 처리 ~ 
		%>
			<script type="text/javascript">
				alert("글이 수정되었습니다.");
				location.href="List.jsp";
			</script>
		<%
	}
	else { 
		%>
		<script type="text/javascript">
			alert("비밀번호가 틀렸습니다.");
			history.back();
		</script>
	<%
	}
%>
