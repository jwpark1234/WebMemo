package com.memo;

public class ShareDto {	
	private int memoNum;		// ������ �޸��� ������ȣ 			
	private String userId;		// �������� ȸ���� id			
	private String sharedFavYN;	// �������� ȸ���� ���ã�� ����
	
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
	public String getSharedFavYN() {
		return sharedFavYN;
	}
	public void setSharedFavYN(String sharedFavYN) {
		this.sharedFavYN = sharedFavYN;
	}
}
