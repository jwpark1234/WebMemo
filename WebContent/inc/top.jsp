<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<!-- 공통으로 적용되는 부분 : 반응형 웹을 위한 설정, css설정 -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<link rel="stylesheet" href="../css/bootstrap.css">
		<link rel="stylesheet" href="../css/commons.css">
		<title>웹메모</title>
				
		<!-- jQuery (부트스트랩의 자바스크립트 플러그인을 위해 필요합니다) -->
	    <script src="https://code.jquery.com/jquery-3.2.1.js"></script>
	    <!-- 모든 컴파일된 플러그인을 포함합니다 (아래), 원하지 않는다면 필요한 각각의 파일을 포함하세요 -->
	    <script src="../js/bootstrap.js"></script>
		<script type="text/javascript">
			//*************로그인****************************
			// 로그인 버튼 클릭 시 입력값의 유효성을 체크하는 함수
			function loginCheck() {
				var userId = $('#login_userId').val();
				var pw = $('#login_pw').val();
				
				if(userId == "") { // 아이디를 입력하지 않은 경우
					alert("아이디를 입력하세요.");
					$('#login_userId').focus();
					return;
				}
				else if(pw == "") { // 비밀번호를 입력하지 않은 경우
					alert("비밀번호를 입력하세요.");
					$('#login_pw').focus();
					return;
				}
				
				// 아이디와 비밀번호를 입력했다면 로그인처리 서블릿으로 값을 전달(post방식)
 				$.post( "../UserLoginServlet", { userId : userId, pw : pw })
					 .done(function( result ) { // 서블릿에서 로그인 처리하고 결과값을 받음
						 
					if(result == 1) { // 로그인 성공 : 로그인/로그아웃 처리하는 페이지로 이동
						location.href="../user/loginout.jsp?userId=" + userId;
					}
					else if(result == 0){ // 비밀번호 불일치
						alert("비밀번호가 틀렸습니다.");
						$('#login_pw').focus();
					}
					else if(result == -1) { // 아이디 없음
						alert("존재하지 않는 아이디입니다.");
						$('#login_userId').focus();
					}
					else if(result == -2) { // 데이터베이스 오류
						alert("데이터베이스 오류입니다.");
					}
				});
			}
			//**************************************************
			
			//**********************회원가입*************************
			// 회원가입 시 아이디 중복검사 버튼을 클릭 시 중복검사하는 함수
			function idCheck() {
				var userId = $('#join_userId').val();
				
				if(userId == "") { // 아이디를 입력안함
					alert("아이디를 입력하세요.");
					$('#join_userId').focus();
					return;
				}
				
				// 입력한 아이디를 아이디중복체크하는 서블릿으로 전달
				$.post( "../UserCheckServlet", { userId : userId })
					 .done(function( result ) { // 서블릿처리 결과를 받아옴
						 
					if(result == 1) { // 중복 없음
						alert("사용할 수 있는 아이디입니다.");
						$('#join_pw').focus();
					}
					else if(result == 0){ // 중복
						alert("이미 존재하는 아이디입니다.");
						$('#join_userId').focus();
					}
					else if(result == -1) { // 데이터베이스 오류
						alert("데이터베이스 오류입니다.");
					}
				});
			}
			
			// 회원가입, 회원정보 수정 시 비밀번호, 비밀번호 확인이 일치하는 확인하는 함수
			function pwCheck() {
				
				// 회원가입 시
				var join_pw = $('#join_pw').val();
				var join_pw_confirm = $('#join_pw_confirm').val();
				
				// 비밀번호 입력창 옆에 메세지 출력
				if(join_pw != join_pw_confirm) {
					$('#join_passCheck').html("비밀번호 불일치");
				}
				else {
					$('#join_passCheck').html("비밀번호 일치");
				}
				
				// 회원정보 수정 시
				var myinfo_pw = $('#myinfo_pw').val();
				var myinfo_pw_confirm = $('#myinfo_pw_confirm').val();
				
				// 비밀번호 입력창 옆에 메세지 출력
				if(myinfo_pw != myinfo_pw_confirm) {
					$('#myinfo_passCheck').html("비밀번호 불일치");
				}
				else {
					$('#myinfo_passCheck').html("비밀번호 일치");
				}
			}
			
			// 이메일을 입력하거나 수정한 경우
			// 이메일 인증버튼 옆에 인증 메세지를 초기화함
			function resetAuth() {
				$('#join_emailCheck').html('이메일 미인증');
			}
			
			var authNum; // 이메일 인증번호 저장 변수
			
			// 이메일 인증버튼을 눌렀을 경우 호출되는 함수
			function authProc() {
				var email = $('#join_email').val();
				
				if(email == "") { //이메일을 입력안함
					alert("이메일을 입력해주세요.");
					return;
				}
				
				// 이메일주소를 이메일보내기 서블릿으로 전달
				$.post( "../EmailServlet", { email : email })
				 .done(function( result ) { // 서블릿 처리 후 인증번호를 받아옴
					 
					 if(result == "fail") { // 이메일 주소가 잘못됨
						 alert("유효한 이메일 주소가 아닙니다.")
						 return;
					 }	 
					 
					 authNum = result; // 인증번호
					 $('#emailAuthModal').modal('show'); // 인증번호 입력모달 띄움
				});
			}
			
			// 이메일 인증번호 입력모달의 확인버튼을 클릭할 경우 호출되는 함수
			function authCheck() {
				var inputNum = $('#authNum').val();
				
				if(inputNum == "") { // 인증번호 미입력
					alert("인증번호를 입력하세요.");
					return;
				}
				
				if(inputNum != authNum) { // 인증번호 틀림
					alert("인증번호가 틀렸습니다. 다시 입력하세요.");
					$('#authNum').val('');
					return;
				}
				else {
					alert("인증완료");
					$('#emailAuthModal').modal('hide'); // 모달창 끔
					$('#join_emailCheck').html('이메일 인증완료'); // 이메일인증버튼 옆에 인증 메세지를 변경
				}
			}
			
			// 회원가입 버튼 클릭시 입력값 유효성 체크하는 함수
			function joinCheck() {
				
				// 비밀번호가 일치하는지 확인
				if($('#join_passCheck').html() != '비밀번호 일치') {
					alert("비밀번호가 일치하지 않습니다.");
					return;
				}
				
				// 이메일 인증을 했는지 확인
				if($('#join_emailCheck').html() != '이메일 인증완료') {
					alert("이메일 인증을 해주세요.")
					return;
				}
				
				// form태그에 사용자가 입력한 모든 값을 직렬화 하여 저장
				// input태그 name값=입력한값&input태그 name값=입력한값& ... 형식의 문자열로 저장됨
				var joinData = $('#joinForm').serialize();
				joinData += "&type=add"; // 처리 유형은 회원가입!
				
				// 직렬화한 데이터를 회원가입,수정 서블릿으로 전달한다.
				$.ajax({
 					type : "POST",
 					url : "../UserAddEditServlet",
 					data : joinData,
 					contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
 					dataType: 'html',
 					success : function( result ) { // 서블릿 처리 성공 시 결과값 전달받음
 						
 						if(result == 1) { // 회원가입 성공
							alert("회원가입을 축하드립니다!");
							location.href="../main/index.jsp";
 						}
						else if(result == 0) { // 빈항목이 있음
							alert("모든 항목을 입력하세요.");
						}
						else if(result == -1) { // 아이디 유효하지 않음
							alert("유효한 아이디가 아닙니다.");
							$('#join_userId').focus();
						}
						else if(result == -2) { // 아이디 중복
							alert("이미 등록된 아이디입니다.");
							$('#join_userId').focus();
						}
						else if(result == -3) { // 비밀번호 유효하지 않음
							alert("비밀번호는 8~12자로 입력해주세요.");
							$('#join_pw').focus();
						}
						else if(result == -4) { // 이름 유효하지 않음
							alert("이름은 2~10자로 입력해주세요.");
							$('#join_name').focus();
						}
						else if(result == -5) { // 전화번호가 유효하지 않음
							alert("전화번호는 000-0000-0000 형태로 입력해주세요.");
							$('#join_phone').focus();
						}
		 			}
				});
			}
			//******************************************************

			//******************회원정보 수정**************************
			//회원정보 모달에서 회원정보수정 버튼을 클릭할 경우
			//회원정보 모달을 숨기고 회원정보 수정 모달을 띄우는 함수
			function myinfoEdit() {
				$('#myinfoModal').modal('hide'); // 회원정보 모달 숨김
				$('#myinfoModal').on('hidden.bs.modal', function (e) { // 회원정보 모달 숨김이 완료되면
					$('#editInfoModal').modal('show'); // 회원정보 수정 모달 띄움
				});
			}
			
			// 회원정보 수정 버튼 클릭 시 호출되는 함수
			function myinfoCheck(userId) {
				// 입력한 값을 직렬화하여 저장
				var myinfoData = $('#myinfoForm').serialize();
				myinfoData += "&type=edit&userId=" + userId; // 유형은 수정! + 회원아이디도 전달
				
				// 직렬화한 값을 회원가입/수정 서블릿으로 전달
				$.ajax({
 					type : "POST",
 					url : "../UserAddEditServlet",
 					data : myinfoData,
 					contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
 					dataType: 'html',
 					success : function( result ) { // 결과값 전달받음
 						
 						if(result == 1) { // 수정완료
							alert("정보수정을 완료하였습니다!");
							location.href="../main/index.jsp";
 						}
						else if(result == 0) { // 빈 항목 있음
							alert("모든 항목을 입력하세요.");
						}
						else if(result == -1) { // 비밀번호 불일치
							alert("비밀번호가 일치하지 않습니다.");
							$('#join_pw').focus();
						}
		 			}
				});
			}
			//*****************************************************
			
			//*********************회원탈퇴******************************
			//회원정보 모달에서 회원탈퇴 버튼 클릭 시 호출되는 함수
			function dropUser() {
				var ok = confirm("정말 탈퇴하시겠습니까?"); // 탈퇴여부 재확인
				
				if(ok == true){ // 탈퇴한다
					location.href = "../user/deleteUser.jsp"
				}
				else if(ok == false){ // 탈퇴취소
					return;
				}
			}
			//*******************************************************
			
			//*********************세션체크****************************
			//로그인하지 않은 이용자가 메모 페이지를 접속할 경우 접속을 차단하는 함수
			function sessionCheck(userId) {
				if(userId == "") {
					alert("로그인 후 이용해주세요.");
					location.href = "../main/index.jsp";
				}else {
					location.href = "../memo/List.jsp";
				}
			}
			//********************************************************
			
			//**********************모달창 종료 버튼**************************
			// 로그인/회원가입 모달창을 종료할 경우 호출되는 함수
			function resetModal1() {
				// 모달창이 숨겨지면 입력한 값을 지우고 이메일 인증도 리셋시킴
				$('#loginsignupModal').on('hidden.bs.modal', function(e) {
 					$("#loginsignupModal .modal-body input").val("");
 					resetAuth();
				});
			}
			
			// 이메일 인증번호 입력모달창을 종료할 경우 호출되는 함수
			function resetModal2() {
				// 모달창이 숨겨지면 입력한 인증번호값을 지우고 이메일 인증도 리셋시킴
				$('#emailAuthModal').on('hidden.bs.modal', function(e) {
 					$('#authNum').val("");
 					resetAuth();
				});
			}
			//**************************************************************
		</script>
	</head>
	<body>
		<jsp:useBean id="dao" class="com.memo.UserDao"/>
		<jsp:useBean id="dto" class="com.memo.UserDto"/>
		<%
			request.setCharacterEncoding("UTF-8");
		

			String userId = null;
			
			// 주소값은 데이터베이스에 우편번호%주소%상세주소 형식으로 저장됨 
			// 주소값을 split함수로 다시 나눌때 나눠진 값을 저장할 배열이 필요함 
			String[] addrArr = {"","",""}; 
			
			// 세션이 있으면 아이디, 회원정보, 주소값 나눔
			if(session.getAttribute("userId") != null) {
				userId = (String) session.getAttribute("userId");
				dto = dao.getUser(userId);
				addrArr = dto.getAddress().split("%");
			}
			
			// 내비게이션 메뉴 선택상태를 css로 표시하기 위한 변수
			// 메인페이지 : active = null
			// 메모 페이지 : active = memo
			// 질문게시판 페이지 : active = qna
			String active = request.getParameter("active");
		%>
		
		<!-- 상단 메뉴 -->
		<header>
			<nav class="navbar navbar-default">
				<div class="container-fluid">
					<!-- 내비게이션 -->
					<div class="navbar-header" >
						<!-- 화면창이 작을 경우 우측상단에 나타나는 메뉴버튼 -->
						<!-- 화면창이 작아지면 메뉴들이 숨겨지는데 이 버튼을 누르면 collapseMenu 영역이 보여짐 -->
						<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#collapseMenu">
							<span class="icon-bar"></span>
							<span class="icon-bar"></span>
							<span class="icon-bar"></span>
						</button> 
						<!-- 로고  -->
						<a class="navbar-brand" href="../main/index.jsp"><div id="logo">WebMemo</div></a>
					</div>
					<!-- 실제 내비게이션 메뉴 -->
					<!-- 화면창이 클 경우 일반적인 메뉴로 보여지고 -->
					<!-- 화면창이 작을 경우 메뉴들이 숨겨지고 메뉴버튼을 클릭한 경우에만 메뉴들이 보여짐 -->
					<div class="navbar-collapse collapse" id="collapseMenu">
						<ul class="nav navbar-nav">
							<!-- active변수값에 따라 li태그의 class값을 active로 설정 -> class=active 시 css에 보여짐  -->
							<li <%=active.equals("memo") ? "class='active'" : "" %>>
								<!-- 메모페이지는 로그인 여부를 체크한다. -->
								<a href="#" onclick="sessionCheck('<%=userId == null ? "" : userId%>')">메모</a>
							</li>
							<li <%=active.equals("qna") ? "class='active'" : "" %>>
								<a href="../qna/List.jsp?page=1">질문게시판</a>
							</li>
						</ul>
							
						<!-- 아이디, 로그인/로그아웃/회원정보 버튼-->												
						<div class="navbar-form navbar-right">
	                   	<%
							if(session.getAttribute("userId") == null) {
	                   	%>
		                   	<!-- 로그인 안한 상태 : 로그인/회원가입 모달창 버튼 -->
	                   		<a class="btn btn-danger" data-toggle="modal" data-target="#loginsignupModal"><span class="glyphicon glyphicon-user"></span></a>
	                   	<%
							} else {
	                   	%>
	                   		<!-- 로그인 상태 : 아이디 표시 + 로그아웃 , 회원정보 버튼 -->
	                   		<b><%=userId %></b> 님.&nbsp;&nbsp;
	                   		<a class="btn btn-danger" data-toggle="modal" data-target="#myinfoModal"><span class="glyphicon glyphicon-user"></span></a>
	                   		<a class="btn btn-warning" href="../user/loginout.jsp"><span class="glyphicon glyphicon-off"></span></a>	
	                   	<%
							}
	                   	%>
	                   	</div>
					</div>
				</div> 
			</nav>
		</header>
		<!-- 상단 메뉴 끝 -->
		
		<!-- 로그인 / 회원가입 모달 창 시작 -->
		<!-- data-backdrop="static" data-keyboard="false" : 모달창 영역 밖을 클릭하거나 esc버튼을 눌려도 모달창이 꺼지지 않게 함 -->
		<div class="modal fade bs-modal-sm" id="loginsignupModal" role="dialog" data-backdrop="static" data-keyboard="false" aria-labelledby="mySmallModalLabel" aria-hidden="true">
	 		<div class="modal-dialog modal-sm">
	 			<!-- 실제 모달창 내용영역 -->
	  			<div class="modal-content">
					<div class="bs-example bs-example-pills">
						<!-- 내부 탭. pill : 알약모양의 탭-->
						<ul id="myTab" class="nav nav-pills nav-justified">
							<li class="active">
								<a href="#login" data-toggle="pill" >로그인</a>
							</li>
							<li class="">
								<a href="#join" data-toggle="pill">회원가입</a>
							</li>
						</ul>       
					</div>
					<div class="modal-body">
						<div id="myTabContent" class="tab-content">
			
							<!-- 로그인 탭 시작-->
							<!-- 로그인 버튼 : 아이디, 비밀번호 유효성 체크 후 로그인처리 loginCheck() -->
							<!-- 닫기 버튼 : 입력값 초기화 resetModal1() -->
							<div class="tab-pane fade active in" id="login">
							    <form id="loginForm" name = "loginForm" class="form-horizontal">
									<div class="controls">
										<input type="text" id="login_userId" name="userId" class="form-control" placeholder="아이디" maxlength="12" class="input-medium">
									</div>
									<div class="controls">
										<input type="password" id="login_pw" name="pw" class="form-control" placeholder="비밀번호" maxlength="12" class="input-medium">
									</div>
									<br>
									<div class="controls text-center">
										<button type="button" class="btn btn-danger" onclick="loginCheck();">로그인</button>
										<button type="button" class="btn btn-default" data-dismiss="modal"  onclick="resetModal1();">닫기</button>
									</div>
			    				</form>
							</div>
							<!-- 로그인 탭 끝 -->
			
							<!-- 회원가입 탭 시작-->
							<!-- 회원가입 버튼 : 입력값 유효성 체크 후 회원가입 처리 joinCheck() -->
							<!-- 닫기 버튼 : 입력값 초기화 resetModal1() -->
							<div class="tab-pane fade" id="join">
							    <form id="joinForm" name = "joinForm" class="form-horizontal">
									<div class="controls">
										<input type="text" id="join_userId" name="userId" class="form-control" placeholder="아이디" maxlength="12" class="input-large" style="display: inline;">
										<button id="idCheck_btn" class="btn btn-warning" onclick="idCheck();" type="button">중복확인</button>
									</div>
									<div class="controls">
										<input type="password" id="join_pw" name="pw" onkeyup="pwCheck();" class="form-control" placeholder="비밀번호" maxlength="12" class="input-large"> 
									</div>
									<div class="controls">
										<!-- onkeyup : 사용자가 해당 input태그에 키보드로 입력할때마다 호출되는 이벤트리스너 -->
										<input type="password" id="join_pw_confirm" name="pw_confirm" onkeyup="pwCheck();" class="form-control" placeholder="비밀번호 확인" maxlength="12" class="input-large">
										<font id ="join_passCheck" name="check" size="2" color="red">비밀번호 불일치</font>
									</div>
									<div class="controls">
										<input type="text" id="join_name" name="name" class="form-control" placeholder="이름" maxlength="10" class="input-large">
									</div>
									<div class="controls">
										<input type="text" id="join_postcode" name="postcode" placeholder="우편번호" class="form-control input-large" style="display: inline;">
										<!-- 다음 주소 api로 주소를 검색한다. -->
										<button type="button" onclick="sample6_execDaumPostcode('<%="join" %>')" class="btn btn-warning">우편번호 찾기</button><br>
									</div>
									<div class="controls">
										<input type="text" id="join_address1" name="address1" placeholder="주소" class="form-control input-large" >
									</div>
									<div class="controls">
										<input type="text" id="join_address2" name="address2" placeholder="상세주소" class="form-control input-large" >
									</div>			
									<div class="controls">
										<input type="email" id="join_email" name="email" onkeyup="resetAuth();" class="form-control" placeholder="이메일" maxlength="20" class="input-large" style="display: inline;">
										<button type="button" class = "btn btn-warning" onclick="authProc();">인증번호</button>
										<br><font id ="join_emailCheck" size="2" color="red">이메일 미인증</font>
									</div>
									<div class="controls">
										<input type="text" id="join_phone" name="phone" class="form-control" placeholder="전화번호" maxlength="13" class="input-large" style="display: inline;">
									</div>
									<br>
									<div class="control-group">
										<div class="controls text-center">
											<button type="button" class="btn btn-danger" onclick="joinCheck();">회원가입</button>
				              				<button type="button" class="btn btn-default" data-dismiss="modal" onclick="resetModal1();">닫기</button>
					              		</div>
					            	</div>
			       				</form>
							</div>
							<!-- 회원가입 탭 끝 -->
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- 로그인 / 회원가입 모달 끝 -->
		
		<!-- 회원정보 / 탈퇴 모달 시작 -->
		<!-- 정보수정 버튼 : 회원정보 모달을 끄고 회원정보 수정 모달 띄움 myinfoEdit()-->
		<!-- 회원탈퇴 버튼 : 탈퇴여부 확인 후 탈퇴처리 dropUser() -->
		<div class="modal fade bs-modal-sm" id="myinfoModal" role="dialog" data-backdrop="static" data-keyboard="false" aria-hidden="true">
	 		<div class="modal-dialog modal-sm">
	  			<div class="modal-content">
					<!-- 모달 제목 -->
					<div class="modal-header">
						회원정보
					</div>
					<div class="modal-body">
						<table id="myinfo">
							<tr>
								<td width="20%" height="50">아이디</td>
								<td>&nbsp;&nbsp;</td>
								<td>
									<div class="controls">
										<p class="form-control-static"><%=userId %></p>
									</div>
								</td>
							</tr>
							<tr>
								<td width="20%" height="50">이름</td>
								<td>&nbsp;&nbsp;</td>
								<td>
									<div class="controls">
										<p class="form-control-static"><%=dto.getName() %></p>
									</div>
								</td>
							</tr>
							<tr>
								<td width="20%" height="50">주소</td>
								<td>&nbsp;&nbsp;</td>
								<td>
									<div class="controls">
										<p class="form-control-static"><%="(" + addrArr[0] + ") " + addrArr[1] + " " + addrArr[2] %></p>
									</div>
								</td>
							</tr>
							<tr>
								<td width="20%" height="50">이메일</td>
								<td>&nbsp;&nbsp;</td>
								<td>
									<div class="controls">
										<p class="form-control-static"><%=dto.getEmail() %></p>
									</div>
								</td>
							</tr>
							<tr>
								<td width="20%" height="50">전화번호</td>
								<td>&nbsp;&nbsp;</td>
								<td>
									<div class="controls">
										<p class="form-control-static"><%=dto.getPhone() %></p>
									</div>
								</td>
							</tr>
						</table>
						<hr>
						<div class="control-group">
							<div class="controls text-center">
								<button type="button" class="btn btn-danger" onclick="myinfoEdit();">정보수정</button>
								<button type="button" class="btn btn-warning" onclick="dropUser();">회원탈퇴</button>
								<button type="button" class="btn btn-default" data-dismiss="modal">닫기</button>
							</div>
						</div>
					</div>	
				</div>
			</div>
		</div>
		<!-- 회원정보 / 탈퇴 모달 끝 -->
		
		<!-- 회원정보 수정 모달 -->
		<!-- 수정완료 버튼 : 입력값 유효성 체크 myinfoCheck() -->
		<div class="modal bs-modal-sm" id="editInfoModal" role="dialog" data-backdrop="static" data-keyboard="false" aria-hidden="true">
	 		<div class="modal-dialog modal-sm">
	  			<div class="modal-content">
					<div class="modal-header">
						회원정보 수정
					</div>
					<div class="modal-body">
	    				<form id="myinfoForm" name = "myinfoForm" class="form-horizontal">
							<div class="controls">
								<!-- 아이디는 수정할 수 없으므로 수정할 수 없게 고정속성을 준다! -->
								 <p class="form-control-static"><%=userId %></p>
							</div>
							<div class="controls">
								<input type="password" id="myinfo_pw" name="pw" onkeyup="pwCheck();" class="form-control" placeholder="비밀번호" maxlength="12" class="input-large" >
							</div>
							<div class="controls">
								<input type="password" id="myinfo_pw_confirm" name="pw_confirm" onkeyup="pwCheck();" class="form-control" placeholder="비밀번호 확인" maxlength="12" class="input-large" >
								<font id ="myinfo_passCheck" size="2" color="red">비밀번호 불일치</font>
							</div>
							<div class="controls">
								<input type="text" id="myinfo_name" name="name" class="form-control" placeholder="이름" maxlength="10" class="input-large" value="<%=dto.getName()%>">
							</div>
							<div class="controls">
								<input type="text" id="myinfo_postcode" name="postcode" placeholder="우편번호" class="form-control input-large" value="<%=addrArr[0] %>" style="display: inline;">
								<button type="button" onclick="sample6_execDaumPostcode('<%="myinfo" %>')" class="btn btn-warning">우편번호 찾기</button><br>
							</div>
							<div class="controls">
								<input type="text" id="myinfo_address1" name="address1" placeholder="주소" value="<%=addrArr[1] %>" class="form-control input-large">
							</div>
							<div class="controls">
								<input type="text" id="myinfo_address2" name="address2" placeholder="상세주소" value="<%=addrArr[2] %>" class="form-control input-large">
							</div>			
							<div class="controls">
								<input type="email" id="myinfo_email" name="email" class="form-control" placeholder="이메일" maxlength="20" class="input-large" value="<%=dto.getEmail()%>" style="display: inline;">
							</div>
							<div class="controls">
								<input type="text" id="myinfo_phone" name="phone" class="form-control" placeholder="전화번호" maxlength="13" class="input-large" value="<%=dto.getPhone()%>" style="display: inline;">
							</div>
							<hr>
							<div class="control-group">
								<div class="controls text-center">
									<button type="button" class="btn btn-danger" onclick="myinfoCheck('<%=userId%>');">수정완료</button>
									<button type="button" class="btn btn-default" data-dismiss="modal" onclick="location.reload();">닫기</button>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
		<!-- 회원정보 수정 모달 끝 -->
				
		<!-- 이메일 인증번호 모달 -->
		<!-- 확인 버튼 : 인증번호 체크 authCheck() -->
		<!-- 닫기 : 인증 초기화 resetModal2() -->
		<!-- tabindex=-1 : 부모 모달창과 자식모달창을 분리하여 종료버튼 클릭시 자식모달창인 이 모달만 종료시키기 위함 -->
		<div class="modal fade bs-modal-sm" id="emailAuthModal" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard="false" aria-hidden="true">
	 		<div class="modal-dialog modal-sm">
	  			<div class="modal-content">
	  				<div class="modal-header">
	  					이메일 인증
	  				</div>
					<div class="modal-body">
					    <form class="form-horizontal">
							<div class="controls">
								<input type="text" id="authNum" name="authNum" class="form-control" placeholder="인증번호 4자리 입력" maxlength="10" class="input-medium">
							</div>
							<div class="controls text-center">
								<button type="button" class="btn btn-danger" onclick="authCheck();">확인</button>
								<button type="button" class="btn btn-default" data-dismiss="modal" onclick="resetModal2();">닫기</button>
							</div>
	    				</form>
					</div>
				</div>
			</div>
		</div>
		<!-- 이메일 인증번호 모달 끝 -->
				
		<!-- 다음 주소 API -->
		<!-- type값은 회원가입/회원정보수정 모달의 input태그를 구분하기 위함 -->
		<script src="http://dmaps.daum.net/map_js_init/postcode.v2.js"></script>
		<script>
		    function sample6_execDaumPostcode(type) {
		        new daum.Postcode({
		            oncomplete: function(data) {
		                // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.
		
		                // 각 주소의 노출 규칙에 따라 주소를 조합한다.
		                // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
		                var fullAddr = ''; // 최종 주소 변수
		                var extraAddr = ''; // 조합형 주소 변수
		
		                // 사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
		                if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
		                    fullAddr = data.roadAddress;
		
		                } else { // 사용자가 지번 주소를 선택했을 경우(J)
		                    fullAddr = data.jibunAddress;
		                }
		
		                // 사용자가 선택한 주소가 도로명 타입일때 조합한다.
		                if(data.userSelectedType === 'R'){
		                    //법정동명이 있을 경우 추가한다.
		                    if(data.bname !== ''){
		                        extraAddr += data.bname;
		                    }
		                    // 건물명이 있을 경우 추가한다.
		                    if(data.buildingName !== ''){
		                        extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
		                    }
		                    // 조합형주소의 유무에 따라 양쪽에 괄호를 추가하여 최종 주소를 만든다.
		                    fullAddr += (extraAddr !== '' ? ' ('+ extraAddr +')' : '');
		                }
		
		                // 우편번호와 주소 정보를 해당 필드에 넣는다.
		                document.getElementById(type + '_postcode').value = data.zonecode; //5자리 새우편번호 사용
		                document.getElementById(type + '_address1').value = fullAddr;
		
		                // 커서를 상세주소 필드로 이동한다.
		                document.getElementById(type + '_address2').value = "";
		                document.getElementById(type + '_address2').focus();

		            }
		        }).open();
		    }
		</script>
	</body>
</html>