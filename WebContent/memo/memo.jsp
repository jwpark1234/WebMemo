<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.memo.MemoDto"%>
<%@page import="java.util.Vector"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Insert title here</title>

		<script type="text/javascript">
			// 페이지가 다 로드되면 호출되는 이벤트
			$(document).ready(function() {
				// 메모 페이지에서 새로고침 후에도 선택한탭을 유지하기 위한 처리 부분
				// location객체에서 해쉬값(탭id값)을 받아와 해당 탭을 활성화 시킴
			    if (location.hash) {
			        $("a[href='" + location.hash + "']").tab("show");
			    }
				// 탭을 클릭하면 주소값에서 #뒤에 값인 탭id를 얻어와 location객체에 해쉬값을 저장
			    $(document.body).on("click", "a[data-toggle]", function(event) {
			        location.hash = this.getAttribute("href");
			    });
			});
			
			// 메모 페이지에서 새로고침 후에도 선택한탭을 유지하기 위한 처리 부분
			// 뒤로가기 버튼 클릭 시 이벤트 처리
			$(window).on("popstate", function() {
			    var anchor = location.hash || $("a[data-toggle='tab']").first().attr("href");
			    $("a[href='" + anchor + "']").tab("show");
			});
		
		</script>

	</head>
	<body>

		<jsp:useBean id="dao" class="com.memo.MemoDao"/>
		<jsp:useBean id="dto" class="com.memo.MemoDto"/>
		<%
			request.setCharacterEncoding("UTF-8");
		
			String keyWord = "";
			// 만약 검색어가 입력되었다면?
			if(request.getParameter("keyWord") != null) {
				
				// 검색어값 저장
				keyWord = request.getParameter("keyWord");
			}
			
			// 즐겨찾기 리스트, 모든 메모 리스트, 내 메모 리스트, 공유받은 리스트, 공유한 리스트를 다 받아옴
			Vector<MemoDto> myFavList = dao.getFavoriteList((String) session.getAttribute("userId"), keyWord);
			Vector<MemoDto> myTotalList = dao.getTotalList((String) session.getAttribute("userId"), keyWord);
			Vector<MemoDto> myMemoList = dao.getMemoList((String) session.getAttribute("userId"), keyWord);
			Vector<MemoDto> mySharedList = dao.getSharedList((String) session.getAttribute("userId"), keyWord);
			Vector<MemoDto> mySharingList = dao.getSharingList((String) session.getAttribute("userId"), keyWord);
			
			// 리스트들의 사이즈
			int myFavListSize = myFavList.size();
			int myTotalListSize = myTotalList.size();
			int myMemoListSize = myMemoList.size();
			int mySharedListSize = mySharedList.size();
			int mySharingListSize = mySharingList.size();
			
			SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd a hh:mm");
		%>
			<br>
			<!-- 페이지 상단부 -->
			<div class="row">
				<div class="col-sm-8 bs-example">
					<!-- 메모 탭 (즐겨찾기, 모든메모, 내메모, 공유받은, 공유한) -->
					<ul class="nav nav-pills" id="memoTab" role="pilllist" >
						<li >
							<a href="#myFavList" aria-controls="myFavList" role="pill" data-toggle="pill">
								<!-- 즐겨찾기 별 모양 -->
								<span class="glyphicon glyphicon-star"></span> 
								<!-- 리스트 항목 갯수 -->
								<span class="badge"><%=myFavListSize %></span>
							</a>
						</li>
						<li class="active">
							<a href="#myTotalList" aria-controls="myTotalList" role="pill" data-toggle="pill">
								모든 메모&nbsp;&nbsp;
								<span class="badge"><%=myTotalListSize %></span>
							</a>
						</li>
						<li >
							<a href="#myMemoList" aria-controls="myMemoList" role="pill" data-toggle="pill">
								내 메모&nbsp;&nbsp;
								<span class="badge"><%=myMemoListSize %></span>
							</a>
						</li>
						<li >
							<a href="#mySharedList" aria-controls="mySharedList" role="pill" data-toggle="pill">
								공유받은 메모&nbsp;&nbsp;<span class="badge"><%=mySharedListSize%></span>
							</a>
						</li>
						<li >
							<a href="#mySharingList" aria-controls="mySharingList" role="pill" data-toggle="pill">
								공유한 메모&nbsp;&nbsp;<span class="badge"><%=mySharingListSize%></span>
							</a>
						</li>
					</ul>
				</div>

				<!-- 검색창 : 메모관련 내용을 검색할 수 있다. -->
				<div class="col-sm-4">
					<form class="form-inline" action="List.jsp" name="search" method="post">
						<div class="input-group">
							<input class="form-control" type="text" name="keyWord" placeholder ="제목, 내용, 첨부파일, 아이디" size="40" value="<%=keyWord%>">
							<span class="input-group-btn">
								<input class="btn btn-warning" type="submit" value="찾기">
							</span>
						</div>
						<input type="hidden" name="page" value= "0">
					</form>
				</div>
			</div>
			
			<!-- 메모 리스트 부분 : 탭 선택에 따라 하나의 리스트만 출력됨 -->
			<div class="tab-content">
			<br>
			    <!-- 즐겨찾기 메모 리스트 -->
			    <div role="tabpanel" id="myFavList" class="tab-pane fade row">
			        <%
			        	// 메모가 없으면 메모를 추가하라는 메세지나 검색결과가 없다는 메세지 출력
			        	if(myFavListSize == 0) {
			        %>
			        		<div class="text-center col-sm-12">
			        			<div class="noMemo" >
			        				<table class="message">
			        					<tr>
			        						<td>
						        			<%
						        				if(keyWord == "") {
						        			%>	
						        				내 메모 하단의 <span class="glyphicon glyphicon-star-empty"></span> 버튼을 눌러 즐겨찾기 목록에 등록해보세요.
						        			<%
						        				}
						        				else {
						        			%>
						        					검색결과가 없습니다.
						        			<%
						        				}
						        			%>		
				        					</td>
			        					</tr>
			        				</table>	
			        			</div>
			        		</div>
			        <%		
			        	}
			        	for(int i = 0; i < myFavListSize; i++) {
			        %>
			        
			        <div class="col-sm-6 col-md-4">
			            <div class="thumbnail" 

			            <%
			            	//메모의 색상 설정 default가 아니면 색상을 db에 저장된 색상명을 가져옴
			            	if(!myFavList.get(i).getColor().equals("default")) {
			            %>
			            	style="background:<%=myFavList.get(i).getColor() %>;"
			            <%
			            	} 
			            %>
			            
			            >
			            	<div class="caption">
			            		<!-- 메모의 소유자와 접속한 아이디를 비교하여 공유받은 메모인지 판단하여 표시 -->
			            		<div align=right class="memoInfo">
			            			<%=myFavList.get(i).getUserId().equals((String)session.getAttribute("userId")) ? "" : myFavList.get(i).getUserId() + "님이 공유한 메모" %>
			            		</div>
			            		<!-- 실제 사용자들이 메모를 클릭할 수 있는 영역. 메모 제목과 메모 내용으로 구성. -->
								<div class="memoFrame" onclick="editMemo('<%=myFavList.get(i).getMemoNum()%>');">
									<h4><%=myFavList.get(i).getSubject().equals("") ? "(제목없음)" : myFavList.get(i).getSubject()%></h4>						<!-- 목록에서 보이는 이미지는 썸네일 이미지 -->
									<p><%=myFavList.get(i).getContent().equals("") ? "메모를 작성하세요." : myFavList.get(i).getContent().replace("\n", "<br>").replace("/upload/","/upload/thumb_") %></p>
								</div>
	   		 					<br>
	   		 					<!-- 메모 하단 부 버튼, 작성/수정 시간 표시 영역 -->
	   		 					<table width="100%">
	   		 						<tr>
	   		 							<td width="30%">
	   		 								<!-- 메모 소유에 따라 즐겨찾기 함수를 다르게 호출함 -->
	   		 								<!-- 내 메모 : memo테이블에 즐겨찾기 유무를 저장 -->
	   		 								<!-- 공유받은 메모 : share테이블에 즐겨찾기 유무를 저장 -->
									        <%if(myFavList.get(i).getUserId().equals((String)session.getAttribute("userId"))) { %>
									        	<a onclick="favoriteMemo('<%=myFavList.get(i).getMemoNum()%>');">
									        		<span id = "fav<%=myFavList.get(i).getMemoNum() %>" class="glyphicon glyphicon-star<%=myFavList.get(i).getFavoriteYN().equals("N") ? "-empty" : ""%>"></span>
									        	</a>
									        <% } else { %>
									        	<a onclick="sharedFavMemo('<%=myFavList.get(i).getMemoNum()%>','<%=(String)session.getAttribute("userId")%>');">
									        		<span id = "fav<%=myFavList.get(i).getMemoNum() %>" class="glyphicon glyphicon-star<%=dao.getSharedFavMemo(myFavList.get(i).getMemoNum(), (String)session.getAttribute("userId")).equals("N") ? "-empty" : ""%>"></span>
									        	</a>
									        <% } %>
									        <!-- 메모 공유 -->
									        <a onclick="shareMemo('<%=myFavList.get(i).getMemoNum()%>', '<%=(String)session.getAttribute("userId")%>');">
									        	<span id="share<%=myFavList.get(i).getMemoNum() %>" class="glyphicon glyphicon-send"></span>
									        </a>
									        <!-- 메모 삭제 -->
									        <a onclick="deleteMemo('<%=myFavList.get(i).getMemoNum()%>');">
									        	<span class="glyphicon glyphicon-trash"></span>
									        </a>
										</td>
										<!-- 메모 작성/수정 시간 표시 -->
										<td width="70%" align=right class="memoInfo">
											<%=s.format(myFavList.get(i).getRegdate()) %>
										</td>
									</tr>
								</table>
							</div>
						</div>
					</div>  
			        <%
			        	}
			        %>
			    </div>
			    
			    <!-- 내 소유의 모든 메모 리스트 -->
			    <div role="tabpanel" id="myTotalList" class="tab-pane fade in row active">
			        <%			     
			     		// 메모가 없으면 메모를 추가하라는 메세지나 검색결과가 없다는 메세지 출력
			        	if(myTotalListSize == 0) {
				    %>
		        		<div class="text-center col-sm-12">
		        			<div class="noMemo">
		        				<table class="message">
			        				<tr>
			        					<td>
				        				<%
					        				if(keyWord == "") {
					        			%>
					        				우측 하단  <img src="../img/new.png" width="25px" style="display: inline;"> 버튼을 눌러 새 메모를 작성해보세요.
					        			<%
					        				}
					        				else {
					        			%>
					        					검색결과가 없습니다.
					        			<%
					        				}
					        			%>
			        					</td>
			        				</tr>
			        			</table>
		        			</div>
		        		</div>
		        	<%		
		        		}
			        	for(int i = 0; i < myTotalListSize; i++) {
			        %>
			        <div class="col-sm-6 col-md-4">
			            <div class="thumbnail"

			            <%
			            	//메모의 색상 설정 default가 아니면 색상을 db에 저장된 색상명을 가져옴
			            	if(!myTotalList.get(i).getColor().equals("default")) {
			            %>
			            	style="background:<%=myTotalList.get(i).getColor() %>;"
			            <%
			            	} 
			            %>
			            
			            >
							<div class="caption">
								<!-- 메모의 소유자와 접속한 아이디를 비교하여 공유받은 메모인지 판단하여 표시 -->
								<div align=right class="memoInfo">
			            			<%=myTotalList.get(i).getUserId().equals((String)session.getAttribute("userId")) ? "" : myTotalList.get(i).getUserId() + "님이 공유한 메모" %>
			            		</div>
			            		<!-- 실제 사용자들이 메모를 클릭할 수 있는 영역. 메모 제목과 메모 내용으로 구성. -->
								<div class="memoFrame" onclick="editMemo('<%=myTotalList.get(i).getMemoNum()%>');">
									<h4><%=myTotalList.get(i).getSubject().equals("") ? "(제목없음)" : myTotalList.get(i).getSubject()%></h4>
									<p><%=myTotalList.get(i).getContent().equals("") ? "메모를 작성하세요." : myTotalList.get(i).getContent().replace("\n", "<br>").replace("/upload/","/upload/thumb_") %></p>
								</div>
    		 					<br>
    		 					<!-- 메모 하단 부 버튼, 작성/수정 시간 표시 영역 -->
    		 					<table width="100%">
	   		 						<tr>
	   		 							<td width="30%">
	   		 								<!-- 메모 소유에 따라 즐겨찾기 함수를 다르게 호출함 -->
	   		 								<!-- 내 메모 : memo테이블에 즐겨찾기 유무를 저장 -->
	   		 								<!-- 공유받은 메모 : share테이블에 즐겨찾기 유무를 저장 -->
									        <%if(myTotalList.get(i).getUserId().equals((String)session.getAttribute("userId"))) { %>
									        	<a onclick="favoriteMemo('<%=myTotalList.get(i).getMemoNum()%>');">
									        		<span id = "fav<%=myTotalList.get(i).getMemoNum() %>" class="glyphicon glyphicon-star<%=myTotalList.get(i).getFavoriteYN().equals("N") ? "-empty" : ""%>"></span>
									        	</a>
									        <% } else { %>
									        	<a onclick="sharedFavMemo('<%=myTotalList.get(i).getMemoNum()%>','<%=(String)session.getAttribute("userId")%>');">
									        		<span id = "fav<%=myTotalList.get(i).getMemoNum() %>" class="glyphicon glyphicon-star<%=dao.getSharedFavMemo(myTotalList.get(i).getMemoNum(), (String)session.getAttribute("userId")).equals("N") ? "-empty" : ""%>"></span>
									        	</a>
									        <% } %>
									        <!-- 메모 공유 -->
									        <a onclick="shareMemo('<%=myTotalList.get(i).getMemoNum()%>', '<%=(String)session.getAttribute("userId")%>');">
									        	<span id="share<%=myTotalList.get(i).getMemoNum() %>" class="glyphicon glyphicon-send"></span>
									        </a>
									       	<!-- 메모 삭제 -->
									       	<a onclick="deleteMemo('<%=myTotalList.get(i).getMemoNum()%>');">
									       		<span class="glyphicon glyphicon-trash"></span>
									       	</a>
								   		</td>
								   		<!-- 메모 작성/수정 시간 표시 -->
								   		<td width="70%" align=right class="memoInfo">
											<%=s.format(myTotalList.get(i).getRegdate()) %>
										</td>
									</tr>
								</table>
							</div>
						</div>
					</div>  
			        <%
			        	}
			        %>
			    </div>
			    
			    <!-- 내 메모 리스트 -->
			    <div role="tabpanel" id="myMemoList" class="tab-pane fade row">
			        <%
			     		// 메모가 없으면 메모를 추가하라는 메세지나 검색결과가 없다는 메세지 출력
			        	if(myMemoListSize == 0) {
				    %>
		        		<div class="text-center col-sm-12">
		        			<div class="noMemo">
		        				<table class="message">
			        				<tr>
			        					<td>
					        			<%
					        				if(keyWord == "") {
					        			%>
					        					우측 하단  <img src="../img/new.png" width="25px" style="display: inline;"> 버튼을 눌러 새 메모를 작성해보세요.	
					        			<%
					        				}
					        				else {
					        			%>
					        					검색결과가 없습니다.
					        			<%
					        				}
					        			%>
			        					</td>
			        				</tr>
			        			</table>
		        			</div>
		        		</div>
		        	<%		
		        		}
			        	for(int i = 0; i < myMemoListSize; i++) {
			        %>
			        <div class="col-sm-6 col-md-4">
			            <div class="thumbnail"
			            	
			            <%
			            	//메모의 색상 설정 default가 아니면 색상을 db에 저장된 색상명을 가져옴
			            	if(!myMemoList.get(i).getColor().equals("default")) {
			            %>
			            	style="background:<%=myMemoList.get(i).getColor() %>;"
			            <%
			            	} 
			            %>
			            
			            >
							<div class="caption">
								<!-- 실제 사용자들이 메모를 클릭할 수 있는 영역. 메모 제목과 메모 내용으로 구성. -->
								<div class="memoFrame" onclick="editMemo('<%=myMemoList.get(i).getMemoNum()%>');">
									<h4><%=myMemoList.get(i).getSubject().equals("") ? "(제목없음)" : myMemoList.get(i).getSubject()%></h4>
									<p><%=myMemoList.get(i).getContent().equals("") ? "메모를 작성하세요." : myMemoList.get(i).getContent().replace("\n", "<br>").replace("/upload/","/upload/thumb_") %></p>
								</div>
    		 					<br>
    		 					<!-- 메모 하단 부 버튼, 작성/수정 시간 표시 영역 -->
	   		 					<table width="100%">
	   		 						<tr>
	   		 							<td width="30%">
	   		 								<!-- 내 메모를 즐겨찾기 추가/해제 -->
									        <a onclick="favoriteMemo('<%=myMemoList.get(i).getMemoNum()%>');">
									        	<span id = "fav<%=myMemoList.get(i).getMemoNum() %>" class="glyphicon glyphicon-star<%=myMemoList.get(i).getFavoriteYN().equals("N") ? "-empty" : ""%>"></span>
									        </a>
									        <!-- 메모 공유 -->
									        <a onclick="shareMemo('<%=myMemoList.get(i).getMemoNum()%>', '<%=(String)session.getAttribute("userId")%>');">
									        	<span id="share<%=myMemoList.get(i).getMemoNum() %>" class="glyphicon glyphicon-send"></span>
									        </a>
									        <!-- 메모 삭제 -->
									        <a onclick="deleteMemo('<%=myMemoList.get(i).getMemoNum()%>');">
									        	<span class="glyphicon glyphicon-trash"></span>
									        </a>
										</td>
										<!-- 메모 작성/수정 시간 표시 -->
										<td width="70%" align=right class="memoInfo">
											<%=s.format(myMemoList.get(i).getRegdate()) %>
										</td>
									</tr>
								</table>
							</div>
						</div>
					</div>  
			        <%
			        	}
			        %>
			    </div>
			    			    
			    <!-- 공유받은 메모 리스트 -->
			    <div role="tabpanel" id="mySharedList" class="tab-pane fade row">
			        <%
			     		// 메모가 없으면 메모를 추가하라는 메세지나 검색결과가 없다는 메세지 출력
			        	if(mySharedListSize == 0) {
				    %>
		        		<div class="text-center col-sm-12">
		        			<div class="noMemo">
		        				<table class="message">
			        				<tr>
			        					<td>
					        			<%
					        				if(keyWord == "") {
					        			%>
					        				다른 회원으로부터 공유받은 메모가 없습니다.
					        			<%
					        				}
					        				else {
					        			%>
					        					검색결과가 없습니다.
					        			<%
					        				}
					        			%>
			        					</td>
			        				</tr>
			        			</table>
		        			</div>
		        		</div>
		        	<%		
		        		}
			        	for(int i = 0; i < mySharedListSize; i++) {
			        %>
			        <div class="col-sm-6 col-md-4">
			            <div class="thumbnail"
			            				            	
			            <%
			            	//메모의 색상 설정 default가 아니면 색상을 db에 저장된 색상명을 가져옴
			            	if(!mySharedList.get(i).getColor().equals("default")) {
			            %>
			            	style="background:<%=mySharedList.get(i).getColor() %>;"
			            <%
			            	} 
			            %>
			            
			            >
							<div class="caption">
								<!-- 메모의 소유자를 표시 -->
								<div align=right class="memoInfo">
			            			<%=mySharedList.get(i).getUserId().equals((String)session.getAttribute("userId")) ? "" : mySharedList.get(i).getUserId() + "님이 공유한 메모" %>
			            		</div>
			            		<!-- 실제 사용자들이 메모를 클릭할 수 있는 영역. 메모 제목과 메모 내용으로 구성. -->
								<div class="memoFrame" onclick="editMemo('<%=mySharedList.get(i).getMemoNum()%>');">
									<h4><%=mySharedList.get(i).getSubject().equals("") ? "(제목없음)" : mySharedList.get(i).getSubject()%></h4>
									<p><%=mySharedList.get(i).getContent().equals("") ? "메모를 작성하세요." : mySharedList.get(i).getContent().replace("\n", "<br>").replace("/upload/","/upload/thumb_") %></p>
								</div>
	   		 					<br>
	   		 					<!-- 메모 하단 부 버튼, 작성/수정 시간 표시 영역 -->
	   		 					<table width="100%">
	   		 						<tr>
	   		 							<td width="30%">
	   		 								<!-- 공유받은 메모를 즐겨찾기 추가/해제 -->
									    	<a onclick="sharedFavMemo('<%=mySharedList.get(i).getMemoNum()%>','<%=(String)session.getAttribute("userId")%>');">
									    		<span id = "fav<%=mySharedList.get(i).getMemoNum() %>" class="glyphicon glyphicon-star<%=dao.getSharedFavMemo(mySharedList.get(i).getMemoNum(), (String)session.getAttribute("userId")).equals("N") ? "-empty" : ""%>"></span>
									    	</a>
									        <!-- 메모 공유 -->
									        <a onclick="shareMemo('<%=mySharedList.get(i).getMemoNum()%>', '<%=(String)session.getAttribute("userId")%>');">
									        	<span id="share<%=mySharedList.get(i).getMemoNum() %>, <%=(String)session.getAttribute("userId")%>" class="glyphicon glyphicon-send"></span>
									       	</a>
									    	<!-- 메모 삭제 -->
									    	<a onclick="deleteMemo('<%=mySharedList.get(i).getMemoNum()%>');">
									    		<span class="glyphicon glyphicon-trash"></span>
									    	</a>
										</td>
										<!-- 메모 작성/수정 시간 표시 -->
										<td width="70%" align=right class="memoInfo">
											<%=s.format(mySharedList.get(i).getRegdate()) %>
										</td>
									</tr>
								</table>
							</div>
						</div>
					</div>  
			        <%
			        	}
			        %>
			    </div>
			    
			    <!-- 공유한 메모 리스트 -->
			    <div role="tabpanel" id="mySharingList" class="tab-pane fade row">
			        <%			
			 		    // 메모가 없으면 메모를 추가하라는 메세지나 검색결과가 없다는 메세지 출력
			        	if(mySharingListSize == 0) {
				    %>
		        		<div class="text-center col-sm-12">
		        			<div class="noMemo">
		        				<table class="message">
			        				<tr>
			        					<td>
					        			<%
					        				if(keyWord == "") {
					        			%>
					        				내 메모 하단의 <span class="glyphicon glyphicon-send"></span> 버튼을 눌러 내 메모를 다른 회원에게 공유해보세요.
					        			<%
					        				}
					        				else {
					        			%>
					        					검색결과가 없습니다.
					        			<%
					        				}
					        			%>
			        					</td>
			        				</tr>
			        			</table>
		        			</div>
		        		</div>
		        	<%		
		        		}
			        	for(int i = 0; i < mySharingListSize; i++) {
			        %>
			        <div class="col-sm-6 col-md-4">
			            <div class="thumbnail"
			            				            	
			            <%
			            	//메모의 색상 설정 default가 아니면 색상을 db에 저장된 색상명을 가져옴
			            	if(!mySharingList.get(i).getColor().equals("default")) {
			            %>
			            	style="background:<%=mySharingList.get(i).getColor() %>;"
			            <%
			            	} 
			            %>
			            
			            >
							<div class="caption">
								<!-- 내 메모를 공유받은 아이디 표시 -->
								<div align=right class="memoInfo">
									<%=mySharingList.get(i).getUserId() + "님에게 공유한 메모"%> 
								</div>
								<!-- 실제 사용자들이 메모를 클릭할 수 있는 영역. 메모 제목과 메모 내용으로 구성. -->
								<div class="memoFrame" onclick="editMemo('<%=mySharingList.get(i).getMemoNum()%>');">
									<h4><%=mySharingList.get(i).getSubject().equals("") ? "(제목없음)" : mySharingList.get(i).getSubject()%></h4>
									<p><%=mySharingList.get(i).getContent().equals("") ? "메모를 작성하세요." : mySharingList.get(i).getContent().replace("\n", "<br>").replace("/upload/","/upload/thumb_") %></p>
								</div>
	   		 					<br>
	   		 					<!-- 메모 하단 부 버튼, 작성/수정 시간 표시 영역 -->
	   		 					<table width="100%">
	   		 						<tr>
	   		 							<td width="30%">
	   		 								<!-- 내 메모를 즐겨찾기 추가/해제 -->
									        <a onclick="favoriteMemo('<%=mySharingList.get(i).getMemoNum()%>');">
									        	<span id = "fav<%=mySharingList.get(i).getMemoNum() %>" class="glyphicon glyphicon-star<%=mySharingList.get(i).getFavoriteYN().equals("N") ? "-empty" : ""%>"></span>
									        </a>
									        <!-- 메모 공유 -->
									        <a onclick="shareMemo('<%=mySharingList.get(i).getMemoNum()%>', '<%=(String)session.getAttribute("userId")%>');">
									        	<span id="share<%=mySharingList.get(i).getMemoNum() %>" class="glyphicon glyphicon-send"></span>
									        </a>
									    	<!-- 메모 삭제 -->
									    	<a onclick="deleteMemo('<%=mySharingList.get(i).getMemoNum()%>');">
									    		<span class="glyphicon glyphicon-trash"></span>
									    	</a>
										</td>
										<!-- 메모 작성/수정 시간 표시 -->
										<td width="70%" align=right class="memoInfo">
											<%=s.format(mySharingList.get(i).getRegdate()) %>
										</td>
									</tr>
									<tr>
										<!-- 내 메모를 공유해제 시키는 버튼 -->
										<td colspan=2 align=center>
											<button type="button" class="btn btn-danger" onclick="killShareMemo('<%=mySharingList.get(i).getMemoNum()%>', '<%=mySharingList.get(i).getUserId()%>');">공유해제</button>
										</td>
									</tr>
								</table>
							</div>
						</div>
					</div>  
			        <%
			        	}
			        %>
			    </div>
		    </div>
		<!-- 메모 리스트 끝 -->
	</body>
</html>