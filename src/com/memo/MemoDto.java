package com.memo;

import java.sql.Timestamp;

public class MemoDto {
	private int memoNum;		// �޸��� ������ num���Դϴ�. primary key
	private String userId;		// �޸� �ۼ��� ȸ�� id�Դϴ�. foreign key
	private String subject;		// �޸��� �����Դϴ�.
	private String content;		// �޸��� �����Դϴ�.
	private Timestamp regdate;	// �޸� ���/������ �ð��Դϴ�.
	private String favoriteYN;	// ���ã�� ���� �������Դϴ�.
	private String color;		// �޸� �����Դϴ�.
	
	public int getMemoNum() {
		return memoNum;
	}
	public void setMemoNum(int memoNum) {
		this.memoNum = memoNum;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Timestamp getRegdate() {
		return regdate;
	}
	public void setRegdate(Timestamp regdate) {
		this.regdate = regdate;
	}
	public String getFavoriteYN() {
		return favoriteYN;
	}
	public void setFavoriteYN(String favoriteYN) {
		this.favoriteYN = favoriteYN;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}	
	
}
