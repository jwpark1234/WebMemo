<%@ page contentType="text/html;charset=UTF-8" %>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.memo.MemoDto"%>
<%@page import="java.util.Vector"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<!-- 공통으로 적용되는 부분 : 반응형 웹을 위한 설정, css설정 -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<link rel="stylesheet" href="../css/bootstrap.css">
 		<link rel="stylesheet" href="../css/commons.css">
		<link href="http://cdnjs.cloudflare.com/ajax/libs/summernote/0.8.8/summernote.css" rel="stylesheet">
		
		<title>웹메모</title>
		<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>	
		<script type="text/javascript">
		
			// 모바일 여부 판단.
			var isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent) ? true : false;
								
			// 페이지가 다 로드되면 호출되는 이벤트
			$(document).ready(function() {
				
			    $('#memoPanel').load('memo.jsp'); // 메모리스트 리로드
								
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
			
			//메모 모달창을 새 메모 작성 모달로 초기화하는 함수
			//새 메모 작성 모달/메모 수정 모달은 하나의 모달태그을 모달 내부의 태그만 고쳐서 공유하여 사용함
			//따라서 이 함수는 새 메모 작성 모달로 다시 세팅해주는 함수다.
			function resetModal() {
				// form태그 리셋
				$('#writeMemoModal').find('form')[0].reset();
				$('.summernote').summernote('code', '');
				// 색상은 기본 색상 선택
				$("#default").prop("checked", true);
						
				// 등록버튼으로 바꿈
				$("#writeMemo_btn").attr("onclick","writeProc('<%=(String)session.getAttribute("userId")%>');");
				$("#writeMemo_btn").html("등록");	
				
			}
			
			//*****************************************************************
						
			
			//************************새 메모 등록**********************************
			// 새 메모를 등록하는 함수
			function writeProc(userId) {
				// 입력값을 직렬화 한다.
				var writeData = $('#writeMemoForm').serialize();
				writeData += "&type=write&userId=" + userId; // 유형은 새메모작성! 아이디로 전달
				
				// 직렬화한 값을 메모 추가/수정 서블릿으로 전달한다
				$.ajax({
					type : "POST",
					url : "../MemoAddEditServlet",
					data : writeData,
					contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
					dataType: 'html',
					success : function( memoNum ) { // 업로드 성공하면 모달창을 끈다.   
		        		$('#writeMemoModal').modal('hide');
		        		$('#memoPanel').load('memo.jsp'); // 메모리스트 리로드
		        		resetModal(); // 모달 초기화
					}
				});
			}
			//******************************************************************
			
			
			//************************메모 수정************************************
			// 메모리스트의 메모를 클릭할 경우 새 메모 작성 모달을 메모 수정 모달로 바꾼다
			// 메모의 index값으로 메모의 내용을 채운다
			function editMemo(memoNum) {
				// 메모읽기 서블릿에서 해당 메모index를 전달
				$.post( "../MemoReadServlet", { memoNum : memoNum })
				 .done(function( memo ) { // 결과값으로 JSON객체를 전달받음
					 
					// 가져온 값으로 input태그의 value값들을 세팅함
					$("#writeMemo_memoNum").val(memoNum);
					$("#writeMemo_subject").val(memo.subject);
					$('.summernote').summernote('code', memo.content);
					// 메모 색깔 세팅
					$('#'+memo.color).prop("checked", true);
					
					// 모달 버튼을 수정버튼으로 바꾼다.
					$("#writeMemo_btn").attr("onclick","editProc();");
					$("#writeMemo_btn").html("수정");
					
					// 세팅이 끝나면 모달창 호출
					$('#writeMemoModal').modal('show');					
				});
			}
			
			// 수정버튼을 클릭하면 호출되는 함수
			function editProc() {
				// 수정한 값을 직렬화
				var editData = $('#writeMemoForm').serialize();
				var memoNum = $("#writeMemo_memoNum").val();
				editData += "&type=edit"; // 유형은 메모 수정!
	
				// 메모추가수정서블릿으로 전달
				$.ajax({
					type : "POST",
					url : "../MemoAddEditServlet",
					data : editData,
					contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
					dataType: 'html',
					success : function( ) { // 업로드 성공하면 모달창을 끈다.
		        		$('#writeMemoModal').modal('hide');
		        		$('#memoPanel').load('memo.jsp');	// 메모리스트 리로드
		        		resetModal(); // 모달 초기화
					}
				});
			}
			//*******************************************************************
			
			//************************메모 삭제************************************
			function deleteMemo(memoNum) {
				var ok = confirm("정말 삭제하시겠습니까?");
				if(ok == true){
					// 메모index를 메모추가수정서블릿에 전달, 유형은 삭제!
					$.post( "../MemoAddEditServlet", { memoNum : memoNum, type : "delete" })
					 .done(function( result ) { 
						 
			        	$('#memoPanel').load('memo.jsp'); // 메모리스트 리로드
			        	
					});	
				}
			}
			//********************************************************************
					
			
			//*********************메모 상태(즐겨찾기, 공유) 변경 함수들*************************
			// 메모를 즐겨찾기에 추가/제거하는 함수
			function favoriteMemo(memoNum) {
				
				// 메모index를 메모상태서블릿에 전달, 유형은 즐겨찾기!
				$.post( "../MemoStatusServlet", { memoNum : memoNum, type : "favorite" })
				 .done(function( result ) { // 즐겨찾기 추가/삭제 여부를 result에 전달받음 
					
					 if(result == "Y") // 즐겨찾기 메모
						$("#fav"+memoNum).attr("class","glyphicon glyphicon-star");
					 else if(result == "N") // 즐겨찾기 아닌 메모
						 $("#fav"+memoNum).attr("class","glyphicon glyphicon-star-empty"); 
					 else if(result == "fail") // 실패
						 alert("데이터 베이스 오류입니다.");
					 
		        	$('#memoPanel').load('memo.jsp'); // 메모리스트 리로드

				});	
			}
			
			// 메모를 공유하는 함수
			function shareMemo(memoNum, myuserId) {
				var userId = prompt("누구에게 공유할까요? 아이디를 입력하세요.","");
				if(userId == "") { // 공유할 대상이 없다.
					alert("아이디를 입력하세요.");
					return;
				}
				else if(userId == myuserId) { // 자신에게 공유할 수 없다.
					alert("자신에게 공유할 수 없습니다.");
					return;
				}
				else if(!userId) // prompt창을 닫을 경우
					return;
				
				// 메모index, 공유대상을 메모상태서블릿에 전달. 유형은 공유하기!
				$.post( "../MemoStatusServlet", { memoNum : memoNum, userId : userId, type : "shareOn" })
				 .done(function( result ) {
					 
					 if(result == 1) { // 공유성공
						alert(userId + "님에게 메모가 공유되었습니다.");
						$('#memoPanel').load('memo.jsp'); // 메모리스트 리로드
				 	 }
					 else if(result == 0) { // 없는 아이디
						alert("존재하지 않는 아이디입니다.");
					 }
					 else if(result == -1) { // 이미 공유한 메모
						 alert("이미 " + userId + "님에게 공유한 메모입니다.");
					 }
					 else if(result == -2) { // 공유해준 사람에게 다시 공유할 경우
						 alert("이 메모는 " + userId + "님의 메모입니다.");
					 }
					 else { // 데이터베이스 오류
						 alert("데이터베이스 오류입니다.");
					 }
				});
			}
				
			// 메모를 공유해제하는 함수
			function killShareMemo(memoNum, userId) {
				var answer = confirm("공유를 해제하시겠습니까?");
				
				if(answer == "false") // confirm창을 닫을 경우
					return;
			
				// 메모index와 공유대상을 메모상태서블릿에 전달. 유형은 공유해제!
				$.post( "../MemoStatusServlet", { memoNum : memoNum, userId : userId, type : "shareOff" })
				 .done(function( result ) {
					 if(result == 1) { // 공유해제 성공!
						alert("공유가 해제되었습니다.");
						$('#memoPanel').load('memo.jsp'); // 메모리스트 리로드
				 	}
					 else { // 데이터베이스 오류
						 alert("데이터베이스 오류입니다.");
					 }
				 });
			}		
			
			// 다른 사람으로부터 공유받은 메모를 즐겨찾기에 추가/제거하는 함수
			function sharedFavMemo(memoNum, userId) {
				
				// 메모index, 공유해준 아이디를 메모상태서블릿에 전달. 유형은 공유즐겨찾기!
				$.post( "../MemoStatusServlet", { memoNum : memoNum, userId : userId, type : "sharedFav" })
				 .done(function( result ) {
					 if(result == "Y") // 즐겨찾기
						$("#fav"+memoNum).attr("class","glyphicon glyphicon-star");
					 else if(result == "N") // 즐겨찾기 안함
						 $("#fav"+memoNum).attr("class","glyphicon glyphicon-star-empty"); 
					 else if(result == "fail") // 오류
						 alert("데이터 베이스 오류입니다.");
					 
					 $('#memoPanel').load('memo.jsp'); // 메모리스트 리로드
				});	
			}
			
			//*******************************************************************
		
		</script>
	</head>
	<body>
		<div class="wrap">
			<!-- header 부분 -->
			<jsp:include page="../inc/top.jsp">
				<jsp:param value="memo" name="active"/>
			</jsp:include>
			<!-- header 부분 -->
	
			<!-- include summernote css/js -->
			<script src="../js/summernote.js"></script>
			<script src="../lang/summernote-ko-KR.js"></script>

			<!-- 메모 리스트 -->
			<div id="memoPanel" class="container">
			</div>
			<!-- 메모 리스트 끝 -->
							
			<!-- 새 메모 쓰기/메모 수정 모달 -->
			<div class="modal fade bs-modal-lg" id="writeMemoModal" tabindex="-1" role="dialog" aria-hidden="true" data-backdrop="static" data-keyboard="false" >
				<div class="modal-dialog bs-modal-lg">
					<div class="modal-content" id = "writeMemo">
						<form id="writeMemoForm" name = "writeMemoForm" class="form-horizontal" enctype="multipart/form-data" method="post">
							<!-- 메모 구분을 위해 메모index를 가진다. -->
							<input type="hidden" id = "writeMemo_memoNum" name = "memoNum">
														
				        	<div class="modal-header">
				        		<div class="controls text-center">
				        			<!-- 메모 색상을 선택한다. 기본값은 default -->
				        			<div id="colorbox">
										<input type="radio" id="default" name="color" value="default" checked>
										<label for="default"><span></span></label>
										<input type="radio" id="White" name="color" value="White">
										<label for="White"><span></span></label>
										<input type="radio" id="Cyan" name="color" value="Cyan">
										<label for="Cyan"><span></span></label>
										<input type="radio" id="Magenta" name="color" value="Magenta">
										<label for="Magenta"><span></span></label>
										<input type="radio" id="Yellow" name="color" value="Yellow">
										<label for="Yellow"><span></span></label>
										<input type="radio" id="OrangeRed" name="color" value="OrangeRed">
										<label for="OrangeRed"><span></span></label>
										<input type="radio" id="Green" name="color" value="Green">
										<label for="Green"><span></span></label>
										<input type="radio" id="DodgerBlue" name="color" value="DodgerBlue">
										<label for="DodgerBlue"><span></span></label>
									</div>
									<!-- 메모 제목 -->
									<input type="text" id="writeMemo_subject" name="subject" class="form-control" placeholder="제목 없음" maxlength="100" class="input-large">
								</div>
							</div>
							<!-- 메모 내용 -->
							<div class="modal-body">
								<div class="controls">
								    <textarea class="summernote" id="writeMemo_content" name=content rows=20 cols=50 class="form-control" ></textarea>
								</div>
							    <script> 
							    
								    // 에디터 설정
								    // 사용할 툴바와 언어, 높이 등							    
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
						    		   		// 드래그 앤 드롭하면 호출됨
						    		   		onImageUpload: function(file) {
						    		   			if(file[0].size >= 10 * 1024 * 1024) {
						    		   				alert("10Mb이상은 업로드할 수 없습니다.");
						    		   				return;
						    		   			}
						    		   			uploadFile(file[0],this);
						    		   		},
							    		},
							      	});
							    
							    	function uploadFile(file,el) {
							    		// 전달할 객체 생성
							    		var form_data = new FormData();
							    		// 파일 객체 추가
							    	    form_data.append('file', file);
							    		// 메모 사진, 파일업로드인지 구분하기 위한 인자 전달
							    	    form_data.append('memoNum',$('#writeMemo_memoNum').val());
							    		
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
								                	$(el).summernote('editor.insertImage', "../upload/"+savefile);
								                }
								                else { // 일반 파일이면 다운로드 링크를 만들어 준다.
								                	$('.summernote').summernote('pasteHTML', 
								                			"<a href='../download.jsp?path=upload&savefile=" + savefile 
								                				+ "&originfile=" + file.name + "' onclick='event.cancelBubble=true;'>"
								                				+ file.name + "</a>" );			// 메모는 div태그에 onclick 콜백함수가 설정되어있다.
								                												// 이 div태그 내부에 a태그를 누를경우 onclick과 a태그가 호출됨
								                												// => a태그를 누를 경우 div태그의 onclick이 동작하지 않도록 설정함.
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
								<div class="control-group" id="file">
								</div>
								<!-- 초기값은 새 메모 등록 버튼 -->
								<div class="control-group">
									<div class="controls text-center">
										<button id = "writeMemo_btn" type="button" class="btn btn-danger" onclick="writeProc('<%=(String)session.getAttribute("userId")%>');">등록</button>
										<button type="button" class="btn btn-default" data-dismiss="modal" onclick="resetModal();")>닫기</button>
									</div>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
			<br>
			<!-- footer 부분 -->
			<jsp:include page="../inc/bottom.jsp"/>
			<!-- footer 부분 -->
		</div>
		<!-- 우측 하단 내비게이션 버튼 -->
		<div class="nav_button">	
			<!-- 이 버튼으로 새 메모를 추가할 수 있다. -->	
			<a data-toggle="modal" href="#writeMemoModal"><img src="../img/new.png"></a><br>
			<!-- 이 버튼으로 페이지 최상단으로 이동할 수 있다. -->
			<a href="#"><img src="../img/up.png"></a>
		</div>
	</body>
</html>