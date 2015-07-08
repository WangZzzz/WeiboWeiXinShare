package com.example.mtt.share;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.mtt.wb.utils.MyWBConstants;
import com.example.mtt.wb.utils.WBAccessTokenKeeper;
import com.example.mtt.wx.utils.WXConstants;
import com.example.mtt.wx.utils.WXUtil;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 
 * @author WangZ 集成微博、微信朋友、微信朋友圈分享功能
 *
 */
public class AllInOneShareActivity extends Activity implements OnClickListener {

	private Button share_bt;
	private Button cancel_bt;
	private static int SHARE_TYPE = 0;
	private final static int WB_SHARE = 0;
	private final static int WX_FRIENDS_SHARE = 1;
	private final static int WX_MOMENTS_SHARE = 2;

	/*
	 * 要分享的图片和文字信息
	 */
	private static Bitmap share_bitmap;
	private static String share_text;

	/*
	 * 略缩图
	 */
	private static final int THUMB_SIZE = 150;

	// 第三方应用和微信通信的接口
	private IWXAPI api;

	/*
	 * 以下是微博分享部分
	 */
	private AuthInfo authInfo;
	private Oauth2AccessToken mAccessToken;
	private SsoHandler mSsoHandler;
	/** 微博微博分享接口实例 */
	private IWeiboShareAPI mWeiboShareAPI = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d(AllInOneShareActivity.class.getName(), "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		initView();
		initData();
		getShareDate();
	}

	private void initView() {
		share_bt = (Button) findViewById(R.id.share_bt);
		cancel_bt = (Button) findViewById(R.id.cancel_bt);

		share_bt.setOnClickListener(this);
		cancel_bt.setOnClickListener(this);
	}

