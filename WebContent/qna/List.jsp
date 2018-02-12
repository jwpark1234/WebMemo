<%@ page contentType="text/html;charset=UTF-8" %>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.memo.BoardDto"%>
<%@page import="java.util.Vector"%>
<%@page import="com.memo.BoardDao"%>

<HTML>
	<head>
		<!-- 공통으로 적용되는 부분 : 반응형 웹을 위한 설정, css설정 -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<link rel="stylesheet" href="../css/bootstrap.css">
		<link rel="stylesheet" href="../css/commons.css">
		<title>웹메모</title>
		
		<script type="text/javascript">
			
			// 검색어가 없는 경우를 체크하는 함수
			function check(){
				if(document.search.keyWord.value == ""){
					alert("검색어를 입력하세요.");
					document.search.keyWord.focus();
					return;
				}
				document.search.submit();
			}
			
			// 목록버튼을 눌렀을때 첫페이지 목록으로 돌아가는 함수
			function fnlist() {
				document.list.action = "List.jsp?page=1";
				document.list.submit();
			}
			
			//현재 글제목을 클릭한 글번호를 넘겨받아서 form태그에 있는 input태그의 value값으로 지정.
			//지정한 후 form의 action속성에 있는 페이지로 요청한다.
			function fnRead(num) {
				document.read.num.value = num;
				document.read.submit();
			}
			
			//새 게시글을 작성할때 호출되는 함수
			//검색필드와 검색어, 페이지값을 전달받아 게시글 작성페이지에서 목록으로 돌아올 수 있게 함.
			function fnWrite() {
				document.write.submit();
			}
		</script>
	</head>
	<BODY>
		<div class="wrap">
			<!-- header 부분 -->
			<jsp:include page="../inc/top.jsp">
				<jsp:param value="qna" name="active"/>
			</jsp:include>
			<!-- header 부분 -->
	    
				<%! String keyWord = "", keyField = ""; 
		
				//[1]------------------페이징을 위한 변수 선언-----------------------------
				int totalRecord = 0;	// 게시판에 저장된 전체 글의 갯수 [2]
				int numPerPage = 15;	// 한 페이지 당 보여질 글의 갯수
				int pagePerBlock = 3; 	// 한 블럭 당 묶여질 페이지 수
				/*
					pagePerBlock 변수 설명
					게시판 하단 부분에 보면                   이전 3개  ◀  1 2 3  ▶ 다음 3개
					◀  또는 ▶  을 클릭했을때 게시판에 글이 많을 경우 한 페이지씩 이동하는 것은 매우 불편하다.
					그럴때에는 여러 페이지를  하나로 묶어서 블럭단위로 이동하게 할 수 있다.
					여러 페이지를 하나로 묶어서 이동하는게 좀 더 빠르게 이동할 수 있따.
				*/
				int totalPage = 0; 		// 전체페이지 수 저장 [4]
				int totalBlock = 0;		// 전체 블럭 수 저장  [9]
				int nowPage = 0;		// 현재 선택한 페이지 번호 저장[7]
				int nowBlock = 0;		// 현재 선택한 페이지 번호가 속한 블럭 [8]
				int beginPerPage = 0;	// 각 페이지의 시작 글번호를 저장[10]
				//[1]------------------------끝--------------------------------------
				int beginPerBlock = 0;
			%>
			
			<%
				// 현재 List.jsp페이지의 검색란에 검색어를 입력했다면... 그 검색어 및 검색기준 필드값을 한글처리
				request.setCharacterEncoding("UTF-8");
				
				// 만약 검색어가 입력되었다면?
				if(request.getParameter("keyWord") != null) {
					// 검색기준값 저장
					keyField = request.getParameter("keyField");
					
					// 검색어값 저장
					keyWord = request.getParameter("keyWord");
				}
			
				// 만약에 [목록]링크를 클릭했을때.. (List.jsp를 재요청했을때.. )
				// input 태그의 hidden으로 요청한 name속성값이 reload인값이 존재하고!!
				if(request.getParameter("reload") != null) {
					// 만약 List.jsp 페이지로 다시 요청받은 값이 true와 같을때..
					if(request.getParameter("reload").equals("true")) {
						keyWord = "";
					}
				}
				
				// 검색어와 검색기준값에 의한 select하기 위한 (DB작업을 위한) DAO객체 생성
				BoardDao dao = new BoardDao();
					
				// 각각의 DTO객체들을 다시 Vector에 담아서 vec 객체에 전달
				Vector<BoardDto> vec = dao.getBoardList(keyField, keyWord);
				
				//[2] 글전체 개수
				totalRecord = vec.size();		// 게시판에 저장된 전체 글 개수
				
				//[4] 전체 페이지 수 구하기 = 게시판에 저장된 전체 글의 개수 / 한 페이지당 보여질 글의 개수
				/*(설명)
					게시판에 저장된 전체 글의 개수가 26개라고 가정하면 게시판의 전체 페이지 수는?? 한 페이지당 만약 5개의 글을 보여지게 한다면 6페이지가 필요하다.
					26.0 / 5 = 5.2                      =>    6
					(double)totalRecord / numPerPage)       ceil()
					Math클래스의 ceil() 메소드는 실수값을 넣으면 무조건 소수점 첫째자리를 올림처리 한다.
					
				*/
				totalPage = (int) Math.ceil((double)totalRecord / numPerPage); 
				if(totalPage == 0) totalPage = 1;
				//[7] 현재 보여질 페이지 번호 구하기
				//(설명) 게시판 하단 부분에 보여질       이전3개 < 1 2 3 > 다음3개
				//1 2 3 4 5 중에 하나의 페이지번호를 선택하여 다시 List.jsp페이지로 재요청을 하면?
				//1 2 3 4 5 중 선택한 하나의 페이지 번호가 List.jsp페이지러 넘어 오면서
				//nowPage(현재 보여질 페이지 번호)을 얻는다.
				//만약 1 2 3 4 5 중에 현재 선택한 페이지 번호가 있을까??
				if(request.getParameter("page") != null) {
					nowPage = Integer.parseInt(request.getParameter("page"));
				}
				//[8]현재 보여질 페이지가 속한 블럭 번호 구하기
				nowBlock = (int) Math.ceil((double)nowPage / pagePerBlock);
			
				beginPerBlock = (nowBlock-1) * pagePerBlock + 1;
				
				//[9]전체 블럭수 = 전체 페이지 수 / 한 블럭 당 묶여질 페이지 수
				totalBlock = (int) Math.ceil((double)totalPage / pagePerBlock); 
				
				//[10]각 페이지마다 맨 위쪽에 첫번째로 보여질 시작 글번호 구하기
				//현재보여질 페이지 번호 * 한 페이지 당 보여질 글의 개수
				beginPerPage = (nowPage-1) * numPerPage;
			%>

			<!-- 게시판 시작 -->
			<div id="qnaPanel" class="container">
				<br>
				<!-- 버튼, 검색창 시작-->
				<div class="row">
					<!-- 버튼부분 -->
					<div class="col-xs-6 col-sm-6">
						<div class="btn-group" role="group">
							<button type="button" class="btn btn-default" onclick="fnlist(); ">목록</button>
							<button type="button" class="btn btn-danger" onclick="fnWrite(); ">글쓰기</button>
						</div>
					</div>
					<!-- 검색창 부분 -->
					<div class="col-xs-6 col-sm-6" align="right">
						<form class="form-inline" action="List.jsp?page=1" name="search" method="post">
							<select class="form-control" name="keyField" size="1">
								<option value="name"> 이름
								<option value="subject"> 제목
								<option value="content"> 내용
							</select>
							<div class="input-group">
								<input class="form-control" type="text" name="keyWord" >
								<span class="input-group-btn">
									<input class="btn btn-warning" type="button" value="찾기" onClick="check()">
								</span>
							</div>
							<input type="hidden" name="page" value= "0">
						</form>
					</div>
				</div>
				<!--  버튼, 검색창 끝 -->
							
				<!-- 게시글 리스트 시작 -->
				<div class="row">
					<div class="text-center col-sm-12">
						<div id="boardPanel">
							<table id="qnaboard" class="table table-hover" >
								<thead>
									<tr align=center >
										<td class="list_index" width="10%"> 번호 </td>
										<td class="list_subject" width="45%"> 제목 </td>
										<td class="list_name" width="15%"> 이름 </td>
										<td class="list_date" width="15%"> 날짜 </td>
										<td class="list_count" width="15%"> 조회수 </td>
									</tr>
								</thead>
								<tbody>
								<%
									//특정날짜형식으로 바꿔주는 객체
									SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
									
									// 만약 벡터 내부에 글들이 하나라도 존재 하지 않다면?
									if(vec.isEmpty()) {
										dao.initiateNum();
										%>
										<tr>
											<td colspan="5" align="center">등록된 글이 없습니다. </td>
										</tr>
										<%	
									}else {//[11] 각각의 한 페이지마다 맨 위에 첫번째로 보여질 글 번호부터 반복
										for(int i = beginPerPage; i < beginPerPage+numPerPage; i++) {
											// i : 각 페이지 첫번째 글에 해당하는 벡터 인덱스 값!
											
											// 만약 i 가 총 글의 개수와 같아지면.. 필요없는 반복문은 중지
											if(i == totalRecord) break;
					
											//벡터에 담긴 글(BoardDto)객체 얻기
											BoardDto dto = (BoardDto) vec.get(i);
											
										%>
										<tr align=center>
											<td class="list_index" > <%=dto.getNum() %> </td>
											<%-- 게시판 글리스트 중에서.. 글제목을 클릭했을때.. fnRead()함수 호출 시 글번호를 전달하여.. form을 실행!!
																											false로 반환하면 함수 안에서 페이지 전환이 일어남 --%>
											<td class="list_subject" align=left> 
											<%= dao.useDepth(dto.getDepth()) %>
											<a href="#" onclick="fnRead('<%=dto.getNum() %>'); return false;"><%=dto.getSubject() %></a> </td>
											<td class="list_name"> <%=dto.getName() %> </td>
											<td class="list_date"> <%=s.format(dto.getRegdate()) %> </td>
											<td class="list_count"> <%=dto.getCount() %> </td>
										</tr>
										<%
										}
									}
								%>
								</tbody>
							</table>
						</div>
					</div>
				</div>
				<!-- 게시글 리스트 끝 -->
							
				<!-- 페이징 시작 -->
				<div class="row">
	    
					<!-- [12] 페이지 번호 출력
					한블럭당 몇개의 페이지 번호를 출력할 것인지 기준을 잡아야 하는데..
					int pagePerBlock = 3; // 한 블럭 당 묶여질 페이지 수
					1 2 3 <---- 1블럭
					4 5 6 <---- 2블럭
					 -->
					<div class="text-center col-sm-12">		
						<nav>
							<ul class="pagination pagination-sm ">
							
								<!-- 	-------------이전 3페이지 ◀ ------------- -->
								<!-- 	현재 블록이 첫번째 블록이 아니면 이전 버튼 생성! -->
								<!-- 	이전 버튼 클릭 시 이전 블록의 마지막 페이지로 이동 -->
							
								<li>
									<!-- 현재 블록이 첫번째 블록일때는 ◀버튼을 누르면 블록 전환 없이 첫번째 페이지로 이동해야됨 -->
									<a href="List.jsp?page=<%=nowBlock == 1 ? beginPerBlock : beginPerBlock-1 %>" aria-label="Previous">
						        		<span aria-hidden="true">&laquo;</span>
						      		</a>
								</li>
								
								<!-- 	-------------이전 3페이지 ◀ ------------- -->
								
							<%							
								//-------------- 페이지 번호 --------------
								// 게시판 하단 Go to Page 부분에 페이지 번호 출력
								// 한 블럭 당 3개의 페이지번호를 보여줘야 하므로...
								// 한 블럭 당 묶여질 페이지 수 만큼 반복하면서... 페이지 번호 출력
								// (int i = 해당 블럭의 첫번째 페이지 번호; i < 해당 블럭의 첫번째 페이지 번호 + 블럭 당 페이지 수; i++)
								// (int i = 1; i < 1+3; i++) 1 2 3
								// (int i = 4; i < 4+3; i++) 4 5 6
								// ...
								for(int i = beginPerBlock; i < beginPerBlock+pagePerBlock; i++) {
									
									// 인덱스 만큼 페이지 번호 링크 출력
							%>
								<li <%= i == nowPage ? "class='active'" : "" %>><a href="List.jsp?page=<%=i%>"><%=i %> <span class="sr-only"></span></a></li>
							<%
									// 모든 페이지 출력 다했으면 반복문 탈출
									if(i == totalPage) break;
								}
								//-----------------끝-------------------
							%>
							
								<!-- 	------------- ▶ 다음 3페이지------------- -->
								<!-- 	현재 페이지가 마지막 블록이 아니라면 다음 버튼 생성      -->
								<!-- 	다음 버튼 클릭 시 다음 블록의 첫번째 페이지로 이동       -->
								
								<li>
									<!-- 현재 블록이 마지막 블록이면 ▶ 클릭 시 블록 전환없이 마지막 페이지로 이동해야됨 -->
									<a href="List.jsp?page=<%=nowBlock == totalBlock ? totalPage : beginPerBlock+pagePerBlock %>" aria-label="Next">
										<span aria-hidden="true">&raquo;</span>
									</a>
							    </li>
								<!-----------------▶ 다음 3페이지 끝------------------->
							</ul>
						</nav>
					</div>
					<!-- 페이징 끝 -->
				</div>
			</div>
			<!-- 게시글 끝 -->
			
			<%-- 현재 List.jsp페이지가 리로드하는지 안하는지 구별하기 위한 true값을 List.jsp에 요청함 --%>
			<form name="list" method="post">
				<input type="hidden" name="reload" value="true">
			</form>
		
			<%-- 게시판 글 리스트 목록 중에서 글제목링크를 클릭했을때..
				Read.jsp로 선택한 글번호, 글을 선택하기 위해 검색한 검색기준값, 글을 선택하기 위한 검색어값, 조회 유무, 돌아올 페이지 전달.
			--%>
			<form action="Read.jsp"name="read" method="post">
				<input type="hidden" name="num" >
				<input type="hidden" name="keyField" value="<%=keyField%>">
				<input type="hidden" name="keyWord" value="<%=keyWord%>">
				<input type="hidden" name="count" value="true">
				<input type="hidden" name="page" value="<%=nowPage%>">
			</form>
			
			<!-- 글 쓰기 버튼을 클릭했을때  -->
			<!-- post.jsp로 검색한 검색기준값, 검색어값, 돌아올 페이지 전달. -->
			<form action="post.jsp"name="write" method="post">
				<input type="hidden" name="keyField" value="<%=keyField%>">
				<input type="hidden" name="keyWord" value="<%=keyWord%>">
				<input type="hidden" name="page" value="<%=nowPage%>">
			</form>
			<br>
			<!-- footer 부분 -->
			<jsp:include page="../inc/bottom.jsp"/>
			<!-- footer 부분 -->
		</div>
	</BODY>
</HTML>