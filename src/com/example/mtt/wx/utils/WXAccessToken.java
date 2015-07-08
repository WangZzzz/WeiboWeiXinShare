package com.example.mtt.wx.utils;

public class WXAccessToken {

	private String access_token;
	private int expires_in;
	private long expires_at;
	private String refresh_token;
	private String openid;

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public int getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}

	public long getExpires_at() {
		return expires_at;
	}

	public void setExpires_at(long expires_at) {
		this.expires_at = expires_at;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}
}
