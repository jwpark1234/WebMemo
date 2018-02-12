package com.memo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

// DB�۾��ϴ� �ڹٺ�Ŭ������ ���� �� �ϳ�!!
public class BoardDao {

	// JSP <-> DB ���� �������Ʈ
	// http://all-record.tistory.com/104	
	
	//DB�۾� ���ѻ� ���� ����
	private Connection con;
	private PreparedStatement pstmt;
	private ResultSet rs;
	// Ŀ�ؼ�Ǯ�� ���� ���� ����
	private DataSource ds;
	
	// Ŀ�ؼ�Ǯ ��� �۾�
	public BoardDao() {
		// TODO Auto-generated constructor stub
	
		try {
			// 1. Was������ ����� BoardApp��������Ʈ�� ��� ������ ������ �ִ� ���ؽ�Ʈ ��ü ����
			Context init = new InitialContext();
			// 2. ����� Was�������� DataSource(Ŀ�ؼ� Ǯ) �˻��ؼ� ��������
			ds = (DataSource) init.lookup("java:comp/env/jdbc/memo");
			
		} catch (Exception e) {
			System.out.println("BoardDao()�����ڿ��� Ŀ�ؼ�Ǯ ��� ���� : " + e);
		}
	}
	
	// Ŀ�ؼ�Ǯ���� ����� Ŀ�ؼ� ��ü�� �ݳ�
	public void freeResource() {
		if(con != null) try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
		if(pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
		if(rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
	}
	
	public void insertBoard(BoardDto dto) {
		// TODO Auto-generated method stub
		
		// 1. �ԷµǴ� �����ʹ� ������ pos�� depth�� 0,0�̴�.
		// 2. �̹� �ԷµǾ� �ִ� �����ʹ� pos�� 1�����ȴ�. => ��������� �������� pos�� ���� ũ�� ���� �ֽ� �������� pos�� ���� �۴�.
		// 3. ��ü���� �Խ��Ĵ��� ������ �۹�ȣ�� �ƴ϶� pos�� �������� �������� ����
		
		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();
			
			String sql = "update Board set pos = pos + 1";
			pstmt = con.prepareStatement(sql);
			pstmt.executeUpdate();
			
			// dto�� �ִ� �����͸� DB�� ����
			sql = "insert into Board(name,email,subject,content,pw,count,regdate,pos,depth) values (?,?,?,?,?,0,now(),0,0)";
			
			//Connection��ü�� ���� ����! insert������ DB�� ������!
			//PreparedStatement��ü�� ���� ���ִµ�
			//�� PreparedStatement��ü�� ��� �ö���????�� ������ ������ insert������
			//PreparedStatement��ü�� �����Ͽ�!!
			//PreparedStatement��ü ������ ���Ϲ޾ƿ´�.
			//�Ʒ��� �޼ҵ忡��!!
			pstmt = con.prepareStatement(sql); //insert������ ������ PreparedStatement��ü
			pstmt.setString(1, dto.getName());
			pstmt.setString(2, dto.getEmail());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
			pstmt.setString(5, dto.getPw());
			
			pstmt.executeUpdate();

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("BoardDaoŬ������ insertBoard() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}
	}

	public void getCount(int num) {
		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();

			// ��ȸ���� ������Ų��!!	
			String sql = "update Board set count = count + 1 where num = " + num;
			pstmt = con.prepareStatement(sql);
			pstmt.executeUpdate();	
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("BoardDaoŬ������ getCount() �޼����� ���� : " + e);
		}finally {
			freeResource();
		}
	}
	
	// �۹�ȣ�� ���޹޾� �� �۹�ȣ�� �ش��ϴ� �������� �˻��ϱ� ���� �޼ҵ�
	public BoardDto getBoard(int no, boolean count) {
		// TODO Auto-generated method stub
		
		BoardDto dto = null;
		
		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();
			
			String sql = "";
			// ��ȸ���� ������Ų��!!
			if(count) {
				sql = "update Board set count = count + 1 where num = " + no;
				pstmt = con.prepareStatement(sql);
				pstmt.executeUpdate();	
			}
			//��ȸ�� ���� �޼��带 �����
			//getCount(num);
			
			// no���� �Խñ��� �޾ƿ´�.
			sql = "select * from Board where num = " + no;
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			rs.next();
			dto = new BoardDto();
			dto.setNum(rs.getInt("num"));
			dto.setName(rs.getString("name"));
			dto.setEmail(rs.getString("email"));
			dto.setSubject(rs.getString("subject"));
			dto.setContent(rs.getString("content"));
			dto.setPw(rs.getString("pw"));
			dto.setCount(rs.getInt("count"));
			dto.setRegdate(rs.getTimestamp("regdate"));
			dto.setPos(rs.getInt("pos"));
			dto.setDepth(rs.getInt("depth"));

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("BoardDaoŬ������ getBoard() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}
		return dto;
	}

	
	public void updateBoard(BoardDto dto) {
		// TODO Auto-generated method stub

		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();
			
			// dto�� �ִ� �����͸� DB�� ����
			String sql = "update Board set name =?, email =?, subject =?, content =? where num = ?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, dto.getName());
			pstmt.setString(2, dto.getEmail());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
			pstmt.setInt(5, dto.getNum());
			
			pstmt.executeUpdate();

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("BoardDaoŬ������ updateBoard() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}
		
		
	}

	
	public void deleteBoard(int no) {
		// TODO Auto-generated method stub
		
		try {
			con = ds.getConnection();
			
			// no���� �Խñ� ����
			String sql = "delete from Board where num = " + no;
			
			pstmt = con.prepareStatement(sql);
			pstmt.executeUpdate();

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("BoardDaoŬ������ deleteBoard() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}
	}

	
	public void replyBoard(BoardDto dto) {
		// TODO Auto-generated method stub
		
		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();
			
			// 1. �θ� ���� pos ���� ū ���� pos�� 1�� �������Ѿ� �Ѵ�.
			String sql = "update Board set pos = pos+1 where pos > (select * from (select pos from Board b where num = ? ) as b)";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, dto.getNum()); // ���� ���� num�� �θ��� num
			pstmt.executeUpdate();

			// dto�� �ִ� �����͸� DB�� ����
			sql = "insert into Board(name,email,subject,content,pw,count,regdate,pos,depth) values (?,?,?,?,?,0,now(),?,?)";
			
			//Connection��ü�� ���� ����! insert������ DB�� ������!
			//PreparedStatement��ü�� ���� ���ִµ�
			//�� PreparedStatement��ü�� ��� �ö���????�� ������ ������ insert������
			//PreparedStatement��ü�� �����Ͽ�!!
			//PreparedStatement��ü ������ ���Ϲ޾ƿ´�.
			//�Ʒ��� �޼ҵ忡��!!
			pstmt = con.prepareStatement(sql); //insert������ ������ PreparedStatement��ü
			pstmt.setString(1, dto.getName());
			pstmt.setString(2, dto.getEmail());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
			pstmt.setString(5, dto.getPw());
			pstmt.setInt(6, dto.getPos()+1); 	// �θ��� pos + 1
			pstmt.setInt(7, dto.getDepth()+1); 	// �θ��� depth + 1
			
			pstmt.executeUpdate();

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("BoardDaoŬ������ replyBoard() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}
		
	}
	
	// DB�� �ִ� �۵��� select�ؼ� �����ͼ� ��ü �۸�� ����Ʈ���� �Խ��ǿ� �ѷ��ֱ� ���� �޼���
	// List.jsp ���������� ����ϴ� �޼���
							//�˻����ذ�		�˻���
	public Vector<BoardDto> getBoardList(String keyField, String keyWord) {
		// TODO Auto-generated method stub

		// �˻������ DTO��ü�� ��� �� DTO��ü�� ArrayList�� ��´�.
		Vector<BoardDto> vec = new Vector<BoardDto>(); 
		
		String sql = "";
		
		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();
			
			// �˻�� �Է����� �ʾҴٸ�?
			if(keyWord == null || keyWord.isEmpty()) {
				// ���� �ֽű��� ���� �ö���� pos�ʵ尪�� �������� �Ͽ� �������� �����Ͽ� �۰˻�
				sql = "select * from Board order by pos";
				//sql = "select * from tblBoard order by num desc";
			}
			else { // �˻�� �Է��ߴٸ�
				sql = "select * from Board where " + keyField + " like '%" + keyWord + "%' order by pos";
				//sql = "select * from tblBoard where " + keyField + " like '%" + keyWord + "%' order by num desc";
			}
			
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				BoardDto dto = new BoardDto();
				dto.setNum(rs.getInt("num"));
				dto.setName(rs.getString("name"));
				dto.setEmail(rs.getString("email"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setPw(rs.getString("pw"));
				dto.setCount((rs.getInt("count")));
				dto.setRegdate(rs.getTimestamp("regdate"));
				dto.setPos(rs.getInt("pos"));
				dto.setDepth(rs.getInt("depth"));
				
				vec.add(dto);
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("BoardDaoŬ������ getBoardList() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}
		return vec;
		
	}
	
	//�鿩����
	public String useDepth(int depth) {
		String result = "";

		for(int i = 0; i < 3*(depth-1); i++)
			result += "&nbsp;";
		if(depth > 0)
			result += "<img src='../img/re.gif'>";

		return result;
	}

	public void initiateNum() {
		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();
			
			String sql = "alter table Board auto_increment = 1";
			
			pstmt = con.prepareStatement(sql);
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("BoardDaoŬ������ initiateNum() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}
	}
	
}
