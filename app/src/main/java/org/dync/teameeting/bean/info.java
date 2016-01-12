package org.dync.teameeting.bean;

/**
 * Created by H_lang on 2016/1/12.
 */
public class info {

    /**
     * meetingid : 400000000492
     * userid : b1ea95e9-157f-4c7e-a144-e5636d582ba6
     * meetname : 1225
     * meetdesc :
     * meetusable : 1
     * pushable : 1
     * meettype1 : 0
     * memnumber : 0
     * crttime : 1452497084808
     */

    private String meetingid;
    private String userid;
    private String meetname;
    private String meetdesc;
    private int meetusable;
    private int pushable;
    private int meettype1;
    private int memnumber;
    private long crttime;

    public void setMeetingid(String meetingid) {
        this.meetingid = meetingid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setMeetname(String meetname) {
        this.meetname = meetname;
    }

    public void setMeetdesc(String meetdesc) {
        this.meetdesc = meetdesc;
    }

    public void setMeetusable(int meetusable) {
        this.meetusable = meetusable;
    }

    public void setPushable(int pushable) {
        this.pushable = pushable;
    }

    public void setMeettype1(int meettype1) {
        this.meettype1 = meettype1;
    }

    public void setMemnumber(int memnumber) {
        this.memnumber = memnumber;
    }

    public void setCrttime(long crttime) {
        this.crttime = crttime;
    }

    public String getMeetingid() {
        return meetingid;
    }

    public String getUserid() {
        return userid;
    }

    public String getMeetname() {
        return meetname;
    }

    public String getMeetdesc() {
        return meetdesc;
    }

    public int getMeetusable() {
        return meetusable;
    }

    public int getPushable() {
        return pushable;
    }

    public int getMeettype1() {
        return meettype1;
    }

    public int getMemnumber() {
        return memnumber;
    }

    public long getCrttime() {
        return crttime;
    }
}
