package org.dync.teameeting.bean;

import java.util.Date;


public class ChatMessage {
    /**
     * message type
     */
    private Type type;

    /**
     * Message Content
     */
    private String content;

    /**
     * Date
     */
    private Date date;

    /**
     * Date format
     */
    private String dateStr;

    /**
     * who send
     */
    private String username;



    public ChatMessage(Type type, String msg, String name, String dateStr) {
        super();
        this.type = type;
        this.content = msg;
        this.username = name;
        this.dateStr = dateStr;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getDateStr() {
        return Long.valueOf(dateStr);
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public Date getDate() {
        return date;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public enum Type {INPUT,
        OUTPUT;
    }
}
