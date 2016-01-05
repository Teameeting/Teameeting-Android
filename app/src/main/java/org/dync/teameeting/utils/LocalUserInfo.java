package org.dync.teameeting.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalUserInfo
{

	public static final String PREFERENCE_NAME = "local_userinfo";
    public static final String FIRST_LOGIN="firstLogin";
	private static SharedPreferences mSharedPreferences;
	private static LocalUserInfo mPreferenceUtils;
	private static SharedPreferences.Editor editor;

	private  LocalUserInfo(Context cxt)
	{
		mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	/**
	 * Get Single(global) SharedPreferences
	 * 
	 * @param cxt
	 * @return
	 */
	public static LocalUserInfo getInstance(Context cxt)
	{
		if (mPreferenceUtils == null)
		{
			mPreferenceUtils = new LocalUserInfo(cxt);
		}
		editor = mSharedPreferences.edit();
		return mPreferenceUtils;
	}


	public void setUserInfo(String str_name, String str_value)
	{

		editor.putString(str_name, str_value);
		editor.commit();
	}

	public void setUserInfoBoolean(String str_name, Boolean str_value)
	{

		editor.putBoolean(str_name, str_value);
		editor.commit();
	}

	public String getUserInfo(String str_name)
	{

		return mSharedPreferences.getString(str_name, "");

	}

	public Boolean getUserInfoBoolean(String str_name)
	{

		return mSharedPreferences.getBoolean(str_name, false);

	}

}
