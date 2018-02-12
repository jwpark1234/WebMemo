<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.File"%>
<%@ page contentType="text/html;charset=UTF-8" %>
    
<%
	request.setCharacterEncoding("UTF-8");
	String path = request.getParameter("path"); // 업로드된 파일의 가상경로 주소(다운로드할 장소)
	String savefile = request.getParameter("savefile");	// 업로드된 파일의 실제 이름
	String originfile = request.getParameter("originfile");	// 업로드할때의 파일 이름

	String realPath = getServletContext().getRealPath("/" + path);
	// 	D:\workspace_jsp\.metadata\.plugins\org.eclipse.wst.server.core\tmp1\wtpwebapps\FileUpload/upload
	String sFilePath = realPath + "\\" + savefile;
	
	byte b[] = new byte[4096];
	File oFile = new File(sFilePath);
	
	FileInputStream in = new FileInputStream(sFilePath); // 업로드했던 파일을 읽어옴
	
	String sMimeType = getServletContext().getMimeType(sFilePath);
	
	// octet-stream은 8비트로 된 일련의 데이터를 뜻한다. 지정되지 않은 파일 형식을 의미함.
	if(sMimeType == null) sMimeType = "application/octet-stream";
	response.setContentType(sMimeType);
	
	// 한글 업로드(이 부분이 한글 파일명이 깨지는 것을 방지해줌)
	String sEncoding = new String(originfile.getBytes("UTF-8"),"8859_1");
	// 이 부분이 모든 파일 링크를 클릭했을 때 다운로드 화면이 출력되게 처리하는 부분
	response.setHeader("Content-Disposition", "attachment;filename=" + sEncoding);
	
	ServletOutputStream out2 = response.getOutputStream();
	int numRead;
	
	// 이미 OutputStream 객체를 OutputStream 초기화
	out.clear();
	pageContext.pushBody();
	
	// 바이트 배열 b의 0번부터 numRead번까지 브라우저로 출력
	while((numRead = in.read(b,0,b.length))!= -1) {
		out2.write(b,0,numRead);
	}
	out2.flush();
	out2.close();
	in.close();
%>