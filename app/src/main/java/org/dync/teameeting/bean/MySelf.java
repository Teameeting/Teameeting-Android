package org.dync.teameeting.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MySelf
{

    /**
     * package name
     */
    private String mAppId;
    /**
     * name
     */
    private String mName;
    /**
     * userId
     */
    private String mUserId;
    /**
     * Status
     */
    private String mStatus;
    /**
     * RegisterType
     */
    private String mRegisterType;
    /**
     * LoginType
     */
    private String mLoginType;
    /**
     * Authorization
     */
    private String mAuthorization;
    /**
     * UserType
     */
    private String mUserType;
    /**
     * RegisterTime
     */
    private String mRegisterTime;
    /**
     * PushToken
     */
    private String mPushToken;
    /**
     * IsNetConnected
     */
    private boolean mIsNetConnected;

    private List<RoomItem> mRoomList = new ArrayList<RoomItem>();

    public MySelf()
    {
        // TODO Auto-generated constructor stub
    }

    public String getmAppId()
    {
        return mAppId;
    }

    public void setmAppId(String mAppId)
    {
        this.mAppId = mAppId;
    }

    public String getmName()
    {
        return mName;
    }

    public void setmName(String mName)
    {
        this.mName = mName;
    }

    public String getmUserId()
    {
        return mUserId;
    }

    public void setmUserId(String mUserId)
    {
        this.mUserId = mUserId;
    }

    public String getmStatus()
    {
        return mStatus;
    }

    public void setmStatus(String mStatus)
    {
        this.mStatus = mStatus;
    }

    public String getmRegisterType()
    {
        return mRegisterType;
    }

    public void setmRegisterType(String mRegisterType)
    {
        this.mRegisterType = mRegisterType;
    }

    public String getmLoginType()
    {
        return mLoginType;
    }

    public void setmLoginType(String mLoginType)
    {
        this.mLoginType = mLoginType;
    }

    public String getmAuthorization()
    {
        return mAuthorization;
    }

    public void setmAuthorization(String mAuthorization)
    {
        this.mAuthorization = mAuthorization;
    }

    public String getmUserType()
    {
        return mUserType;
    }

    public void setmUserType(String mUserType)
    {
        this.mUserType = mUserType;
    }

    public String getmRegisterTime()
    {
        return mRegisterTime;
    }

    public void setmRegisterTime(String mRegisterTime)
    {
        this.mRegisterTime = mRegisterTime;
    }

    public String getmPushToken()
    {
        return mPushToken;
    }

    public void setmPushToken(String mPushToken)
    {
        this.mPushToken = mPushToken;
    }

    public boolean ismIsNetConnected()
    {
        return mIsNetConnected;
    }

    public void setmIsNetConnected(boolean mIsNetConnected)
    {
        this.mIsNetConnected = mIsNetConnected;
    }

    public List<RoomItem> getmRoomList()
    {
        return mRoomList;
    }

    public void setmRoomList(List<RoomItem> mRoomList)
    {
        this.mRoomList = mRoomList;
    }


}
