package com.memo;

public class UserDto {
	private String userId;		// ȸ���� id�Դϴ�.  primary key
	private String pw;			// ȸ���� ��й�ȣ�Դϴ�.
	private String name;		// ȸ���� �̸��Դϴ�.
	private String address;		// ȸ���� �ּ��Դϴ�.
	private String email;		// ȸ���� email �ּ��Դϴ�.
	private String phone;		// ȸ���� ��ȭ��ȣ�Դϴ�.

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
}
