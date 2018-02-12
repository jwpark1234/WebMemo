package com.memo;

public class UserDto {
	private String userId;		// 회원의 id입니다.  primary key
	private String pw;			// 회원의 비밀번호입니다.
	private String name;		// 회원의 이름입니다.
	private String address;		// 회원의 주소입니다.
	private String email;		// 회원의 email 주소입니다.
	private String phone;		// 회원의 전화번호입니다.

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
