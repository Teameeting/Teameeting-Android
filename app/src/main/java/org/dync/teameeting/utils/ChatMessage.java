package org.dync.teameeting.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

	public ChatMessage(Type type, String msg,String name)
	{
		super();
		this.type = type;
		this.content = msg;
		this.name = name;
		setDate(new Date());
	}

	public String getDateStr()
	{
		return dateStr;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.dateStr = df.format(date);

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
