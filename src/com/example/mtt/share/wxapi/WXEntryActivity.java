package com.example.mtt.share.wxapi;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SignUpCallback;
import com.example.mtt.share.R;
import com.example.mtt.share.utils.HttpUtils;
import com.example.mtt.wx.utils.WXAccessToken;
import com.example.mtt.wx.utils.WXAccessTokenKeeper;
import com.example.mtt.wx.utils.WXConstants;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth.Resp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/*
 * 必须新建一个包，在app包名上夹wxapi,Activity的名称必须为WXEntryActivity
 * 当前 Activity 必须实现微信的 IWXAPIEventHandler 
 * WXEntryActivity 类的layout布局文件不用做任何编码
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	// IWXAPI 是第三方app和微信通信的openapi接口
	private IWXAPI api;
	// private Button wx_share;

	// 申请的APPID
	private static final String APP_ID = "wx244f60a9d31cc40c";

	private String code = null;
	private String state = null;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				String tokenString = (String) msg.obj;
				try {
					JSONObject jsonObject = new JSONObject(tokenString);
					String access_token = jsonObject.getString("access_token");
					if (!TextUtils.isEmpty(access_token)) {
						/*
						 * expire_in:在多久之后过期 expire_at:当前时间+expire_in ，过期的时间点
						 */
						int expires_in = jsonObject.getInt("expires_in");
						long expires_at = System.currentTimeMillis()
								+ expires_in;
						String openid = jsonObject.getString("openid");
						/*
						 * refresh_token：更新token信息时使用
						 */
						String refresh_token = jsonObject
								.getString("refresh_token");
						Log.d(WXEntryActivity.class.getName(), access_token
								+ " @ " + expires_at + " @ " + openid);
						WXAccessToken wxAccessToken = new WXAccessToken();
						wxAccessToken.setAccess_token(access_token);
						wxAccessToken.setOpenid(openid);
						wxAccessToken.setExpires_in(expires_in);
						wxAccessToken.setExpires_at(expires_at);
						wxAccessToken.setRefresh_token(refresh_token);
						/*
						 * 将token记录到SharedPreference
						 */
						WXAccessTokenKeeper.writeAccessToken(
								WXEntryActivity.this, wxAccessToken);
						AVUser.AVThirdPartyUserAuth userAuth = new AVUser.AVThirdPartyUserAuth(
								access_token, expires_at + "", "weixin", openid);
					} else {
						String errmsg = jsonObject.getString("errmsg");
						Log.d(WXEntryActivity.class.getName(), errmsg);
						Toast.makeText(WXEntryActivity.this,
								"微信授权失败：" + errmsg, Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_wxentry);
		// 通过WXAPI工厂，获取WXAPI实例
		api = WXAPIFactory.createWXAPI(this, APP_ID, true);
		// initView();
		Log.d(WXEntryActivity.class.getName(), "onCreate");
		api.handleIntent(getIntent(), this);
	}

	/*
	 * private void initView(){ wx_share = (Button)findViewById(R.id.wx_share);
	 * wx_share.setOnClickListener(this); }
	 */

	@Override
	public void onReq(BaseReq req) {
		// TODO Auto-generated method stub
		Log.d(WXEntryActivity.class.getName(), "onReq");

	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {

		Log.d(WXEntryActivity.class.getName(), "onResp");
		String result = null;
		Bundle bundle = new Bundle();
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			resp.toBundle(bundle);
			Resp sp = new Resp(bundle);
			code = sp.code;
			state = sp.state;
			/*
			 * code为空说明是调用的分享接口，不为空说明调用的是授权接口
			 */
			Log.d(this.getClass().getName(), ((code == null) ? "空" : (code
					+ " @ " + state)));
			if (TextUtils.isEmpty(code)) {
				result = "分享成功";
			} else {
				result = "授权成功";
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						String url = WXConstants.AccessTokenURL + "?appid="
								+ WXConstants.APP_ID + "&secret="
								+ WXConstants.AppSecret + "&code=" + code
								+ "&grant_type=authorization_code";
						Log.d(WXEntryActivity.class.getName(), url);
						String tmp = HttpUtils.HttpGetMethod(url);
						Log.d(WXEntryActivity.class.getName(), tmp);
						Message message = Message.obtain();
						message.what = 0;
						message.obj = tmp;
						mHandler.sendMessage(message);
					}
				}).start();
			}
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = "取消操作";
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = "拒绝操作";
			break;
		default:
			result = "分享失败";
			break;
		}

		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
		finish();
	}
}
