package org.dync.teameeting.bean;

import java.io.Serializable;

public class RoomItem  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private int mMeetusable;
	private int mPushable;
	private int mMeetType;
	private int mMeetType2;
	private int mMemNumber;
	private String mMeetingId;
	private String mJoinTime;
	private String mUserId;
	private String mMeetName;
	private String mMeetDesc;
	private int mOwner;
	
	public RoomItem(Long id, int mMeetusable, int mPushable, int mMeetType,
			int mMeetType2, int mMemNumber, String mMeetingId, String mJoinTime,
			int mOwner, String mUserId, String mMeetName, String mMeetDesc
			) {
		super();
		this.id = id;
		this.mMeetusable = mMeetusable;
		this.mPushable = mPushable;
		this.mMeetType = mMeetType;
		this.mMeetType2 = mMeetType2;
		this.mMemNumber = mMemNumber;
		this.mMeetingId = mMeetingId;
		this.mJoinTime = mJoinTime;
		this.mOwner = mOwner;
		this.mUserId = mUserId;
		this.mMeetName = mMeetName;
		this.mMeetDesc = mMeetDesc;
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getmMeetusable() {
		return mMeetusable;
	}

	public void setmMeetusable(int mMeetusable) {
		this.mMeetusable = mMeetusable;
	}

	public int getmPushable() {
		return mPushable;
	}

	public void setmPushable(int mPushable) {
		this.mPushable = mPushable;
	}

	public int getmMeetType() {
		return mMeetType;
	}

	public void setmMeetType(int mMeetType) {
		this.mMeetType = mMeetType;
	}

	public int getmMeetType2() {
		return mMeetType2;
	}

	public void setmMeetType2(int mMeetType2) {
		this.mMeetType2 = mMeetType2;
	}

	public int getmMemNumber() {
		return mMemNumber;
	}

	public void setmMemNumber(int mMemNumber) {
		this.mMemNumber = mMemNumber;
	}

	public String getmMeetingId() {
		return mMeetingId;
	}

	public void setmMeetingId(String mMeetingId) {
		this.mMeetingId = mMeetingId;
	}



	public String getmJoinTime() {
		return mJoinTime;
	}

	public void setmJoinTime(String mJoinTime) {
		this.mJoinTime = mJoinTime;
	}

	public int getmOwner() {
		return mOwner;
	}

	public void setmOwner(int mOwner) {
		this.mOwner = mOwner;
	}

	public String getmUserId() {
		return mUserId;
	}

	public void setmUserId(String mUserId) {
		this.mUserId = mUserId;
	}

	public String getmMeetName() {
		return mMeetName;
	}

	public void setmMeetName(String mMeetName) {
		this.mMeetName = mMeetName;
	}

	public String getmMeetDesc() {
		return mMeetDesc;
	}

	public void setmMeetDesc(String mMeetDesc) {
		this.mMeetDesc = mMeetDesc;
	}




	
	
	

	





}
