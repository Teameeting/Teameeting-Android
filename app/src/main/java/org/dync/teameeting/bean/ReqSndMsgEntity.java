package org.dync.teameeting.bean;

/**
 * Created by zhulang on 2016/1/9 0009.
 */
public class ReqSndMsgEntity {

    /**
     * mtype : 3
     * cmd : 6
     * action : 1
     * tags : 1
     * type : 1
     * nmem : 0
     * ntime : 1452338777912
     * mseq : 14
     * from : 436d31bd40689af7
     * room : 400000000482
     * sess :
     * to :
     * cont : dff
     * pass : 4d142459e0316f0285264c380b40b760e50cf306bb64b52ef272b5a20941a687
     * code : 0
     * status :
     */

    private int mtype;
    private int cmd;
    private int action;
    private int tags;
    private int type;
    private int nmem;
    private long ntime;
    private int mseq;
    private String from;
    private String room;
    private String sess;
    private String to;
    private String cont;
    private String pass;
    private int code;
    private String status;

    public void setMtype(int mtype) {
        this.mtype = mtype;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public void setTags(int tags) {
        this.tags = tags;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setNmem(int nmem) {
        this.nmem = nmem;
    }

    public void setNtime(long ntime) {
        this.ntime = ntime;
    }

    public void setMseq(int mseq) {
        this.mseq = mseq;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setSess(String sess) {
        this.sess = sess;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setCont(String cont) {
        this.cont = cont;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMtype() {
        return mtype;
    }

    public int getCmd() {
        return cmd;
    }

    public int getAction() {
        return action;
    }

    public int getTags() {
        return tags;
    }

    public int getType() {
        return type;
    }

    public int getNmem() {
        return nmem;
    }

    public long getNtime() {
        return ntime;
    }

    public int getMseq() {
        return mseq;
    }

    public String getFrom() {
        return from;
    }

    public String getRoom() {
        return room;
    }

    public String getSess() {
        return sess;
    }

    public String getTo() {
        return to;
    }

    public String getCont() {
        return cont;
    }

    public String getPass() {
        return pass;
    }

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "ReqSndMsgEntity{" +
                "ntime=" + ntime +
                ", from='" + from + '\'' +
                ", room='" + room + '\'' +
                ", to='" + to + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
