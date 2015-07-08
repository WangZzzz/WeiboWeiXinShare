package com.example.mtt.wx.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class WXAccessTokenKeeper {
	private static final String PREFERENCES_NAME = "com_weixin_sdk_android";

	private static final String KEY_ACCESS_TOKEN = "access_token";
	private static final String KEY_OPEN_ID = "openid";
	private static final String KEY_EXPIRES_IN = "expires_in";
	private static final String KEY_EXPIRES_AT = "expires_at";
	private static final String KEY_REFRESH_TOKEN = "refresh_token";

	/**
	 * 保存 Token 对象�? SharedPreferences�?
	 * 
	 * @param context
	 *            应用程序上下文环�?
	 * @param token
	 *            Token 对象
	 */
	public static void writeAccessToken(Context context, WXAccessToken token) {
		if (null == context || null == token) {
			return;
		}

		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(KEY_ACCESS_TOKEN, token.getAccess_token());
		editor.putString(KEY_OPEN_ID, token.getOpenid());
		editor.putInt(KEY_EXPIRES_IN, token.getExpires_in());
		editor.putLong(KEY_EXPIRES_AT, token.getExpires_at());
		editor.putString(KEY_REFRESH_TOKEN, token.getRefresh_token());
		editor.commit();
	}

	/**
	 * �? SharedPreferences 读取 Token 信息�?
	 * 
	 * @param context
	 *            应用程序上下文环�?
	 * 
	 * @return 返回 Token 对象
	 */
	public static WXAccessToken readAccessToken(Context context) {
		if (null == context) {
			return null;
		}

		WXAccessToken token = new WXAccessToken();
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
		token.setAccess_token(pref.getString(KEY_ACCESS_TOKEN, ""));
		token.setOpenid(pref.getString(KEY_OPEN_ID, ""));
		token.setExpires_in(pref.getInt(KEY_EXPIRES_IN, 0));
		token.setExpires_at(pref.getLong(KEY_EXPIRES_AT, 0));
		token.setRefresh_token(pref.getString(KEY_REFRESH_TOKEN, ""));
		return token;
	}

	/**
	 * 清空 SharedPreferences �? Token信息�?
	 * 
	 * @param context
	 *            应用程序上下文环�?
	 */
	public static void clear(Context context) {
		if (null == context) {
			return;
		}

		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}
}
