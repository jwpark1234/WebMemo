package myservlet;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiUtils;
import com.memo.MemoDao;
import com.oreilly.servlet.MultipartRequest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/FileUploadServlet")
public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=UTF-8");

		// 업로드할 경로
		String realpath = getServletContext().getRealPath("upload");
		int max = 10 * 1024 * 1024; // 용량 제한은 10MB
		
		//폴더가 없을 경우 폴더 생성
        //파일 객체 생성
        File file = new File(realpath);
        //파일이 존재하지 않는 경우
        if(!file.exists()){
            //디렉토리 생성 메서드
            file.mkdirs();
        }
		
		MultipartRequest multi = new MultipartRequest(req,realpath,max,"UTF-8", new DefaultFileRenamePolicy()); 
		
		Enumeration e = multi.getFileNames();
		String tagname = (String) e.nextElement();	
		String saveFile = multi.getFilesystemName(tagname);
       
		//메모에 파일이나 사진을 업로드한 경우
        //썸네일을 만든다!!
        if(multi.getParameter("memoNum") != null) {
        	
        	
	        // 썸네일 작업
	        String[] filename = saveFile.split("\\.");
	        String extension = filename[1].toLowerCase();

	        // 사진파일이면 섬네일 만들기. gif 제외
	        if(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("bmp") || extension.equals("png") ) {
	        	
		        int thumbWidth = 300;//썸네일 가로
		        int thumbHeight = 300;//썸네일 세로
		 
		        Image thumbnail = JimiUtils.getThumbnail(realpath + "\\" + saveFile, thumbWidth, thumbHeight, Jimi.IN_MEMORY);// 썸네일 설정
		        try {
					Jimi.putImage(thumbnail, realpath + "\\thumb_" + saveFile);
		        } catch (JimiException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}// 썸네일 생성 
	        }
        }
		
		// 파일명을 돌려준다.
        resp.getWriter().write(saveFile);        
	}
}
