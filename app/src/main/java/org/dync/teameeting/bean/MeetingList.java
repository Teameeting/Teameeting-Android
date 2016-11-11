package org.dync.teameeting.bean;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhulang  on 2015/12/28 0028.
 */
public class MeetingList {
    private int code;
    private String message;
    private long requestid;

    private List<MeetingListEntity> meetingList = new ArrayList<MeetingListEntity>();

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRequestid(long requestid) {
        this.requestid = requestid;
    }

    public void setMeetingList(List<MeetingListEntity> meetingList) {
        this.meetingList = meetingList;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public long getRequestid() {
        return requestid;
    }

    public List<MeetingListEntity> getMeetingList() {
        return meetingList;
    }
}
