package org.dync.teameeting.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 小白龙 on 2015/12/28 0028.
 */
public class MeetingList
{

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

    private List<MeetingListEntity> meetingList;

    public void setCode(int code)
    {
        this.code = code;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setRequestid(long requestid)
    {
        this.requestid = requestid;
    }

    public void setMeetingList(List<MeetingListEntity> meetingList)
    {
        this.meetingList = meetingList;
    }

    public int getCode()
    {
        return code;
    }

    public String getMessage()
    {
        return message;
    }

    public long getRequestid()
    {
        return requestid;
    }

    public List<MeetingListEntity> getMeetingList()
    {
        return meetingList;
    }

    public static class MeetingListEntity implements Serializable
    {
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



        private boolean applyTyep = true; //true:success  false : wait

        public boolean isApplyTyep()
        {
            return applyTyep;
        }

        public void setApplyTyep(boolean applyTyep)
        {
            this.applyTyep = applyTyep;
        }


        public void setJointime(long jointime)
        {
            this.jointime = jointime;
        }

        public void setMeetdesc(String meetdesc)
        {
            this.meetdesc = meetdesc;
        }

        public void setMeetingid(String meetingid)
        {
            this.meetingid = meetingid;
        }

        public void setMeetinguserid(String meetinguserid)
        {
            this.meetinguserid = meetinguserid;
        }

        public void setMeetname(String meetname)
        {
            this.meetname = meetname;
        }

        public void setMeettype(int meettype)
        {
            this.meettype = meettype;
        }

        public void setMeetusable(int meetusable)
        {
            this.meetusable = meetusable;
        }

        public void setMemnumber(int memnumber)
        {
            this.memnumber = memnumber;
        }

        public void setOwner(int owner)
        {
            this.owner = owner;
        }

        public void setPushable(int pushable)
        {
            this.pushable = pushable;
        }

        public long getJointime()
        {
            return jointime;
        }

        public String getMeetdesc()
        {
            return meetdesc;
        }

        public String getMeetingid()
        {
            return meetingid;
        }

        public String getMeetinguserid()
        {
            return meetinguserid;
        }

        public String getMeetname()
        {
            return meetname;
        }

        public int getMeettype()
        {
            return meettype;
        }

        public int getMeetusable()
        {
            return meetusable;
        }

        public int getMemnumber()
        {
            return memnumber;
        }

        public int getOwner()
        {
            return owner;
        }

        public int getPushable()
        {
            return pushable;
        }

        public int getmMeetType2()
        {
            return mMeetType2;
        }

        public void setmMeetType2(int mMeetType2)
        {
            this.mMeetType2 = mMeetType2;
        }

        @Override
        public String toString()
        {
            return "MeetingListEntity{" +
                    "jointime=" + jointime +
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
                    '}';
        }
    }
}
