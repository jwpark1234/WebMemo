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

		// ���ε��� ���
		String realpath = getServletContext().getRealPath("upload");
		int max = 10 * 1024 * 1024; // �뷮 ������ 10MB
		
		//������ ���� ��� ���� ����
        //���� ��ü ����
        File file = new File(realpath);
        //������ �������� �ʴ� ���
        if(!file.exists()){
            //���丮 ���� �޼���
            file.mkdirs();
        }
		
		MultipartRequest multi = new MultipartRequest(req,realpath,max,"UTF-8", new DefaultFileRenamePolicy()); 
		
		Enumeration e = multi.getFileNames();
		String tagname = (String) e.nextElement();	
		String saveFile = multi.getFilesystemName(tagname);
       
		//�޸� �����̳� ������ ���ε��� ���
        //������� �����!!
        if(multi.getParameter("memoNum") != null) {
        	
        	
	        // ����� �۾�
	        String[] filename = saveFile.split("\\.");
	        String extension = filename[1].toLowerCase();

	        // ���������̸� ������ �����. gif ����
	        if(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("bmp") || extension.equals("png") ) {
	        	
		        int thumbWidth = 300;//����� ����
		        int thumbHeight = 300;//����� ����
		 
		        Image thumbnail = JimiUtils.getThumbnail(realpath + "\\" + saveFile, thumbWidth, thumbHeight, Jimi.IN_MEMORY);// ����� ����
		        try {
					Jimi.putImage(thumbnail, realpath + "\\thumb_" + saveFile);
		        } catch (JimiException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}// ����� ���� 
	        }
        }
		
		// ���ϸ��� �����ش�.
        resp.getWriter().write(saveFile);        
	}
}
