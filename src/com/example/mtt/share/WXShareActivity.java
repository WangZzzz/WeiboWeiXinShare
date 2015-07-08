package com.example.mtt.share;

import com.example.mtt.wx.utils.WXUtil;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.R.integer;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * 
 * @author WangZ
 * 微信分享,回调的Activity是WXEntryActivity，必须新建一个包，wxapi
 *
 */
public class WXShareActivity extends Activity implements OnClickListener{

	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	/*
	 * 略缩图
	 */
	private static final int THUMB_SIZE = 150;
	
	//申请的APPID
	private static final String APP_ID = "wx244f60a9d31cc40c";
	
	//第三方应用和微信通信的接口
	private IWXAPI api;
	
	private Button share_friends;
	private Button share_moments;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wxshare);
		initView();		
		api = WXAPIFactory.createWXAPI(this, APP_ID, true);
		regToWx();
	}
	
	private void initView(){
		share_friends = (Button)findViewById(R.id.share_friends);
		share_moments = (Button)findViewById(R.id.share_moments);
		
		share_friends.setOnClickListener(this);
		share_moments.setOnClickListener(this);
	}
	
	/*
	 * 将APP注册到微信
	 */
	private void regToWx(){
		//将应用的APP_ID注册到微信
		api.registerApp(APP_ID);
	}
	
	/*
	 * 获取分享的内容中的文本内容
	 */
	private String getWxText(){
		return "测试分享功能";
	}
	/*
	 * 获取分享的内容中的图片内容
	 */
	private Bitmap getWxImage(){
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		return bitmap;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.share_friends:
			sendToFriends();
			finish();
			break;
		case R.id.share_moments:
			sendToMoments();
			finish();
			break;
		default:
			break;
		}
	}
	
	private void sendToFriends(){
		Bitmap bitmap = getWxImage();
		
		WXImageObject wxImageObject = new WXImageObject(bitmap);
		
		WXMediaMessage wMessage = new WXMediaMessage();
		wMessage.mediaObject = wxImageObject;
		
		//图片描述
		wMessage.description = getWxText();
		//标题
		wMessage.title = "测试分享";
		

		//设置略缩图
		Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
		//回收bitmap,防止oom
		bitmap.recycle();
		wMessage.thumbData = WXUtil.bmpToByteArray(thumbBmp, true);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
//		req.transaction = buildTransaction("img");
		//transaction字段用于唯一标识一个请求
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = wMessage;
		/*
		 * 标识发送到哪里：
		 * 	SendMessageToWX.Req的scene成员，如果scene填WXSceneSession，
		 * 那么消息会发送至微信的会话内。如果scene填WXSceneTimeline（微信4.2以上支持，
		 * 如果需要检查微信版本支持API的情况， 可调用IWXAPI的getWXAppSupportAPI方法,0x21020001及以上支持发送朋友圈），
		 * 那么消息会发送至朋友圈。scene默认值为WXSceneSession。
		 */
		req.scene = SendMessageToWX.Req.WXSceneSession;
		boolean result = api.sendReq(req);
		Log.d(WXShareActivity.class.getName(), result + "");
	}
	
	/*
	 * 分享到朋友圈
	 */
	private void sendToMoments(){
		//查看当前版本微信是否支持分享到朋友圈
		int wxSdkVersion = api.getWXAppSupportAPI();
		if (wxSdkVersion < TIMELINE_SUPPORTED_VERSION) {
			Toast.makeText(WXShareActivity.this, "wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline not supported", Toast.LENGTH_LONG).show();
			return;			
		}else{
			//可以分享到朋友圈
			Bitmap bitmap = getWxImage();
			
			WXImageObject wxImageObject = new WXImageObject(bitmap);
			
			WXMediaMessage wMessage = new WXMediaMessage();
			wMessage.mediaObject = wxImageObject;
			//图片描述
			wMessage.description = getWxText();
			//标题
			wMessage.title = "测试分享";

			//设置略缩图
			Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
			//回收bitmap,防止oom
			bitmap.recycle();
			wMessage.thumbData = WXUtil.bmpToByteArray(thumbBmp, true);
			SendMessageToWX.Req req = new SendMessageToWX.Req();
//			req.transaction = buildTransaction("img");
			//transaction字段用于唯一标识一个请求
			req.transaction = String.valueOf(System.currentTimeMillis());
			req.message = wMessage;
			/*
			 * 标识发送到哪里：
			 * 	SendMessageToWX.Req的scene成员，如果scene填WXSceneSession，
			 * 那么消息会发送至微信的会话内。如果scene填WXSceneTimeline（微信4.2以上支持，
			 * 如果需要检查微信版本支持API的情况， 可调用IWXAPI的getWXAppSupportAPI方法,0x21020001及以上支持发送朋友圈），
			 * 那么消息会发送至朋友圈。scene默认值为WXSceneSession。
			 */
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
			boolean result = api.sendReq(req);
			Log.d(WXShareActivity.class.getName(), result + "");
		}		
	}
}
