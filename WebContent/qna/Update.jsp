<%@page import="com.memo.BoardDto"%>
<%@page import="java.util.Vector"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<html>
	<head>		
		<!-- 공통으로 적용되는 부분 : 반응형 웹을 위한 설정, css설정 -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<link rel="stylesheet" href="../css/bootstrap.css">
		<link rel="stylesheet" href="../css/commons.css">
		<link href="http://cdnjs.cloudflare.com/ajax/libs/summernote/0.8.8/summernote.css" rel="stylesheet">
		<title>웹메모</title>
		
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
		
		
			function check() {
			   if (document.form.new_pass.value == "") {
				 alert("수정을 위해 패스워드를 입력하세요.");
			     form.new_pass.focus();
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
		
			<!-- include summernote css/js -->
			<script src="../js/summernote.js"></script>
			<script src="../lang/summernote-ko-KR.js"></script>

		<%
			request.setCharacterEncoding("UTF-8");
			int no = Integer.parseInt(request.getParameter("no"));
		%>
			<jsp:useBean id="dto" class="com.memo.BoardDto"/>
			<jsp:useBean id="dao" class="com.memo.BoardDao"/>
		<%
			dto = dao.getBoard(no, false);
		
		%>
			<div id="qnaPanel" class="container">
				<form name=form method=post action="UpdateProc.jsp" >
					<input type="hidden" name="no" value="<%=no %>" />
					<input type="hidden" name="name" value="<%=dto.getName() %>" />
					<br>
					<div class="row">
						<div class="col-sm-12">
							<div class="btn-group" role="group">
								<input class="btn btn-default" type=button value="뒤로" onClick="history.back()">
								<input class="btn btn-default" type=reset value="다시수정"> 
								<input class="btn btn-danger" type=button value="수정완료" onClick="check()">
							</div>
						</div>
					</div>
					<br>
					<div class="row">
						<div class="text-center col-sm-12">
							<div id="boardPanel" >
								<table width=100%>
									<tr>
								    	<td width=25%>
								    		<div class="controls">
								    			<p class="form-control-static"><%=dto.getName() %></p>
								    		</div>
								    	</td>
									</tr>
									<tr>
										<td width=25%>
											<div class="controls">
												<input type=email name=email maxlength=20 class="form-control" placeholder="이메일" value="<%=dto.getEmail()%>">
											</div>
										</td>
									</tr>
									<tr>
										<td width=100%>
											<div class="controls">
												<input type=text name=subject maxlength=100 class="form-control" placeholder="제목" value="<%=dto.getSubject()%>">
											</div>
										</td>
									</tr>
									<tr>
										<td width=100%>
											<div class="controls">
								    			<textarea class="summernote" name=content rows=20 cols=50 class="form-control" ><%=dto.getContent() %></textarea>
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
															uploadFile(file[0],this);
 									    		   		}
  									    		   },
										      });
										    	function uploadFile(file,el) {
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
										<td width=25%>
											<div class="controls">
												<input type=password name=new_pass maxlength=20  class="form-control" placeholder="비밀번호">
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