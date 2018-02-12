package com.memo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class UserDao {

	//DB�۾� ���ѻ� ���� ����
	private Connection con;
	private PreparedStatement pstmt;
	private ResultSet rs;
	// Ŀ�ؼ�Ǯ�� ���� ���� ����
	private DataSource ds;
	
	// Ŀ�ؼ�Ǯ ��� �۾�
	public UserDao() {
		// TODO Auto-generated constructor stub
	
		try {
			// 1. Was������ ����� Memo��������Ʈ�� ��� ������ ������ �ִ� ���ؽ�Ʈ ��ü ����
			Context init = new InitialContext();
			// 2. ����� Was�������� DataSource(Ŀ�ؼ� Ǯ) �˻��ؼ� ��������
			ds = (DataSource) init.lookup("java:comp/env/jdbc/memo");
			
		} catch (Exception e) {
			System.out.println("UserDao()�����ڿ��� Ŀ�ؼ�Ǯ ��� ���� : " + e);
		}
	}
	
	// Ŀ�ؼ�Ǯ���� ����� Ŀ�ؼ� ��ü�� �ݳ�
	public void freeResource() {
		if(con != null) try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
		if(pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
		if(rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
	}
		
	// �α��� �޼���
	public int login(String userId, String pw) {
		String sql = "select pw from User where userId =?";
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				if(rs.getString(1).equals(pw)) {
					return 1; // �α��� ����
				}
				else
					return 0; // �α��� ���� - ��й�ȣ Ʋ��
			}
			return -1; // �α��� ���� - ���̵� ����
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("UserDaoŬ������ login() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}
		return -2; // �����ͺ��̽� ����
	}
	
	// �ߺ����� Ȯ�� �޼���
	public int checkUser(String userId) {
		String sql = "select * from User where userId =?";

		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();
			if(rs.next()) 
				return 0; // ���̵� �ߺ�
			else
				return 1; // ���� ������ ���̵�
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("UserDaoŬ������ checkUser() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}
		return -1; // �����ͺ��̽� ����
	}
	
	// ȸ���� �߰��ϴ� �޼���
	public int insertUser(UserDto dto) {
		
		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();
			
			// dto�� �ִ� �����͸� DB�� ����
			String sql = "insert into User(userId,pw,name,address,email,phone) values (?,?,?,?,?,?)";
			
			//Connection��ü�� ���� ����! insert������ DB�� ������!
			//PreparedStatement��ü�� ���� ���ִµ�
			//�� PreparedStatement��ü�� ��� �ö���????�� ������ ������ insert������
			//PreparedStatement��ü�� �����Ͽ�!!
			//PreparedStatement��ü ������ ���Ϲ޾ƿ´�.
			//�Ʒ��� �޼ҵ忡��!!
			pstmt = con.prepareStatement(sql); //insert������ ������ PreparedStatement��ü
			pstmt.setString(1, dto.getUserId());
			pstmt.setString(2, dto.getPw());
			pstmt.setString(3, dto.getName());
			pstmt.setString(4, dto.getAddress());
			pstmt.setString(5, dto.getEmail());
			pstmt.setString(6, dto.getPhone());
			
			return pstmt.executeUpdate();  // ���� ���� : 1
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("UserDaoŬ������ insertUser() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}
		return -2; // �̹� ��ϵ� ���̵� 
	}
	
	// ȸ�� ������ �����ϴ� �޼���
	public int updateUser(UserDto dto) {
		
		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();
			
			// dto�� �ִ� �����͸� DB�� ����
			String sql = "update User set pw =?, name =?, address =?, email =?, phone =? where userId =?";
			
			//Connection��ü�� ���� ����! update������ DB�� ������!
			//PreparedStatement��ü�� ���� ���ִµ�
			//�� PreparedStatement��ü�� ��� �ö���????�� ������ ������ update������
			//PreparedStatement��ü�� �����Ͽ�!!
			//PreparedStatement��ü ������ ���Ϲ޾ƿ´�.
			//�Ʒ��� �޼ҵ忡��!!
			pstmt = con.prepareStatement(sql); //update������ ������ PreparedStatement��ü
			pstmt.setString(1, dto.getPw());
			pstmt.setString(2, dto.getName());
			pstmt.setString(3, dto.getAddress());
			pstmt.setString(4, dto.getEmail());
			pstmt.setString(5, dto.getPhone());
			pstmt.setString(6, dto.getUserId());
			
			pstmt.executeUpdate();

			return 1; // ���� �Ϸ�
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("UserDaoŬ������ updateUser() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}
		return -2; // �����ͺ��̽� ����
	}
	
	// ȸ���� �����ϴ� �޼���
	public void deleteUser(String userId) {
		
		try {
			con = ds.getConnection();
				
			// userId���� ȸ���� �������� ���� ����
			String sql = "delete from Share where userId =?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
					
			pstmt.executeUpdate();	
			
			// userId���� ȸ�� ������ ��� �޸� ����
			sql = "delete from Memo where userId =?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
					
			pstmt.executeUpdate();	

			// userId���� ȸ�� ����
			sql = "delete from User where userId =?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("UserDaoŬ������ deleteUser() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}
		
	}
	
	// ȸ�� ������ ��ȸ�ϴ� �޼���
	public UserDto getUser(String userId) {
		
		UserDto dto = new UserDto();
		
		try {
			//DataSource(Ŀ�ؼ�Ǯ)���� DB�� �̸� ����� Connection ��ü�� ���´�.
			con = ds.getConnection();
					
			// no���� �Խñ��� �޾ƿ´�.
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
			System.out.println("UserDaoŬ������ getUser() �޼����� ���� : " + e);
		} finally {
			freeResource();
		}
		return dto;
	}
	
}
