package com.memo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

// DB작업하는 자바빈클래스의 종류 중 하나!!
public class BoardDao {

	// JSP <-> DB 연동 참고사이트
	// http://all-record.tistory.com/104	
	
	//DB작업 삼총사 변수 선언
	private Connection con;
	private PreparedStatement pstmt;
	private ResultSet rs;
	// 커넥션풀을 담을 변수 선언
	private DataSource ds;
	
	// 커넥션풀 얻는 작업
	public BoardDao() {
		// TODO Auto-generated constructor stub
	
		try {
			// 1. Was서버와 연결된 BoardApp웹프로젝트의 모든 정보를 가지고 있는 컨텍스트 객체 생성
			Context init = new InitialContext();
			// 2. 연결된 Was서버에서 DataSource(커넥션 풀) 검색해서 가져오기
			ds = (DataSource) init.lookup("java:comp/env/jdbc/memo");
			
		} catch (Exception e) {
			System.out.println("BoardDao()생성자에서 커넥션풀 얻기 실패 : " + e);
		}
	}
	
	// 커넥션풀에서 사용한 커넥션 객체를 반납
	public void freeResource() {
		if(con != null) try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
		if(pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
		if(rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
	}
	
	public void insertBoard(BoardDto dto) {
		// TODO Auto-generated method stub
		
		// 1. 입력되는 데이터는 무조건 pos와 depth가 0,0이다.
		// 2. 이미 입력되어 있는 데이터늬 pos는 1증가된다. => 가장오래된 데이터의 pos가 가장 크고 가장 최신 데이터의 pos는 가장 작다.
		// 3. 전체적인 게시파느이 정렬은 글번호가 아니라 pos를 기준으로 오름차순 정렬
		
		try {
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();
			
			String sql = "update Board set pos = pos + 1";
			pstmt = con.prepareStatement(sql);
			pstmt.executeUpdate();
			
			// dto에 있는 데이터를 DB에 저장
			sql = "insert into Board(name,email,subject,content,pw,count,regdate,pos,depth) values (?,?,?,?,?,0,now(),0,0)";
			
			//Connection객체의 힘을 빌려! insert구문을 DB에 실행할!
			//PreparedStatement객체를 얻을 수있는데
			//이 PreparedStatement객체를 얻어 올때는????를 제외한 나머지 insert구문을
			//PreparedStatement객체에 저장하여!!
			//PreparedStatement객체 자제를 리턴받아온다.
			//아래의 메소드에서!!
			pstmt = con.prepareStatement(sql); //insert구문을 실행할 PreparedStatement객체
			pstmt.setString(1, dto.getName());
			pstmt.setString(2, dto.getEmail());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
			pstmt.setString(5, dto.getPw());
			
			pstmt.executeUpdate();

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("BoardDao클래스의 insertBoard() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}
	}

	public void getCount(int num) {
		try {
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();

			// 조회수를 증가시킨다!!	
			String sql = "update Board set count = count + 1 where num = " + num;
			pstmt = con.prepareStatement(sql);
			pstmt.executeUpdate();	
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("BoardDao클래스의 getCount() 메서드의 오류 : " + e);
		}finally {
			freeResource();
		}
	}
	
	// 글번호를 전달받아 그 글번호에 해당하는 글정보를 검색하기 위한 메소드
	public BoardDto getBoard(int no, boolean count) {
		// TODO Auto-generated method stub
		
		BoardDto dto = null;
		
		try {
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();
			
			String sql = "";
			// 조회수를 증가시킨다!!
			if(count) {
				sql = "update Board set count = count + 1 where num = " + no;
				pstmt = con.prepareStatement(sql);
				pstmt.executeUpdate();	
			}
			//조회수 증가 메서드를 만든다
			//getCount(num);
			
			// no값인 게시글을 받아온다.
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
			System.out.println("BoardDao클래스의 getBoard() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}
		return dto;
	}

	
	public void updateBoard(BoardDto dto) {
		// TODO Auto-generated method stub

		try {
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();
			
			// dto에 있는 데이터를 DB에 수정
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
			System.out.println("BoardDao클래스의 updateBoard() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}
		
		
	}

	
	public void deleteBoard(int no) {
		// TODO Auto-generated method stub
		
		try {
			con = ds.getConnection();
			
			// no값인 게시글 삭제
			String sql = "delete from Board where num = " + no;
			
			pstmt = con.prepareStatement(sql);
			pstmt.executeUpdate();

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("BoardDao클래스의 deleteBoard() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}
	}

	
	public void replyBoard(BoardDto dto) {
		// TODO Auto-generated method stub
		
		try {
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();
			
			// 1. 부모 글의 pos 보다 큰 글은 pos를 1씪 증가시켜야 한다.
			String sql = "update Board set pos = pos+1 where pos > (select * from (select pos from Board b where num = ? ) as b)";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, dto.getNum()); // 전달 받은 num은 부모의 num
			pstmt.executeUpdate();

			// dto에 있는 데이터를 DB에 저장
			sql = "insert into Board(name,email,subject,content,pw,count,regdate,pos,depth) values (?,?,?,?,?,0,now(),?,?)";
			
			//Connection객체의 힘을 빌려! insert구문을 DB에 실행할!
			//PreparedStatement객체를 얻을 수있는데
			//이 PreparedStatement객체를 얻어 올때는????를 제외한 나머지 insert구문을
			//PreparedStatement객체에 저장하여!!
			//PreparedStatement객체 자제를 리턴받아온다.
			//아래의 메소드에서!!
			pstmt = con.prepareStatement(sql); //insert구문을 실행할 PreparedStatement객체
			pstmt.setString(1, dto.getName());
			pstmt.setString(2, dto.getEmail());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
			pstmt.setString(5, dto.getPw());
			pstmt.setInt(6, dto.getPos()+1); 	// 부모의 pos + 1
			pstmt.setInt(7, dto.getDepth()+1); 	// 부모의 depth + 1
			
			pstmt.executeUpdate();

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("BoardDao클래스의 replyBoard() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}
		
	}
	
	// DB에 있는 글들을 select해서 가져와서 전체 글목록 리스트들을 게시판에 뿌려주기 위한 메서드
	// List.jsp 페이지에서 사용하는 메서드
							//검색기준값		검색어
	public Vector<BoardDto> getBoardList(String keyField, String keyWord) {
		// TODO Auto-generated method stub

		// 검색결과를 DTO객체에 담고 이 DTO객체를 ArrayList에 담는다.
		Vector<BoardDto> vec = new Vector<BoardDto>(); 
		
		String sql = "";
		
		try {
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();
			
			// 검색어를 입력하지 않았다면?
			if(keyWord == null || keyWord.isEmpty()) {
				// 가장 최신글이 위로 올라오게 pos필드값을 기준으로 하여 오름차순 정렬하여 글검색
				sql = "select * from Board order by pos";
				//sql = "select * from tblBoard order by num desc";
			}
			else { // 검색어를 입력했다면
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
			System.out.println("BoardDao클래스의 getBoardList() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}
		return vec;
		
	}
	
	//들여쓰기
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
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();
			
			String sql = "alter table Board auto_increment = 1";
			
			pstmt = con.prepareStatement(sql);
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("BoardDao클래스의 initiateNum() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}
	}
	
}
