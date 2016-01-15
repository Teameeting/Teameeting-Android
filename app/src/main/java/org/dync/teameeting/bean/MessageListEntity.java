package org.dync.teameeting.bean;

/**
 * Created by zhangqilu on 2016/1/14.
 */
public class MessageListEntity {

    /**
     * id : 56
     * messagetype : 0
     * meetingid : 400000000491
     * sessionid : e4cd6dad85c08adb73a2f9b9d7547386
     * userid : a2835c4583ec7bab
     * sendtime : 1452758647241
     * message : 123
     */

    private int id;
    private int messagetype;
    private String meetingid;
    private String sessionid;
    private String userid;
    private long sendtime;
    private String message;

    public void setId(int id) {
        this.id = id;
    }

    public void setMessagetype(int messagetype) {
        this.messagetype = messagetype;
    }

    public void setMeetingid(String meetingid) {
        this.meetingid = meetingid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setSendtime(long sendtime) {
        this.sendtime = sendtime;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public int getMessagetype() {
        return messagetype;
    }

    public String getMeetingid() {
        return meetingid;
    }

    public String getSessionid() {
        return sessionid;
    }

    public String getUserid() {
        return userid;
    }

    public long getSendtime() {
        return sendtime;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "MessageListEntity{" +
                "id=" + id +
                ", messagetype=" + messagetype +
                ", meetingid='" + meetingid + '\'' +
                ", sessionid='" + sessionid + '\'' +
                ", userid='" + userid + '\'' +
                ", sendtime=" + sendtime +
                ", message='" + message + '\'' +
                '}';
    }
}
