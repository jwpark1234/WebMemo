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
	</head>
	<body>
		<div class="wrap">
			<!-- header 부분 -->
			<jsp:include page="../inc/top.jsp">
				<jsp:param value="none" name="active"/>
			</jsp:include>
			<!-- header 부분 -->
					
			<% request.setCharacterEncoding("UTF-8"); %>
		
			<div class="container">
				<div class="jumbotron">
					<div class="mainImg"></div> <!-- 점보트론 이미지영역 -->
					<div class="container"> <!-- 점보트론 글자 영역 -->
						<div class="row">
	            			<div class="col-lg-12">
	            				<br><br><br><br><br><br>
	            				
								<h1 class="text-center"><b>WebMemo</b></h1>
								<p class="text-center"><b>텍스트, 사진, 파일을 메모하고 친구들과 공유해보세요!</b></p>
							</div>
						</div>
					</div>
				</div>
				<!-- 메인페이지 부가 설명 부분 -->
				<div class="row">
					<div class="col-sm-4">
						<h3 class="text-center"><b>메모</b></h3>
						<p class="text-center">텍스트, 사진, 파일을 메모하세요. 텍스트는 최대 500자, 사진과 파일은 최대 10Mb까지 업로드 할 수 있습니다.</p>
					</div>
					<div class="col-sm-4">
						<h3 class="text-center"><b>공유</b></h3>
						<p class="text-center">친구에게 나의 메모를 공유해보세요. 공유받은 메모는 다른 친구에게 또 공유할 수 있습니다.</p>
					</div>
					<div class="col-sm-4">
						<h3 class="text-center"><b>즐겨찾기</b></h3>
						<p class="text-center">자주 접하는 메모장은 즐겨찾기 목록에 추가하세요. 공유받은 메모도 즐겨찾기할 수 있습니다.</p>
					</div>
				</div>	
			</div>
			<br><br><br>
			<!-- footer 부분 -->
			<jsp:include page="../inc/bottom.jsp"/>
			<!-- footer 부분 -->
		</div>
	</body>
</html>