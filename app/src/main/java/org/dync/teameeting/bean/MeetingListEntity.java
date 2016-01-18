package org.dync.teameeting.bean;

import android.content.Context;

import com.orhanobut.logger.Logger;

import org.dync.teameeting.db.CRUDChat;
import org.dync.teameeting.db.chatdao.ChatCacheEntity;
import org.dync.teameeting.utils.StringHelper;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhulang on 2016/1/8 0008.
 */
public class MeetingListEntity implements Serializable {

    private long createtime;
    private long jointime;
    private String meetdesc;
    private String meetingid;
    private String meetinguserid;
    private String meetname;
    private int meettype;
    private int meetusable;
    private int memnumber;
    private int owner;
    private int pushable;
    private int mMeetType2 = 0;
    private boolean isRead = true;
    private String unReadMessage;


    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public boolean isRead(Context context) {
        if (meetingid != null) {
            long l = CRUDChat.selectLoadListSize(context, meetingid);
            isRead = l > 0 ? false : true;
        }
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public String getUnReadMessage() {
        return unReadMessage;
    }

    public void setUnReadMessage(String unReadMessage) {
        this.unReadMessage = unReadMessage;
    }


    private boolean applyTyep = true; //true:success  false : wait

    public boolean isApplyTyep() {
        return applyTyep;
    }

    public void setApplyTyep(boolean applyTyep) {
        this.applyTyep = applyTyep;
    }


    public void setJointime(long jointime) {
        this.jointime = jointime;
    }

    public void setMeetdesc(String meetdesc) {
        this.meetdesc = meetdesc;
    }

    public void setMeetingid(String meetingid) {
        this.meetingid = meetingid;
    }

    public void setMeetinguserid(String meetinguserid) {
        this.meetinguserid = meetinguserid;
    }

    public void setMeetname(String meetname) {
        this.meetname = meetname;
    }

    public void setMeettype(int meettype) {
        this.meettype = meettype;
    }

    public void setMeetusable(int meetusable) {
        this.meetusable = meetusable;
    }

    public void setMemnumber(int memnumber) {
        this.memnumber = memnumber;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public void setPushable(int pushable) {
        this.pushable = pushable;
    }

    public long getJointime() {
        return jointime;
    }

    public String getMeetdesc() {
        return meetdesc;
    }

    public String getMeetingid() {
        return meetingid;
    }

    public String getMeetinguserid() {
        return meetinguserid;
    }

    public String getMeetname() {
        return meetname;
    }

    public int getMeettype() {
        return meettype;
    }

    public int getMeetusable() {
        return meetusable;
    }

    public int getMemnumber() {
        return memnumber;
    }

    public int getOwner() {
        return owner;
    }

    public int getPushable() {
        return pushable;
    }

    public int getmMeetType2() {
        return mMeetType2;
    }

    public void setmMeetType2(int mMeetType2) {
        this.mMeetType2 = mMeetType2;
    }

    public void initUnReadMessage(Context context) {
        if (!isRead(context)) {
            ChatCacheEntity chatCacheEntity = CRUDChat.selectTopChatMessage(context, meetingid);
            long l = CRUDChat.selectLoadListSize(context, meetingid);
            long sendtimeOrlong = chatCacheEntity.getSendtimeOrlong();
            setUnReadMessage(StringHelper.unReadMessageStr(l, sendtimeOrlong, context.getResources()));
        }
    }

    @Override
    public String toString() {
        return "MeetingListEntity{" +
                "createtime=" + createtime +
                ", jointime=" + jointime +
                ", meetdesc='" + meetdesc + '\'' +
                ", meetingid='" + meetingid + '\'' +
                ", meetinguserid='" + meetinguserid + '\'' +
                ", meetname='" + meetname + '\'' +
                ", meettype=" + meettype +
                ", meetusable=" + meetusable +
                ", memnumber=" + memnumber +
                ", owner=" + owner +
                ", pushable=" + pushable +
                ", mMeetType2=" + mMeetType2 +
                ", isRead=" + isRead +
                ", unReadMessage='" + unReadMessage + '\'' +
                ", applyTyep=" + applyTyep +
                '}';
    }
}
