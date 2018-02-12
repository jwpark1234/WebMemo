package com.memo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class UserDao {

	//DB작업 삼총사 변수 선언
	private Connection con;
	private PreparedStatement pstmt;
	private ResultSet rs;
	// 커넥션풀을 담을 변수 선언
	private DataSource ds;
	
	// 커넥션풀 얻는 작업
	public UserDao() {
		// TODO Auto-generated constructor stub
	
		try {
			// 1. Was서버와 연결된 Memo웹프로젝트의 모든 정보를 가지고 있는 컨텍스트 객체 생성
			Context init = new InitialContext();
			// 2. 연결된 Was서버에서 DataSource(커넥션 풀) 검색해서 가져오기
			ds = (DataSource) init.lookup("java:comp/env/jdbc/memo");
			
		} catch (Exception e) {
			System.out.println("UserDao()생성자에서 커넥션풀 얻기 실패 : " + e);
		}
	}
	
	// 커넥션풀에서 사용한 커넥션 객체를 반납
	public void freeResource() {
		if(con != null) try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
		if(pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
		if(rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
	}
		
	// 로그인 메서드
	public int login(String userId, String pw) {
		String sql = "select pw from User where userId =?";
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				if(rs.getString(1).equals(pw)) {
					return 1; // 로그인 성공
				}
				else
					return 0; // 로그인 실패 - 비밀번호 틀림
			}
			return -1; // 로그인 실패 - 아이디 없음
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("UserDao클래스의 login() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}
		return -2; // 데이터베이스 오류
	}
	
	// 중복가입 확인 메서드
	public int checkUser(String userId) {
		String sql = "select * from User where userId =?";

		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();
			if(rs.next()) 
				return 0; // 아이디 중복
			else
				return 1; // 가입 가능한 아이디
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("UserDao클래스의 checkUser() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}
		return -1; // 데이터베이스 오류
	}
	
	// 회원을 추가하는 메서드
	public int insertUser(UserDto dto) {
		
		try {
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();
			
			// dto에 있는 데이터를 DB에 저장
			String sql = "insert into User(userId,pw,name,address,email,phone) values (?,?,?,?,?,?)";
			
			//Connection객체의 힘을 빌려! insert구문을 DB에 실행할!
			//PreparedStatement객체를 얻을 수있는데
			//이 PreparedStatement객체를 얻어 올때는????를 제외한 나머지 insert구문을
			//PreparedStatement객체에 저장하여!!
			//PreparedStatement객체 자제를 리턴받아온다.
			//아래의 메소드에서!!
			pstmt = con.prepareStatement(sql); //insert구문을 실행할 PreparedStatement객체
			pstmt.setString(1, dto.getUserId());
			pstmt.setString(2, dto.getPw());
			pstmt.setString(3, dto.getName());
			pstmt.setString(4, dto.getAddress());
			pstmt.setString(5, dto.getEmail());
			pstmt.setString(6, dto.getPhone());
			
			return pstmt.executeUpdate();  // 가입 성공 : 1
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("UserDao클래스의 insertUser() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}
		return -2; // 이미 등록된 아이디 
	}
	
	// 회원 정보를 수정하는 메서드
	public int updateUser(UserDto dto) {
		
		try {
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();
			
			// dto에 있는 데이터를 DB에 저장
			String sql = "update User set pw =?, name =?, address =?, email =?, phone =? where userId =?";
			
			//Connection객체의 힘을 빌려! update구문을 DB에 실행할!
			//PreparedStatement객체를 얻을 수있는데
			//이 PreparedStatement객체를 얻어 올때는????를 제외한 나머지 update구문을
			//PreparedStatement객체에 저장하여!!
			//PreparedStatement객체 자제를 리턴받아온다.
			//아래의 메소드에서!!
			pstmt = con.prepareStatement(sql); //update구문을 실행할 PreparedStatement객체
			pstmt.setString(1, dto.getPw());
			pstmt.setString(2, dto.getName());
			pstmt.setString(3, dto.getAddress());
			pstmt.setString(4, dto.getEmail());
			pstmt.setString(5, dto.getPhone());
			pstmt.setString(6, dto.getUserId());
			
			pstmt.executeUpdate();

			return 1; // 수정 완료
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("UserDao클래스의 updateUser() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}
		return -2; // 데이터베이스 오류
	}
	
	// 회원을 삭제하는 메서드
	public void deleteUser(String userId) {
		
		try {
			con = ds.getConnection();
				
			// userId값인 회원이 공유받은 관계 삭제
			String sql = "delete from Share where userId =?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
					
			pstmt.executeUpdate();	
			
			// userId값인 회원 소유의 모든 메모 삭제
			sql = "delete from Memo where userId =?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
					
			pstmt.executeUpdate();	

			// userId값인 회원 삭제
			sql = "delete from User where userId =?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("UserDao클래스의 deleteUser() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}
		
	}
	
	// 회원 정보를 조회하는 메서드
	public UserDto getUser(String userId) {
		
		UserDto dto = new UserDto();
		
		try {
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();
					
			// no값인 게시글을 받아온다.
			String sql = "select * from User where userId =?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();
			
			rs.next();
			dto.setUserId(rs.getString("userId"));
			dto.setPw(rs.getString("pw"));
			dto.setName(rs.getString("name"));
			dto.setAddress(rs.getString("address"));
			dto.setEmail(rs.getString("email"));
			dto.setPhone(rs.getString("phone"));

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("UserDao클래스의 getUser() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}
		return dto;
	}
	
}
