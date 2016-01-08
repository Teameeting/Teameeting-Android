package org.dync.teameeting.bean;

import java.util.Date;

public class ChatMessage
{

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
    private String name;

    public enum Type
    {
        INPUT, OUTPUT
    }

    public ChatMessage(Type type, String msg, String name, String dateStr)
    {
        super();
        this.type = type;
        this.content = msg;
        this.name = name;
        this.dateStr = dateStr;
    }

    public long getDateStr()
    {
        return Long.valueOf(dateStr);
    }

    public void setDateStr(String dateStr)
    {
        this.dateStr = dateStr;
    }

    public Date getDate()
    {
        return date;
    }


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

}
