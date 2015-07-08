package com.example.mtt.share;


import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.mtt.share.R;


import com.example.mtt.wb.utils.WBAccessTokenKeeper;
import com.example.mtt.wb.utils.MyWBConstants;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*	微博授权
 * 	使用两种授权方式，当有客户端时，使用SSO授权，当没有客户端时，使用web授权
 */
public class WBAuthActivity extends Activity implements OnClickListener{

	private Button auth_bt;
	private Button login_bt;
	private Button clear_bt;
	private TextView token_tv;
	
	private AuthInfo authInfo;
	private Oauth2AccessToken mAccessToken;
	private SsoHandler mSsoHandler;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wbauth);
		initView();
		initData();
	}
	
	/*
	 * 	初始化各种组件
	 */
	private void initView(){
		auth_bt = (Button)findViewById(R.id.auth_bt);
		login_bt = (Button)findViewById(R.id.login_bt);
		clear_bt = (Button)findViewById(R.id.clear_bt);
		token_tv = (TextView)findViewById(R.id.token_tv);
		
		auth_bt.setOnClickListener(this);
		login_bt.setOnClickListener(this);
		clear_bt.setOnClickListener(this);
	}
	
	private void initData(){
		 authInfo = new AuthInfo(this, MyWBConstants.APP_KEY, MyWBConstants.REDIRECT_URL, MyWBConstants.SCOPE);
	     mSsoHandler = new SsoHandler(WBAuthActivity.this, authInfo);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.login_bt:
			/*
			 * 查询SharedPrefrence里面有没有token信息
			 */
			mAccessToken = WBAccessTokenKeeper.readAccessToken(WBAuthActivity.this);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			Log.d(this.getClass().getName(),sdf.format(new Date(mAccessToken.getExpiresTime())));
			Log.d(this.getClass().getName(), sdf.format(new Date(System.currentTimeMillis())));
			/*
			 * 说明在过期时间内
			 */
			if(mAccessToken.getExpiresTime() > System.currentTimeMillis()){
				updateTokenView(true);
				Intent intent = new Intent(this, WBShareActivity.class);
				startActivity(intent);
				finish();
			}else{
				Toast.makeText(this, "请先授权", Toast.LENGTH_SHORT).show();
				return;
			}			
			break;
		case R.id.auth_bt:
			 mSsoHandler.authorize(new AuthListener());
			 break;
		case R.id.clear_bt:
			/*
			 *	清除token信息
			 */
			WBAccessTokenKeeper.clear(WBAuthActivity.this);
			token_tv.setText("");
			break;
		default:
			break;
		}
	}
		
	/*
	 * 	微博认证授权回调类
	 * 	当授权成功后，请保存access_token、expires_in、uid 等信息到 SharedPreferences中
	 */
	class AuthListener implements WeiboAuthListener{

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			Toast.makeText(WBAuthActivity.this, 
	                   "取消授权", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onComplete(Bundle bundle) {
			// TODO Auto-generated method stub
			//从Bundle中解析出Token信息
			mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
			//获取用户输入的 电话号码信息 
			 String  phoneNum =  mAccessToken.getPhoneNum();
			 if (mAccessToken.isSessionValid()) {
	                // 显示 Token
	                updateTokenView(false);
	                // 保存 Token到SharedPreferences
	                WBAccessTokenKeeper.writeAccessToken(WBAuthActivity.this, mAccessToken);
	                Toast.makeText(WBAuthActivity.this, 
	                        "授权成功", Toast.LENGTH_SHORT).show();
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
	                Toast.makeText(WBAuthActivity.this, message, Toast.LENGTH_LONG).show();
	            }
		}

		@Override
		public void onWeiboException(WeiboException e) {
			// TODO Auto-generated method stub
			Toast.makeText(WBAuthActivity.this, 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();		
		}
		
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // SSO 授权回调
        // 重要：SSO 登陆,Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        
    }
	
	/*
	 * 授权完成后，更新token信息
	 */
	private void updateTokenView(boolean hasExisted) {
		String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
				new java.util.Date(mAccessToken.getExpiresTime()));
		String format = "Token：%1$s \n有效期：%2$s";
	    token_tv.setText(String.format(format, mAccessToken.getToken(), date));
	    
	    String message = String.format(format, mAccessToken.getToken(), date);
	    if (hasExisted) {
	    	message = "Token 仍在有效期内，无需再次登录。" + "\n" + message;
	    }
	    token_tv.setText(message);
	}
}
