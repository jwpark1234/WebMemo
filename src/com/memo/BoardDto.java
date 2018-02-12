package com.memo;

import java.sql.Timestamp;

// jspbeginner데이터베이스 내부에 있는 tblBoard테이블에 레코드를 BoardDto클래스에 대한 객체를 생성하여
// BoardDto객체 단위로 insert하기 위한 클래스
// 참고로 테이블의 필드명과 클래스의 변수명이 같게 한다.
public class BoardDto {
	private int num;			// 글번호 저장 primary key
	private String name;		// 글쓴이 저장
	private String email;		// 글쓴이의 이메일 정보 저장
	private String subject;		// 글제목 저장
	private String content;		// 글내용 저장
	private String pw;			// 글에 대한 Pass값 저장
	private int count;			// 글 조회수 저장
	private Timestamp regdate;	// 글쓴 시간 저장
	private int pos;			// 답변달기 필드1
	private int depth;			// 답변달기 필드2
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Timestamp getRegdate() {
		return regdate;
	}
	public void setRegdate(Timestamp regdate) {
		this.regdate = regdate;
	}
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
}
