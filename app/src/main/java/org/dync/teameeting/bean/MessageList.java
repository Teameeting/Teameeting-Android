package org.dync.teameeting.bean;

import java.util.List;

/**
 * Created by zhangqilu on 2016/1/14.
 */
public class MessageList {

    private long requestid;
    private int code;
    private String message;

    private List<MessageListEntity> messageList;

    public void setRequestid(long requestid) {
        this.requestid = requestid;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessageList(List<MessageListEntity> messageList) {
        this.messageList = messageList;
    }

    public long getRequestid() {
        return requestid;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<MessageListEntity> getMessageList() {
        return messageList;
    }

}
