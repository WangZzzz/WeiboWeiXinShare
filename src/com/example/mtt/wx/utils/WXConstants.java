package com.example.mtt.wx.utils;

public class WXConstants {
	public static final String APP_ID = "wx244f60a9d31cc40c";

	public static final String AppSecret = "36b82f33343ac81fbe455eb76c865e78";

	/*
	 * 获取accesstoken的url
	 */
	public static final String AccessTokenURL = "https://api.weixin.qq.com/sns/oauth2/access_token";

	public static final String RefreshTokenURL = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
	/*
	 * 支持分享到朋友圈的微信版本
	 */
	public static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
}
