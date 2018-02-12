<%@page import="com.memo.BoardDto"%>
<%@page import="java.util.Vector"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<html>
	<head>
		<!-- 공통으로 적용되는 부분 : 반응형 웹을 위한 설정, css설정 -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<link rel="stylesheet" href="../css/bootstrap.css">
		<link rel="stylesheet" href="../css/commons.css">
		<title>웹메모</title>		
	
		<script>
			// 비밀번호 입력유무 체크
			function check() {
				if (document.form.pass.value == "") {
					alert("패스워드를 입력하세요.");
					form.pass.focus();
					return false;
				}
				document.form.submit();
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
			int no = Integer.parseInt(request.getParameter("no"));
		%>
			<jsp:useBean id="dao" class="com.memo.BoardDao"/>
		<%
			String paramPass = request.getParameter("pass");
			String dbPass = "";
	
			if(paramPass != null) {
				// 게시글 비밀번호를 가져옴
				dbPass = dao.getBoard(no,false).getPw();	
						
				// 비밀번호 일치여부 확인
				if(paramPass.equals(dbPass)) {
					dao.deleteBoard(no);
					%>
					<script type="text/javascript">
						alert("글이 삭제되었습니다.");
						location.href="List.jsp";
					</script>
					<% 
				}
				else {
					%>
					<script type="text/javascript">
			 			alert("비밀번호가 다릅니다.");
			 			history.back();
					</script>
					<% 
				}
			}
		%>
			<div id="qnaPanel" class="container">
				<form name=form method=post action="Delete.jsp" >
					<input type="hidden" name="no" value="<%=no%>">
					<br>
					<!-- 버튼 부분 -->
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-group" role="group">
								<input type=button class="btn btn-default" value="뒤로" onClick="history.back()">
								<input type=reset class="btn btn-default" value="다시쓰기"> 
								<input type=button class="btn btn-danger" value="삭제완료" onClick="check()"> 
							</div>
						</div>
					</div>
					<br>
					<!-- 비밀번호 입력 부분 -->
					<div class="row">
						<div class="text-center col-sm-12">
							<div id="boardPanel" >
							<br>
								<table width=100%>
									<tr>
										<td width=25%></td>
										<td align=center width=50%>
											<table class="table">
												<tr>
													<td align=center>비밀번호를 입력해주세요.</td>
												</tr>
												<tr> 
													<td align=center>  
														<div class="controls">
															<input type=password name="pass" size=17 maxlength=15 class="form-control">
														</div>
													</td> 
												</tr>
											</table>
										</td>
										<td></td>
									</tr>
								</table>		
							</div>
						</div>
					</div>
				</form> 
			</div>
			<br>
			<!-- footer 부분 -->
			<jsp:include page="../inc/bottom.jsp"/>
			<!-- footer 부분 -->
		</div>
	</body>
</html>
