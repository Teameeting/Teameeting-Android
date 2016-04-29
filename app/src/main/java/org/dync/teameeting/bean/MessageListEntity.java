package org.dync.teameeting.bean;

/**
 * Created by zhangqilu on 2016/1/14.
 */
public class MessageListEntity {

    private int id;
    private int messagetype;
    private String meetingid;
    private String sessionid;
    private String userid;
    private long sendtime;
    private String message;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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
