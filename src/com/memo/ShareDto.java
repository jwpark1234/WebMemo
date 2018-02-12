package com.memo;

public class ShareDto {	
	private int memoNum;		// 공유된 메모의 고유번호 			
	private String userId;		// 공유받은 회원의 id			
	private String sharedFavYN;	// 공유받은 회원의 즐겨찾기 여부
	
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
