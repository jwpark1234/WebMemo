package com.memo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class MemoDao {

	//DB작업 삼총사 변수 선언
	private Connection con;
	private PreparedStatement pstmt;
	private ResultSet rs;
	// 커넥션풀을 담을 변수 선언
	private DataSource ds;
	
	// 커넥션풀 얻는 작업
	public MemoDao() {
		// TODO Auto-generated constructor stub
	
		try {
			// 1. Was서버와 연결된 Memo웹프로젝트의 모든 정보를 가지고 있는 컨텍스트 객체 생성
			Context init = new InitialContext();
			// 2. 연결된 Was서버에서 DataSource(커넥션 풀) 검색해서 가져오기
			ds = (DataSource) init.lookup("java:comp/env/jdbc/memo");
			
		} catch (Exception e) {
			System.out.println("MemoDao()생성자에서 커넥션풀 얻기 실패 : " + e);
		}
	}
	
	// 커넥션풀에서 사용한 커넥션 객체를 반납
	public void freeResource() {
		if(con != null) try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
		if(pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
		if(rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
	}
	
	// 새로운 메모를 추가하는 메서드
	public int insertMemo(MemoDto dto) {
		
		try {
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();
			
			// dto에 있는 데이터를 DB에 저장
			String sql = "insert into Memo(userId,subject,content,regdate,favoriteYN,color) values (?,?,?,now(),'N',?)";
			
			//Connection객체의 힘을 빌려! insert구문을 DB에 실행할!
			//PreparedStatement객체를 얻을 수있는데
			//이 PreparedStatement객체를 얻어 올때는????를 제외한 나머지 insert구문을
			//PreparedStatement객체에 저장하여!!
			//PreparedStatement객체 자제를 리턴받아온다.
			//아래의 메소드에서!!
			pstmt = con.prepareStatement(sql); //insert구문을 실행할 PreparedStatement객체
			pstmt.setString(1, dto.getUserId());
			pstmt.setString(2, dto.getSubject());
			pstmt.setString(3, dto.getContent());
			pstmt.setString(4, dto.getColor());
			
			pstmt.executeUpdate();

			sql = "select memoNum from Memo where userId =? order by memoNum desc";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, dto.getUserId());
			rs = pstmt.executeQuery();
			if(rs.next())
				return rs.getInt("memoNum"); // 등록하고 memoNum을 반환한다.
			
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("MemoDao클래스의 insertMemo() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}
		return -1; // 등록 실패
	}
			
	// 메모의 내용을 수정하는 메서드
	public void updateMemo(MemoDto dto) {
		
		try {
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();

			// 메모index에 해당하는 메모의 내용을 수정한다.
			String sql = "update Memo set userId =?, subject =?, content =?, color =?, regdate = now() where memoNum =?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, dto.getUserId());
			pstmt.setString(2, dto.getSubject());
			pstmt.setString(3, dto.getContent());
			pstmt.setString(4, dto.getColor());
			pstmt.setInt(5, dto.getMemoNum());
			
			pstmt.executeUpdate();	
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("MemoDao클래스의 updateMemo() 메서드의 오류 : " + e);
		}finally {
			freeResource();
		}
		
	}
	
	// 메모를 삭제하는 메서드
	public void deleteMemo(int memoNum) {
		
		try {
			con = ds.getConnection();
			
			// memoNum값인 공유관계 삭제
			String sql = "delete from Share where memoNum =?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			pstmt.executeUpdate();
			
			// memoNum값인 메모를 삭제
			sql = "delete from Memo where memoNum =?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			pstmt.executeUpdate();

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("MemoDao클래스의 deleteMemo() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}
		
	}
	
	// 나와 관련된 모든 메모를 조회하는 메서드
	public Vector<MemoDto> getTotalList(String userId, String keyWord) {
		Vector<MemoDto> vec = new Vector<MemoDto>();
		
		String sql = "";
		
		try {
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();
			
			// 검색어를 입력하지 않았다면?
			if(keyWord == null || keyWord.isEmpty()) {
				// Memo테이블과 Share테이블을 left outer조인한다. 
				sql = "select * from Memo as m left join Share as s on m.memoNum = s.memoNum where m.userId =? or s.userId =? order by m.regdate desc";
			}
			else { // 검색어를 입력했다면
				sql = "select * from Memo as m left join Share as s on m.memoNum = s.memoNum "
						+ "where (m.userId =? or s.userId =?) and (m.subject like '%" + keyWord + "%' or m.content like '%" + keyWord + "%' or m.userId like '%" + keyWord + "%') order by m.regdate desc";
			}
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			pstmt.setString(2, userId);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MemoDto dto = new MemoDto();
				dto.setMemoNum(rs.getInt("memoNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setRegdate(rs.getTimestamp("regdate"));
				dto.setFavoriteYN(rs.getString("favoriteYN"));
				dto.setColor(rs.getString("color"));
				
				vec.add(dto);
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("MemoDao클래스의 getTotalList() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}
		return vec;
		
	}
	
	
	// 내 메모 목록을 조회하는 메서드 
	public Vector<MemoDto> getMemoList(String userId, String keyWord) {
		Vector<MemoDto> vec = new Vector<MemoDto>();
		
		String sql = "";
		
		try {
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();
			
			// 검색어를 입력하지 않았다면?
			if(keyWord == null || keyWord.isEmpty()) {
				sql = "select * from Memo where userId =? order by regdate desc";
			}
			else { // 검색어를 입력했다면
				sql = "select * from Memo where userId =? and (subject like '%" + keyWord + "%' or content like '%" + keyWord + "%' ) order by regdate desc";
			}
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MemoDto dto = new MemoDto();
				dto.setMemoNum(rs.getInt("memoNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setRegdate(rs.getTimestamp("regdate"));
				dto.setFavoriteYN(rs.getString("favoriteYN"));
				dto.setColor(rs.getString("color"));
				
				vec.add(dto);
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("MemoDao클래스의 getMemoList() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}
		return vec;
		
	}
	
	// 메모 1개를 조회하는 메서드
	public MemoDto getMemo(int memoNum) {
		MemoDto dto = new MemoDto();
		
		try {
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();
			
			String sql = "select * from Memo where memoNum = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			rs = pstmt.executeQuery();
			
			rs.next();
			dto.setMemoNum(rs.getInt("memoNum"));
			dto.setUserId(rs.getString("userId"));
			dto.setSubject(rs.getString("subject"));
			dto.setContent(rs.getString("content"));
			dto.setRegdate(rs.getTimestamp("regdate"));
			dto.setFavoriteYN(rs.getString("favoriteYN"));
			dto.setColor(rs.getString("color"));
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("MemoDao클래스의 getMemo() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}		
		return dto;
		
	}
	
	// 내 메모를 즐겨찾기 온/오프 메서드
	public String favoriteMemo(int memoNum) {
		
		try {
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();

			String sql = "select favoriteYN from Memo where memoNum =?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			rs = pstmt.executeQuery();
			
			String result = "N";
			if(rs.next()) {
				if(rs.getString(1).equals("Y")) { // 메모가 즐겨찾기상태였다면 즐겨찾기 해제함
					sql = "update Memo set favoriteYN = 'N' where memoNum =?";
				}
				// 메모가 즐겨찾기상태가 아니면 즐겨찾기 추가함
				else if(rs.getString(1).equals("N") || rs.getString(1).equals("NULL")) {
					sql = "update Memo set favoriteYN = 'Y' where memoNum =?";
					result = "Y";
				}
			}
			
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			pstmt.executeUpdate();
			
			return result;
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("MemoDao클래스의 favoriteMemo() 메서드의 오류 : " + e);
		}finally {
			freeResource();
		}
		return "fail";
	}
		
	// 즐겨찾기 리스트를 조회할 메서드
	public Vector<MemoDto> getFavoriteList(String userId, String keyWord) {
		Vector<MemoDto> vec = new Vector<MemoDto>();
					
		try {
			String sql = "";
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();
			if(keyWord == null || keyWord.isEmpty()) 
				// 나의 관련된 모든 메모 중에 즐겨찾기가 추가된 메모를 조회한다.
				sql = "select * from Memo as m left join Share as s on m.memoNum = s.memoNum where (m.userId =? and m.favoriteYN = 'Y') or (s.userId =? and s.sharedFavYN = 'Y') order by m.regdate desc";
			else
				// 나의 관련된 모든 메모 중에 즐겨찾기가 추가된 메모를 조회한다.
				sql = "select * from Memo as m left join Share as s on m.memoNum = s.memoNum where ((m.userId =? and m.favoriteYN = 'Y') or (s.userId =? and s.sharedFavYN = 'Y')) and (m.subject like '%" + keyWord + "%' or m.content like '%" + keyWord + "%' ) order by m.regdate desc";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			pstmt.setString(2, userId);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MemoDto dto = new MemoDto();
				dto.setMemoNum(rs.getInt("m.memoNum"));
				dto.setUserId(rs.getString("m.userId"));
				dto.setSubject(rs.getString("m.subject"));
				dto.setContent(rs.getString("m.content"));
				dto.setRegdate(rs.getTimestamp("m.regdate"));
				dto.setFavoriteYN(rs.getString("m.favoriteYN"));
				dto.setColor(rs.getString("color"));
				
				vec.add(dto);
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("MemoDao클래스의 getFavoriteList() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}		
		
		return vec;
	}
	
	// 메모를 공유시키는 메서드
	public int shareMemo(int memoNum, String userId) {
		
		try {
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();

			// 공유 대상이 있는지 확인한다.
			String sql = "select * from user where userId=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();
			if(!rs.next())
				return 0; // 공유할 대상이 없다.
			
			sql = "select * from share where memoNum =? and userId =?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			pstmt.setString(2, userId);
			rs = pstmt.executeQuery();
			if(rs.next())
				return -1; // 이미 같은 메모를 같은 회원에게 공유했다.
			
			sql = "select * from memo where memoNum =?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				if(userId.equals(rs.getString("userId")))
					return -2; // 메모의 원래 주인에게 다시 공유했다.
			}
			
			// 해당 메모와 공유받을 아이디를 Share 테이블에 추가한다.
			sql = "insert into Share(memoNum,userId,sharedFavYN) values (?,?,'N')";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			pstmt.setString(2, userId);
			
			return pstmt.executeUpdate(); // 결과값은 반환한다. 1 - 공유성공
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("MemoDao클래스의 shareMemo() 메서드의 오류 : " + e);
		}finally {
			freeResource();
		}
		return -3; // DB오류
	}
	
	// 공유한 메모를 공유해제시키는 메서드
	public int killShareMemo(int memoNum, String userId) {
	
		try {
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();
		
			// 공유 관계를 Share 테이블에서 삭제한다.
			String sql = "delete from Share where memoNum =? and userId =?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			pstmt.setString(2, userId);
			
			return pstmt.executeUpdate();	
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("MemoDao클래스의 killShareMemo() 메서드의 오류 : " + e);
		}finally {
			freeResource();
		}
		return -1; // 데이터베이스 오류
	}
	
	// 내가 공유한 메모 리스트를 조회하는 메서드
	public Vector<MemoDto> getSharingList(String userId, String keyWord) {
		Vector<MemoDto> vec = new Vector<MemoDto>();
		
		try {
			String sql = "";
			
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();
			if(keyWord == null || keyWord.isEmpty()) 
				sql = "select * from Memo as m join Share as s on m.memoNum = s.memoNum where m.userId =? order by m.regdate desc";
			else
				sql = "select * from Memo as m join Share as s on m.memoNum = s.memoNum where m.userId =? and (m.subject like '%" + keyWord + "%' or m.content like '%" + keyWord + "%' or s.userId like '%" + keyWord + "%') order by m.regdate desc";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MemoDto dto = new MemoDto();
				dto.setMemoNum(rs.getInt("m.memoNum"));
				dto.setUserId(rs.getString("s.userId")); // 임시로 내 메모를 공유받은 유저 아이디를 저장 
				dto.setSubject(rs.getString("m.subject"));
				dto.setContent(rs.getString("m.content"));
				dto.setRegdate(rs.getTimestamp("m.regdate"));
				dto.setFavoriteYN(rs.getString("m.favoriteYN"));
				dto.setColor(rs.getString("color"));
				
				vec.add(dto);
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("MemoDao클래스의 getSharingMemo() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}
		
		return vec;
	}
	
	// 내가 공유받은 메모 리스트를 조회하는 메서드
	public Vector<MemoDto> getSharedList(String userId, String keyWord) {
		Vector<MemoDto> vec = new Vector<MemoDto>();
		
		try {
			String sql = "";
			
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();
			if(keyWord == null || keyWord.isEmpty()) 
				sql = "select * from Memo as m join Share as s on m.memoNum = s.memoNum where s.userId =? order by m.regdate desc";
			else
				sql = "select * from Memo as m join Share as s on m.memoNum = s.memoNum where s.userId =? and (m.subject like '%" + keyWord + "%' or m.content like '%" + keyWord + "%' or m.userId like '%" + keyWord + "%') order by m.regdate desc";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MemoDto dto = new MemoDto();
				dto.setMemoNum(rs.getInt("m.memoNum"));
				dto.setUserId(rs.getString("m.userId"));
				dto.setSubject(rs.getString("m.subject"));
				dto.setContent(rs.getString("m.content"));
				dto.setRegdate(rs.getTimestamp("m.regdate"));
				dto.setFavoriteYN(rs.getString("m.favoriteYN"));
				dto.setColor(rs.getString("color"));
				
				vec.add(dto);
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("MemoDao클래스의 getSharedMemo() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}
		
		return vec;
	}
	
	// 공유받은 메모를 즐겨찾기 온/오프 메서드
	public String sharedFavMemo(int memoNum, String userId) {
			
		try {
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();
			
			String sql = "select sharedFavYN from Share where memoNum =? and userId =?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			pstmt.setString(2, userId);
			rs = pstmt.executeQuery();
			
			// 공유받은 메모의 즐겨찾기 여부는 share테이블에 저장된다.
			String result = "N";
			if(rs.next()) {
				if(rs.getString(1).equals("Y")) { // 즐겨찾기 중인 메모이면
					sql = "update Share set sharedFavYN = 'N' where memoNum =? and userId =?";
				}
				else if(rs.getString(1).equals("N") || rs.getString(1).equals("NULL")) {// 즐겨찾기 중인 메모가 아니면
					sql = "update Share set sharedFavYN = 'Y' where memoNum =? and userId =?";
					result = "Y";
				}	
			}
			
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			pstmt.setString(2, userId);
			
			pstmt.executeUpdate();	
			
			return result;
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("MemoDao클래스의 sharedFavMemo() 메서드의 오류 : " + e);
		}finally {
			freeResource();
		}
		return "fail";
	}
	
	// 공유받은 메모의 즐겨찾기 여부 칼럼을 조회할 메서드
	public String getSharedFavMemo(int memoNum, String userId) {
					
		try {
			//DataSource(커넥션풀)에서 DB와 미리 연결된 Connection 객체를 얻어온다.
			con = ds.getConnection();
			
			// 공유받은 메모의 즐겨찾기 여부는 share테이블에 조회한다.
			String sql = "select sharedFavYN from Share where memoNum =? and userId =?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			pstmt.setString(2, userId);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return rs.getString(1);
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("MemoDao클래스의 getSharedFavMemo() 메서드의 오류 : " + e);
		} finally {
			freeResource();
		}		
		
		return "fail";
	}
	
}
	
	