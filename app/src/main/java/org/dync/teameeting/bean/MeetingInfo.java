package org.dync.teameeting.bean;

import java.io.Serializable;


/**
 * Created by zhangqilu on 2016/1/12.
 */
public class MeetingInfo {
    /*    info {"meetingid":"400000000492","userid":"b1ea95e9-157f-4c7e-a144-e5636d582ba6","meetname":"1225","meetdesc":"","meetusable":1,
                "pushable":1,"meettype1":0,"memnumber":0,"crttime":1452497084808}*/
    private String meetingid;
    private String userid;
    private String meetname;
    private String meetdesc;
    private int meetenable;
    private int pushable;
    private int meettype1;
    private int memnumber;
    private long crttime;

    public String getMeetingid() {
        return meetingid;
    }

    public void setMeetingid(String meetingid) {
        this.meetingid = meetingid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMeetname() {
        return meetname;
    }

    public void setMeetname(String meetname) {
        this.meetname = meetname;
    }

    public String getMeetdesc() {
        return meetdesc;
    }

    public void setMeetdesc(String meetdesc) {
        this.meetdesc = meetdesc;
    }

    public int getMeetenable() {
        return meetenable;
    }

    public void setMeetenable(int meetenable) {
        this.meetenable = meetenable;
    }

    public int getPushable() {
        return pushable;
    }

    public void setPushable(int pushable) {
        this.pushable = pushable;
    }

    public int getMeettype1() {
        return meettype1;
    }

    public void setMeettype1(int meettype1) {
        this.meettype1 = meettype1;
    }

    public int getMemnumber() {
        return memnumber;
    }

    public void setMemnumber(int memnumber) {
        this.memnumber = memnumber;
    }

    public long getCrttime() {
        return crttime;
    }

    public void setCrttime(long crttime) {
        this.crttime = crttime;
    }

    @Override
    public String toString() {
        return "MeetingInfo{" + "meetingid='" + meetingid + '\'' +
                ", userid='" + userid + '\'' + ", meetname='" + meetname + '\'' +
                ", meetdesc='" + meetdesc + '\'' + ", meetusable=" + meetenable +
                ", pushable=" + pushable + ", meettype1=" + meettype1 + ", memnumber=" +
                memnumber + ", crttime=" + crttime + '}';
    }
}
