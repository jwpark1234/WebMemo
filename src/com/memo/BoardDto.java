package com.memo;

import java.sql.Timestamp;

// jspbeginner�����ͺ��̽� ���ο� �ִ� tblBoard���̺� ���ڵ带 BoardDtoŬ������ ���� ��ü�� �����Ͽ�
// BoardDto��ü ������ insert�ϱ� ���� Ŭ����
// ����� ���̺��� �ʵ��� Ŭ������ �������� ���� �Ѵ�.
public class BoardDto {
	private int num;			// �۹�ȣ ���� primary key
	private String name;		// �۾��� ����
	private String email;		// �۾����� �̸��� ���� ����
	private String subject;		// ������ ����
	private String content;		// �۳��� ����
	private String pw;			// �ۿ� ���� Pass�� ����
	private int count;			// �� ��ȸ�� ����
	private Timestamp regdate;	// �۾� �ð� ����
	private int pos;			// �亯�ޱ� �ʵ�1
	private int depth;			// �亯�ޱ� �ʵ�2
	
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
