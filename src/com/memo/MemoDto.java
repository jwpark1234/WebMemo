package com.memo;

import java.sql.Timestamp;

public class MemoDto {
	private int memoNum;		// 메모의 고유한 num값입니다. primary key
	private String userId;		// 메모를 작성한 회원 id입니다. foreign key
	private String subject;		// 메모의 제목입니다.
	private String content;		// 메모의 내용입니다.
	private Timestamp regdate;	// 메모가 등록/수정된 시간입니다.
	private String favoriteYN;	// 즐겨찾기 유무 데이터입니다.
	private String color;		// 메모 색상입니다.
	
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
