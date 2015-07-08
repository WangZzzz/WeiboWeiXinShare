package com.example.mtt.share;



import com.example.mtt.wb.utils.WBAccessTokenKeeper;
import com.example.mtt.wb.utils.MyWBConstants;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/*
 * 	通过第三方唤起微博客户端进行分享
 */
public class WBShareActivity extends Activity implements OnClickListener, IWeiboHandler.Response{

	
	private Button share_by_client;
	private Button share_bt;
	
	/** 微博微博分享接口实例 */
    private IWeiboShareAPI  mWeiboShareAPI = null;
    
    public static final int SHARE_CLIENT = 1;
    public static final int SHARE_ALL_IN_ONE = 2;
    
    private int mShareType = SHARE_ALL_IN_ONE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wbshare);
		initView();
		initData();
	}
	
	private void initView(){
		share_bt = (Button)findViewById(R.id.share_bt);
		share_by_client = (Button)findViewById(R.id.share_by_client);
		share_bt.setOnClickListener(this);
		share_by_client.setOnClickListener(this);
	}
	
	private void initData(){
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, MyWBConstants.APP_KEY);
		mWeiboShareAPI.registerApp(); // 将应用程序注册到微博客户端
	}
	
	/*
	 * 唤起客户端发博器进行分享
	 */
	private void sendMultiMessage() {
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage(); //初始化微博的分享消息
		weiboMessage.textObject = getTextObject();
		weiboMessage.imageObject = getImageObject();
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		request. transaction = String. valueOf(System. currentTimeMillis());
		request. multiMessage = weiboMessage;
		if(mShareType == SHARE_CLIENT){
			//通过微博客户端分享
			mWeiboShareAPI.sendRequest(WBShareActivity.this, request); //发送请求消息到微博，唤起微博分享界面
		}else if(mShareType == SHARE_ALL_IN_ONE){
            AuthInfo authInfo = new AuthInfo(this, MyWBConstants.APP_KEY, MyWBConstants.REDIRECT_URL, MyWBConstants.SCOPE);
            Oauth2AccessToken accessToken = WBAccessTokenKeeper.readAccessToken(getApplicationContext());
            String token = "";
            if (accessToken != null) {
                token = accessToken.getToken();
            }
            mWeiboShareAPI.sendRequest(this, request, authInfo, token, new WeiboAuthListener() {
                
                @Override
                public void onWeiboException( WeiboException e ) {
                	Log.d(this.getClass().getName(), "Error!!!!!");
                	Log.d(this.getClass().getName(), e.toString());
                }
                
                @Override
                public void onComplete( Bundle bundle ) {
                    // TODO Auto-generated method stub
                	Log.d(WBShareActivity.class.getName(), "onComplete1");
                    Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                    Log.d(WBShareActivity.class.getName(), "onComplete2");
                    WBAccessTokenKeeper.writeAccessToken(getApplicationContext(), newToken);
                    Log.d(WBShareActivity.class.getName(), "onComplete3");
//                	Toast.makeText(getApplicationContext(), "onAuthorizeComplete token = " + newToken.getToken(), 0).show();
                }
                
                @Override
                public void onCancel() {
                }
            });
		}else{
			return;
		}
	}
	
	private TextObject getTextObject(){
		TextObject textObject = new TextObject();
		textObject.text = "测试分享功能";
		return textObject;
	}
	
	private ImageObject getImageObject(){
		Log.d(this.getClass().getName(), "getImageObject");
		ImageObject imageObject = new ImageObject();
		Resources resources = getResources();
		imageObject.setImageObject(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher));
		return imageObject;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.share_by_client:
			mShareType = SHARE_CLIENT;
			sendMultiMessage();
			break;
		case R.id.share_bt:
			mShareType = SHARE_ALL_IN_ONE;
			sendMultiMessage();
			break;
		default:
			break;
		}
		
	}
	
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		mWeiboShareAPI.handleWeiboResponse(intent, WBShareActivity.this);
	}

	@Override
	public void onResponse(BaseResponse baseResp) {
		// TODO Auto-generated method stub
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			Toast.makeText(WBShareActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
			finish();
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			Toast.makeText(WBShareActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
			finish();
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			Toast.makeText(WBShareActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
			finish();
			break;
		default:
			break;
		}
		
	}
	

}
