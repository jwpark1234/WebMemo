<%@page import="com.memo.UserDao"%>
<%@page import="com.memo.BoardDao"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<html>
	<head>
		<!-- 공통으로 적용되는 부분 : 반응형 웹을 위한 설정, css설정 -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<link rel="stylesheet" href="../css/bootstrap.css">
		<link rel="stylesheet" href="../css/commons.css">
		<link href="http://cdnjs.cloudflare.com/ajax/libs/summernote/0.8.8/summernote.css" rel="stylesheet">
		<title>웹메모</title>
	</head>
	<body>
		<div class="wrap">
			<!-- header 부분 -->
			<jsp:include page="../inc/top.jsp">
				<jsp:param value="qna" name="active"/>
			</jsp:include>
			<!-- header 부분 -->
		
			<!-- include summernote css/js -->
			<script src="../js/summernote.js"></script>
			<script src="../lang/summernote-ko-KR.js"></script>
			
			<script>
				// 모바일 여부 판단.
				var isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent) ? true : false;
				
				$(document).ready(function() {
					// 모바일에서는 파일업로드 버튼 추가
					if(isMobile) {
						$('#file').html('<input type="file" id="fileUpload">');
						
						$('#fileUpload').change(function (e) {
				   			if(e.target.files[0].size >= 10 * 1024 * 1024) {
				   				alert("10Mb이상은 업로드할 수 없습니다.");
				   				return;
				   			}
				   			uploadFile(e.target.files[0],$('#writeMemo_content'));
						});
					}
				});
			</script>

			<%
				request.setCharacterEncoding("UTF-8");
				BoardDao dao = new BoardDao();
				int no = Integer.parseInt(request.getParameter("no"));
				String keyField = request.getParameter("keyField");
				String keyWord = request.getParameter("keyWord");
				
				//부모의 글 제목, 글 내용
				String subject = dao.getBoard(no, false).getSubject();
				String content = dao.getBoard(no, false).getContent();
			%>
			<div id="qnaPanel" class="container">
				<form name=post method=post action="ReplyProc.jsp" >
					<input type="hidden" name="keyField" value="<%=keyField %>">
					<input type="hidden" name="keyWord" value="<%=keyWord %>">
					<input type="hidden" name="no" value="<%=no%>">
						
					<br>
					<!-- 버튼 영역 -->
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-group" role="group">
								<input class="btn btn-default" type=button value="뒤로" onClick="history.back()">
								<input class="btn btn-default" type=reset value="다시쓰기">
								<input class="btn btn-danger" type=submit value="등록" >
							</div>
						</div>
					</div>
					<br>
					<!-- 답글 작성 영역 -->
					<div class="row">
						<div class="text-center col-sm-12">
							<div id="boardPanel" >
								<table width="100%">
									<tr>
										<!-- 로그인 유무에 따라 작성자 표시 -->
								    	<td width=25%>
								    		<div class="controls">
								    	<%
								    		UserDao udao = null;
								    		String name = "";
											if(session.getAttribute("userId") != null) {
												udao = new UserDao();
												name = udao.getUser((String)session.getAttribute("userId")).getName();
									    %>
								    			<p class="form-control-static"><%=name %></p>
								    	<%
											} else {
									    %>
								     			<input type=text id="name" name=name maxlength=10 class="form-control" placeholder="작성자">
								    	<%
								    	 	}
								     	%>
								     		</div>
								     	</td>
									</tr>
									<tr>
										<td width="25%">
											<div class="controls">
										<%
											String email = "";
											if(session.getAttribute("userId") != null) {
												email = udao.getUser((String)session.getAttribute("userId")).getEmail();
											}
									    %>
								     			<input type=email id="email" name=email maxlength=20 class="form-control" value="<%=email %>" placeholder="이메일">
											</div>
										</td>
									</tr>
									<tr>
										<td width="100%">
											<div class="controls">
												<input type=text name=subject maxlength=100 value="<%=subject %>" class="form-control" placeholder="제목">
											</div>
										</td>
									</tr>
									<tr>
										<td width="100%">
											<div class="controls">
								    			<textarea class="summernote" name=content rows=20 cols=50 class="form-control" ><%=content+"<br><br>----------------------------------------------------------------------<br><br><br>" %></textarea>
								    		</div>
 										    <script> 
										    	$('.summernote').summernote({
										    		toolbar: [
										    	    		// [groupName, [list of button]]
										    	            ['style', ['bold', 'italic', 'underline', 'clear']],
										    	            ['font', ['strikethrough', 'superscript', 'subscript']],
										    	            ['fontsize', ['fontsize']],
										    	            ['color', ['color']],
										    	            ['para', ['ul', 'ol', 'paragraph']],
										    	            ['height', ['height']]
										    		],
										    		placeholder:"내용을 작성하세요. 텍스트나 10Mb 이하의 그림, 파일을 이곳에 넣어주세요.",
										        	lang: 'ko-KR',
										        	height:380,
 									    		   	callbacks: {
 									    		   		onImageUpload: function(file) {
	 									    		   		if(file[0].size >= 10 * 1024 * 1024) {
	 								    		   				alert("10Mb이상은 업로드할 수 없습니다.");
	 								    		   				return;
	 								    		   			}
															uploadfile(file[0],this);
 									    		   		}
  									    		   },
										      });
										    	function uploadfile(file,el) {
										    		var form_data = new FormData();
										    	    form_data.append('file', file);
										    		$.ajax({
											            type : 'post',
											            url : '../FileUploadServlet',
											            data : form_data,
											            processData : false,
											            contentType : false,
											            success : function( savefile ) { // 업로드 성공하면 파일명을 받아온다.
											            	var ext = savefile.split('.').pop().toLowerCase();

											            	// 이미지 파일이면 editor영역에 <img>태그로 뿌려준다.
											                if($.inArray(ext, ['gif','png','jpg','jpeg']) != -1) {
											                	if(ext == 'gif') {
											                		alert("gif파일은 썸네일을 제공하지 않습니다.");
											                	}
											                	$(el).summernote('editor.insertImage', "../upload/"+file.name);
											                }
											                else { // 일반 파일이면 다운로드 링크를 만들어 준다.
											                	$('.summernote').summernote('pasteHTML', "<a href='../download.jsp?path=upload&savefile=" + savefile + "&originfile=" + file.name + "'>" + file.name + "</a>" );
											                }
											            },
											            error : function(error) { // 실패할 경우 에러메세지 출력
											                console.log(error);
											                console.log(error.status);
											                alert(error + ", " +error.status);
											            }
											        });	
										    	}
 										    </script> 
										</td>
									</tr>
									<tr>
								    	<td>
								    		<div class="controls" id="file"></div>
								 		</td>
								    </tr>
									<tr> 
										<td width="25%">
											<div class="controls">
												<input type=password name=pass maxlength=20  class="form-control" placeholder="비밀번호">
											</div>
										</td>
									</tr>
								</table>
								<br>
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