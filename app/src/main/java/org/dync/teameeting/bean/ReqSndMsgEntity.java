package org.dync.teameeting.bean;

/**
 * Created by zhulang on 2016/1/9 0009.
 */
public class ReqSndMsgEntity {

    private int mtype;
    private int messagetype;
    private int signaltype;
    private int cmd;
    private int action;
    private int tags;
    private int type;
    private int nmem;
    private long ntime;
    private int mseq;
    private String from;
    private String room;
    private String to;
    private String cont;
    private String pass;
    private String nname;
    private String rname;
    private int code;

    public void setMtype(int mtype) {
        this.mtype = mtype;
    }

    public void setMessagetype(int messagetype) {
        this.messagetype = messagetype;
    }

    public void setSignaltype(int signaltype) {
        this.signaltype = signaltype;
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

    public void setTo(String to) {
        this.to = to;
    }

    public void setCont(String cont) {
        this.cont = cont;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setNname(String nname) {
        this.nname = nname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getMtype() {
        return mtype;
    }

    public int getMessagetype() {
        return messagetype;
    }

    public int getSignaltype() {
        return signaltype;
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

    public String getTo() {
        return to;
    }

    public String getCont() {
        return cont;
    }

    public String getPass() {
        return pass;
    }

    public String getNname() {
        return nname;
    }

    public String getRname() {
        return rname;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "ReqSndMsgEntity{" +
                "mtype=" + mtype +
                ", messagetype=" + messagetype +
                ", signaltype=" + signaltype +
                ", cmd=" + cmd +
                ", action=" + action +
                ", tags=" + tags +
                ", type=" + type +
                ", nmem=" + nmem +
                ", ntime=" + ntime +
                ", mseq=" + mseq +
                ", from='" + from + '\'' +
                ", room='" + room + '\'' +
                ", to='" + to + '\'' +
                ", cont='" + cont + '\'' +
                ", pass='" + pass + '\'' +
                ", nname='" + nname + '\'' +
                ", rname='" + rname + '\'' +
                ", code=" + code +
                '}';
    }
}
