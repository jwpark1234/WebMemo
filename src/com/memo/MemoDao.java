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

	//DB�۾� ���ѻ� ���� ����
	private Connection con;
	private PreparedStatement pstmt;
	private ResultSet rs;
	// Ŀ�ؼ�Ǯ�� ���� ���� ����
	private DataSource ds;
	
	// Ŀ�ؼ�Ǯ ��� �۾�
	public MemoDao() {
		// TODO Auto-generated constructor stub
	
		try {
			// 1. Was������ ����� Memo��������Ʈ�� ��� ������ ������ �ִ� ���ؽ�Ʈ ��ü ����
			Context init = new InitialContext();
			// 2. ����� Was�������� DataSource(Ŀ�ؼ� Ǯ) �˻��ؼ� ��������
			ds = (DataSource) init.lookup("java:comp/env/jdbc/memo");
			
		} catch (Exception e) {
			System.out.println("MemoDao()�����ڿ��� Ŀ�ؼ�Ǯ ��� ���� : " + e);
		}
	}
	
	// Ŀ�ؼ�Ǯ���� ����� Ŀ�ؼ� ��ü�� �ݳ�
	public void freeResource() {
		if(con != null) try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
		if(pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
		if(rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
	}
	
	// ���ο� �޸� �߰��ϴ� �޼���
	public int insertMemo(MemoDto dto) {
		
		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();
			
			// dto�� �ִ� �����͸� DB�� ����
			String sql = "insert into Memo(userId,subject,content,regdate,favoriteYN,color) values (?,?,?,now(),'N',?)";
			
			//Connection��ü�� ���� ����! insert������ DB�� ������!
			//PreparedStatement��ü�� ���� ���ִµ�
			//�� PreparedStatement��ü�� ��� �ö���????�� ������ ������ insert������
			//PreparedStatement��ü�� �����Ͽ�!!
			//PreparedStatement��ü ������ ���Ϲ޾ƿ´�.
			//�Ʒ��� �޼ҵ忡��!!
			pstmt = con.prepareStatement(sql); //insert������ ������ PreparedStatement��ü
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
				return rs.getInt("memoNum"); // ����ϰ� memoNum�� ��ȯ�Ѵ�.
			
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("MemoDaoŬ������ insertMemo() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}
		return -1; // ��� ����
	}
			
	// �޸��� ������ �����ϴ� �޼���
	public void updateMemo(MemoDto dto) {
		
		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();

			// �޸�index�� �ش��ϴ� �޸��� ������ �����Ѵ�.
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
			System.out.println("MemoDaoŬ������ updateMemo() �޼����� ���� : " + e);
		}finally {
			freeResource();
		}
		
	}
	
	// �޸� �����ϴ� �޼���
	public void deleteMemo(int memoNum) {
		
		try {
			con = ds.getConnection();
			
			// memoNum���� �������� ����
			String sql = "delete from Share where memoNum =?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			pstmt.executeUpdate();
			
			// memoNum���� �޸� ����
			sql = "delete from Memo where memoNum =?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			pstmt.executeUpdate();

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("MemoDaoŬ������ deleteMemo() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}
		
	}
	
	// ���� ���õ� ��� �޸� ��ȸ�ϴ� �޼���
	public Vector<MemoDto> getTotalList(String userId, String keyWord) {
		Vector<MemoDto> vec = new Vector<MemoDto>();
		
		String sql = "";
		
		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();
			
			// �˻�� �Է����� �ʾҴٸ�?
			if(keyWord == null || keyWord.isEmpty()) {
				// Memo���̺�� Share���̺��� left outer�����Ѵ�. 
				sql = "select * from Memo as m left join Share as s on m.memoNum = s.memoNum where m.userId =? or s.userId =? order by m.regdate desc";
			}
			else { // �˻�� �Է��ߴٸ�
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
			System.out.println("MemoDaoŬ������ getTotalList() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}
		return vec;
		
	}
	
	
	// �� �޸� ����� ��ȸ�ϴ� �޼��� 
	public Vector<MemoDto> getMemoList(String userId, String keyWord) {
		Vector<MemoDto> vec = new Vector<MemoDto>();
		
		String sql = "";
		
		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();
			
			// �˻�� �Է����� �ʾҴٸ�?
			if(keyWord == null || keyWord.isEmpty()) {
				sql = "select * from Memo where userId =? order by regdate desc";
			}
			else { // �˻�� �Է��ߴٸ�
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
			System.out.println("MemoDaoŬ������ getMemoList() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}
		return vec;
		
	}
	
	// �޸� 1���� ��ȸ�ϴ� �޼���
	public MemoDto getMemo(int memoNum) {
		MemoDto dto = new MemoDto();
		
		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
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
			System.out.println("MemoDaoŬ������ getMemo() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}		
		return dto;
		
	}
	
	// �� �޸� ���ã�� ��/���� �޼���
	public String favoriteMemo(int memoNum) {
		
		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();

			String sql = "select favoriteYN from Memo where memoNum =?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			rs = pstmt.executeQuery();
			
			String result = "N";
			if(rs.next()) {
				if(rs.getString(1).equals("Y")) { // �޸� ���ã����¿��ٸ� ���ã�� ������
					sql = "update Memo set favoriteYN = 'N' where memoNum =?";
				}
				// �޸� ���ã����°� �ƴϸ� ���ã�� �߰���
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
			System.out.println("MemoDaoŬ������ favoriteMemo() �޼����� ���� : " + e);
		}finally {
			freeResource();
		}
		return "fail";
	}
		
	// ���ã�� ����Ʈ�� ��ȸ�� �޼���
	public Vector<MemoDto> getFavoriteList(String userId, String keyWord) {
		Vector<MemoDto> vec = new Vector<MemoDto>();
					
		try {
			String sql = "";
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();
			if(keyWord == null || keyWord.isEmpty()) 
				// ���� ���õ� ��� �޸� �߿� ���ã�Ⱑ �߰��� �޸� ��ȸ�Ѵ�.
				sql = "select * from Memo as m left join Share as s on m.memoNum = s.memoNum where (m.userId =? and m.favoriteYN = 'Y') or (s.userId =? and s.sharedFavYN = 'Y') order by m.regdate desc";
			else
				// ���� ���õ� ��� �޸� �߿� ���ã�Ⱑ �߰��� �޸� ��ȸ�Ѵ�.
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
			System.out.println("MemoDaoŬ������ getFavoriteList() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}		
		
		return vec;
	}
	
	// �޸� ������Ű�� �޼���
	public int shareMemo(int memoNum, String userId) {
		
		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();

			// ���� ����� �ִ��� Ȯ���Ѵ�.
			String sql = "select * from user where userId=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();
			if(!rs.next())
				return 0; // ������ ����� ����.
			
			sql = "select * from share where memoNum =? and userId =?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			pstmt.setString(2, userId);
			rs = pstmt.executeQuery();
			if(rs.next())
				return -1; // �̹� ���� �޸� ���� ȸ������ �����ߴ�.
			
			sql = "select * from memo where memoNum =?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				if(userId.equals(rs.getString("userId")))
					return -2; // �޸��� ���� ���ο��� �ٽ� �����ߴ�.
			}
			
			// �ش� �޸�� �������� ���̵� Share ���̺� �߰��Ѵ�.
			sql = "insert into Share(memoNum,userId,sharedFavYN) values (?,?,'N')";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			pstmt.setString(2, userId);
			
			return pstmt.executeUpdate(); // ������� ��ȯ�Ѵ�. 1 - ��������
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("MemoDaoŬ������ shareMemo() �޼����� ���� : " + e);
		}finally {
			freeResource();
		}
		return -3; // DB����
	}
	
	// ������ �޸� ����������Ű�� �޼���
	public int killShareMemo(int memoNum, String userId) {
	
		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();
		
			// ���� ���踦 Share ���̺��� �����Ѵ�.
			String sql = "delete from Share where memoNum =? and userId =?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			pstmt.setString(2, userId);
			
			return pstmt.executeUpdate();	
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("MemoDaoŬ������ killShareMemo() �޼����� ���� : " + e);
		}finally {
			freeResource();
		}
		return -1; // �����ͺ��̽� ����
	}
	
	// ���� ������ �޸� ����Ʈ�� ��ȸ�ϴ� �޼���
	public Vector<MemoDto> getSharingList(String userId, String keyWord) {
		Vector<MemoDto> vec = new Vector<MemoDto>();
		
		try {
			String sql = "";
			
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
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
				dto.setUserId(rs.getString("s.userId")); // �ӽ÷� �� �޸� �������� ���� ���̵� ���� 
				dto.setSubject(rs.getString("m.subject"));
				dto.setContent(rs.getString("m.content"));
				dto.setRegdate(rs.getTimestamp("m.regdate"));
				dto.setFavoriteYN(rs.getString("m.favoriteYN"));
				dto.setColor(rs.getString("color"));
				
				vec.add(dto);
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("MemoDaoŬ������ getSharingMemo() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}
		
		return vec;
	}
	
	// ���� �������� �޸� ����Ʈ�� ��ȸ�ϴ� �޼���
	public Vector<MemoDto> getSharedList(String userId, String keyWord) {
		Vector<MemoDto> vec = new Vector<MemoDto>();
		
		try {
			String sql = "";
			
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
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
			System.out.println("MemoDaoŬ������ getSharedMemo() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}
		
		return vec;
	}
	
	// �������� �޸� ���ã�� ��/���� �޼���
	public String sharedFavMemo(int memoNum, String userId) {
			
		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();
			
			String sql = "select sharedFavYN from Share where memoNum =? and userId =?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, memoNum);
			pstmt.setString(2, userId);
			rs = pstmt.executeQuery();
			
			// �������� �޸��� ���ã�� ���δ� share���̺� ����ȴ�.
			String result = "N";
			if(rs.next()) {
				if(rs.getString(1).equals("Y")) { // ���ã�� ���� �޸��̸�
					sql = "update Share set sharedFavYN = 'N' where memoNum =? and userId =?";
				}
				else if(rs.getString(1).equals("N") || rs.getString(1).equals("NULL")) {// ���ã�� ���� �޸� �ƴϸ�
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
			System.out.println("MemoDaoŬ������ sharedFavMemo() �޼����� ���� : " + e);
		}finally {
			freeResource();
		}
		return "fail";
	}
	
	// �������� �޸��� ���ã�� ���� Į���� ��ȸ�� �޼���
	public String getSharedFavMemo(int memoNum, String userId) {
					
		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();
			
			// �������� �޸��� ���ã�� ���δ� share���̺� ��ȸ�Ѵ�.
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
			System.out.println("MemoDaoŬ������ getSharedFavMemo() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}		
		
		return "fail";
	}
	
}
	
	