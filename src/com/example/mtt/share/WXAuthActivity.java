package com.example.mtt.share;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.mtt.share.wxapi.WXEntryActivity;
import com.example.mtt.wx.utils.WXAccessToken;
import com.example.mtt.wx.utils.WXAccessTokenKeeper;
import com.example.mtt.wx.utils.WXConstants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXAuthActivity extends Activity implements OnClickListener {

	// 第三方应用和微信通信的接口
	private IWXAPI api;

	final SendAuth.Req req = new SendAuth.Req();

	private Button wx_auth_bt;
	private Button wx_clear_bt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wxauth);
		initView();
		initData();

	}

	private void initView() {
		wx_auth_bt = (Button) findViewById(R.id.wx_auth_bt);
		wx_clear_bt = (Button) findViewById(R.id.wx_clear_bt);
		wx_auth_bt.setOnClickListener(this);
		wx_clear_bt.setOnClickListener(this);
	}

	private void initData() {
		api = WXAPIFactory.createWXAPI(this, WXConstants.APP_ID, true);
		regToWx();

		req.scope = "snsapi_userinfo";
		/*
		 * 用于保持请求和回调的状态，授权请求后原样带回给第三方。 该参数可用于防止csrf攻击（跨站请求伪造攻击），
		 * 建议第三方带上该参数，可设置为简单的随机数加session进行校验
		 */
		req.state = System.currentTimeMillis() + "";
	}

	/*
	 * 将APP注册到微信
	 */
	private void regToWx() {
		// 将应用的APP_ID注册到微信
		api.registerApp(WXConstants.APP_ID);
	}

	private void sendAuth() {
		api.sendReq(req);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.wx_auth_bt:
			sendAuth();
			break;
		case R.id.wx_clear_bt:
			WXAccessTokenKeeper.clear(WXAuthActivity.this);
			WXAccessToken tmp = WXAccessTokenKeeper
					.readAccessToken(WXAuthActivity.this);
			Log.d(WXEntryActivity.class.getName(), tmp.getExpires_at() + "");
			break;
		default:
			break;
		}
	}
}
