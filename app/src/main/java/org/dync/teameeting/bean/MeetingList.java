package org.dync.teameeting.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhulang  on 2015/12/28 0028.
 */
public class MeetingList {

    /**
     * code : 200
     * meetingList : [{"jointime":1451201381005,"meetdesc":"","meetingid":"400000000147","meetinguserid":"cc1f0115b54c46a1","meetname":"Untitled room","meettype":0,"meetusable":1,"memnumber":0,"owner":1,"pushable":1},{"jointime":1451130113421,"meetdesc":"","meetingid":"400000000146","meetinguserid":"cc1f0115b54c46a1","meetname":"Untitled room","meettype":0,"meetusable":1,"memnumber":0,"owner":1,"pushable":1},{"jointime":1451129707889,"meetdesc":"","meetingid":"400000000145","meetinguserid":"cc1f0115b54c46a1","meetname":"Untitled room???","meettype":0,"meetusable":1,"memnumber":0,"owner":1,"pushable":1},{"jointime":1451129691076,"meetdesc":"","meetingid":"400000000144","meetinguserid":"cc1f0115b54c46a1","meetname":"Untitled room","meettype":0,"meetusable":1,"memnumber":0,"owner":1,"pushable":1},{"jointime":1451129560784,"meetdesc":"","meetingid":"400000000142","meetinguserid":"cc1f0115b54c46a1","meetname":"Untitled room","meettype":0,"meetusable":1,"memnumber":0,"owner":1,"pushable":1},{"jointime":1451129548950,"meetdesc":"","meetingid":"400000000140","meetinguserid":"cc1f0115b54c46a1","meetname":"Untitled room","meettype":0,"meetusable":1,"memnumber":0,"owner":1,"pushable":1}]
     * message : get user meeting room list success
     * requestid : 1451292857674
     */

    private int code;
    private String message;
    private long requestid;
    /**
     * jointime : 1451201381005
     * meetdesc :
     * meetingid : 400000000147
     * meetinguserid : cc1f0115b54c46a1
     * meetname : Untitled room
     * meettype : 0
     * meetusable : 1
     * memnumber : 0
     * owner : 1
     * pushable : 1
     */

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

    public void setMeetingList(List<MeetingListEntity> meetingList) {this.meetingList = meetingList;}

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
