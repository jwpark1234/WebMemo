<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.memo.BoardDto"%>
<%@page import="com.memo.BoardDao"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<html>
	<head>
		<!-- 공통으로 적용되는 부분 : 반응형 웹을 위한 설정, css설정 -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<link rel="stylesheet" href="../css/bootstrap.css">
		<link rel="stylesheet" href="../css/commons.css">
		<title>웹메모</title>
		
		<script type="text/javascript">
			function fnlist() {
				document.list.submit();
			}
		</script>
	</head>

	<body>
		<div class="wrap">
			<!-- header 부분 -->
			<jsp:include page="../inc/top.jsp">
				<jsp:param value="qna" name="active"/>
			</jsp:include>
			<!-- header 부분 -->
		<%
			request.setCharacterEncoding("UTF-8");
			String no = request.getParameter("num");
			String keyField = request.getParameter("keyField");
			String keyWord = request.getParameter("keyWord");
			// 조회 유무 판단 : true = 조회수 올림, false = 조회수 그대로
			boolean count = Boolean.valueOf(request.getParameter("count"));
			// 목록버튼 눌렀을 경우 돌아갈 페이지 번호
			String nowPage = request.getParameter("page");
			
			// 글 상세보기 DB작업을 위한 DAO객체 생성
			BoardDao dao = new BoardDao();
			BoardDto dto = dao.getBoard(Integer.parseInt(no), count); // 조회수 증가 유무도 함께 전달
			
			SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		%>
			<div id="qnaPanel" class="container">
				<br>
				<!-- 버튼 영역 -->
				<div class="row">
					<div class="col-sm-12">
						<div class="btn-group" role="group">
							<a class="btn btn-default" href="#" onclick="fnlist(); return false;">목 록</a>  
							<a class="btn btn-danger" href="Reply.jsp?no=<%=no%>&keyField=<%=keyField %>&keyWord=<%=keyWord %>">답 글</a> 
							<a class="btn btn-danger" href="Update.jsp?no=<%=no%>">수 정</a> 
							<a class="btn btn-danger" href="Delete.jsp?no=<%=no%>">삭 제</a>
						</div>
					</div>
				</div>
				<br>
				<!-- 게시글 영역 -->
				<div class="row">
					<div class="text-center col-sm-12">
						<div id="boardPanel" >
							<br>
							<table class="table" width="100%">				
							    <tr> 
									<td id="read_title"><%=dto.getSubject() %></td>
									<td align=right> 조회수 : <%=dto.getCount() %></td>
								</tr>
								<tr> 
									<td> 작성자 : <b><%=dto.getName() %></b><%=" ( " + dto.getEmail() + " )"%></td>
									<td align=right><%=s.format(dto.getRegdate()) %></td>
								</tr>
								<tr> 
			   													<!-- 줄바꿈을 \n에서 <br>태그로 바꿔준다.  -->
			    					<td colspan=2><br><%=dto.getContent().replace("\n", "<br>") %></td>
								</tr>
							</table>
						</div>
					</div>
				</div>
			</div>
			<!-- 목록으로 돌아갈때 원래 페이지로 키워드값을 가지고 돌아감 -->
			<form action="List.jsp?page=<%=nowPage %>" name="list" method="post">
				<input type="hidden" name="keyField" value="<%=keyField %>">
				<input type="hidden" name="keyWord" value="<%=keyWord %>">
			</form>
			<br>
			<!-- footer 부분 -->
			<jsp:include page="../inc/bottom.jsp"/>
			<!-- footer 부분 -->
		</div>
	</body>
</html>