	private void initData() {
		// 微信部分
		api = WXAPIFactory.createWXAPI(this, WXConstants.APP_ID, true);
		regToWx();

		// 微博部分
		authInfo = new AuthInfo(this, MyWBConstants.APP_KEY,
				MyWBConstants.REDIRECT_URL, MyWBConstants.SCOPE);
		mSsoHandler = new SsoHandler(AllInOneShareActivity.this, authInfo);

		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this,
				MyWBConstants.APP_KEY);
	}

	/*
	 * 将APP注册到微信
	 */
	private void regToWx() {
		// 将应用的APP_ID注册到微信
		api.registerApp(WXConstants.APP_ID);
	}

	/*
	 * 得到前一个Activity传递过来的数据，type:分享类型---朋友圈、朋友、微博 image：bitmap text：文本
	 */
	private void getShareDate() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		SHARE_TYPE = bundle.getInt("type");
		share_bitmap = bundle.getParcelable("image");
		share_text = bundle.getString("text");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.share_bt: {
			/*
			 * 根据不同的分享类型，调用不同的方法
			 */
			switch (SHARE_TYPE) {
			case WX_FRIENDS_SHARE:
				// 微信朋友分享
				sendToFriends();
				finish();
				break;
			case WX_MOMENTS_SHARE:
				// 微信朋友圈分享
				sendToMoments();
				finish();
				break;
			case WB_SHARE:
				// 微博分享
				sendWbShare();
				break;
			default:
				break;
			}
			break;
		}
		case R.id.cancel_bt:
			finish();
			break;
		default:
			break;
		}
	}

	private void sendToFriends() {

		WXImageObject wxImageObject = new WXImageObject(share_bitmap);

		WXMediaMessage wMessage = new WXMediaMessage();
		wMessage.mediaObject = wxImageObject;

		// 图片描述
		wMessage.description = share_text;
		// 标题
		wMessage.title = share_text;

		// 设置略缩图
		Bitmap thumbBmp = Bitmap.createScaledBitmap(share_bitmap, THUMB_SIZE,
				THUMB_SIZE, true);
		// 回收bitmap,防止oom
		share_bitmap.recycle();
		wMessage.thumbData = WXUtil.bmpToByteArray(thumbBmp, true);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		// req.transaction = buildTransaction("img");
		// transaction字段用于唯一标识一个请求
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = wMessage;
		/*
		 * 标识发送到哪里： SendMessageToWX.Req的scene成员，如果scene填WXSceneSession，
		 * 那么消息会发送至微信的会话内。如果scene填WXSceneTimeline（微信4.2以上支持， 如果需要检查微信版本支持API的情况，
		 * 可调用IWXAPI的getWXAppSupportAPI方法,0x21020001及以上支持发送朋友圈），
		 * 那么消息会发送至朋友圈。scene默认值为WXSceneSession。
		 */
		req.scene = SendMessageToWX.Req.WXSceneSession;
		boolean result = api.sendReq(req);
		Log.d(AllInOneShareActivity.class.getName(), result + "");
	}

	/*
	 * 分享到朋友圈
	 */
	private void sendToMoments() {
		// 查看当前版本微信是否支持分享到朋友圈
		int wxSdkVersion = api.getWXAppSupportAPI();
		if (wxSdkVersion < WXConstants.TIMELINE_SUPPORTED_VERSION) {
			Toast.makeText(
					AllInOneShareActivity.this,
					"wxSdkVersion = " + Integer.toHexString(wxSdkVersion)
							+ "\ntimeline not supported", Toast.LENGTH_LONG)
					.show();
			return;
		} else {
			// 可以分享到朋友圈

			WXImageObject wxImageObject = new WXImageObject(share_bitmap);

			WXMediaMessage wMessage = new WXMediaMessage();
			wMessage.mediaObject = wxImageObject;
			// 图片描述
			wMessage.description = share_text;
			// 标题
			wMessage.title = share_text;

			// 设置略缩图
			Bitmap thumbBmp = Bitmap.createScaledBitmap(share_bitmap,
					THUMB_SIZE, THUMB_SIZE, true);
			// 回收bitmap,防止oom
			share_bitmap.recycle();
			wMessage.thumbData = WXUtil.bmpToByteArray(thumbBmp, true);
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			// req.transaction = buildTransaction("img");
			// transaction字段用于唯一标识一个请求
			req.transaction = String.valueOf(System.currentTimeMillis());
			req.message = wMessage;
			/*
			 * 标识发送到哪里： SendMessageToWX.Req的scene成员，如果scene填WXSceneSession，
			 * 那么消息会发送至微信的会话内。如果scene填WXSceneTimeline（微信4.2以上支持，
			 * 如果需要检查微信版本支持API的情况，
			 * 可调用IWXAPI的getWXAppSupportAPI方法,0x21020001及以上支持发送朋友圈），
			 * 那么消息会发送至朋友圈。scene默认值为WXSceneSession。
			 */
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
			boolean result = api.sendReq(req);
			Log.d(AllInOneShareActivity.class.getName(), result + "");
		}
	}

	/*
	 * 以下是微博分享部分
	 */

	private void sendWbShare() {
		boolean isAuthed = isAuthed();
		if (isAuthed) {
			mWeiboShareAPI.registerApp(); // 将应用程序注册到微博客户端
			// 发送消息
			sendMultiMessage();
			finish();
		} else {
			mSsoHandler.authorize(new AuthListener());
		}
	}

	// 查询授权是否在有效期内
	private boolean isAuthed() {
		/*
		 * 查询SharedPrefrence里面有没有token信息
		 */
		mAccessToken = WBAccessTokenKeeper
				.readAccessToken(AllInOneShareActivity.this);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Log.d(this.getClass().getName(),
				sdf.format(new Date(mAccessToken.getExpiresTime())));
		Log.d(this.getClass().getName(),
				sdf.format(new Date(System.currentTimeMillis())));
		/*
		 * 说明在过期时间内
		 */
		if (mAccessToken.getExpiresTime() > System.currentTimeMillis()) {
			return true;
		} else {
			// Toast.makeText(this, "请先授权", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	/*
	 * 微博认证授权回调类 当授权成功后，请保存access_token、expires_in、uid 等信息到 SharedPreferences中
	 */
	class AuthListener implements WeiboAuthListener {

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			Toast.makeText(AllInOneShareActivity.this, "取消授权",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onComplete(Bundle bundle) {
			// TODO Auto-generated method stub
			// 从Bundle中解析出Token信息
			mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
			// 获取用户输入的 电话号码信息
			String phoneNum = mAccessToken.getPhoneNum();
			if (mAccessToken.isSessionValid()) {
				// 保存 Token到SharedPreferences
				WBAccessTokenKeeper.writeAccessToken(
						AllInOneShareActivity.this, mAccessToken);
				Toast.makeText(AllInOneShareActivity.this, "授权成功",
						Toast.LENGTH_SHORT).show();
			} else {
				// 以下几种情况，您会收到Code
				// 1. 当您未在平台上注册的应用程序的包名与签名时；
				// 2. 当您注册的应用程序包名与签名不正确时
				// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时
				String code = bundle.getString("code");
				String message = "授权失败";
				if (!TextUtils.isEmpty(code)) {
					message = message + "\nObtained the code: " + code;
				}
				Toast.makeText(AllInOneShareActivity.this, message,
						Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			// TODO Auto-generated method stub
			Toast.makeText(AllInOneShareActivity.this,
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(AllInOneShareActivity.class.getName(), "onActivityResult");
		// SSO 授权回调
		// 重要：SSO 登陆,Activity 必须重写 onActivityResults
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);

			/*
			 * 发送消息
			 */
			// 当授权成功时，注册app到微博
			mWeiboShareAPI.registerApp();
			// 发送消息
			sendMultiMessage();
			finish();
		}
	}

	/*
	 * 唤起客户端发博器进行分享
	 */
	private void sendMultiMessage() {
		TextObject textObject = new TextObject();
		textObject.text = share_text;

		ImageObject imageObject = new ImageObject();
		imageObject.setImageObject(share_bitmap);

		WeiboMultiMessage weiboMessage = new WeiboMultiMessage(); // 初始化微博的分享消息
		weiboMessage.textObject = textObject;
		weiboMessage.imageObject = imageObject;

		/*
		 * 释放bitmap
		 */
		// share_bitmap.recycle();

		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;

		AuthInfo authInfo = new AuthInfo(this, MyWBConstants.APP_KEY,
				MyWBConstants.REDIRECT_URL, MyWBConstants.SCOPE);
		Oauth2AccessToken accessToken = WBAccessTokenKeeper
				.readAccessToken(getApplicationContext());

		String token = "";
		if (accessToken != null) {
			token = accessToken.getToken();
		}
		mWeiboShareAPI.sendRequest(this, request, authInfo, token,
				new AuthListener());
	}
}
