package com.example.mtt.share;

import com.example.mtt.wb.utils.MyWBConstants;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

/**
 * 
 * @author WangZ
 * 微博分享完成后回调的Activity
 *
 */
public class WBShareReqActivity extends Activity implements IWeiboHandler.Response{

	
	private IWeiboShareAPI  mWeiboShareAPI = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_wbshare_req);
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, MyWBConstants.APP_KEY);
		mWeiboShareAPI.handleWeiboResponse(getIntent(), WBShareReqActivity.this);
	}

	@Override
	public void onResponse(BaseResponse baseResp) {
		// TODO Auto-generated method stub
		Log.d(AllInOneShareActivity.class.getName(), "onResponse");
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
			finish();
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			Toast.makeText(this, "取消分享", Toast.LENGTH_SHORT).show();
			finish();
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
			finish();
			break;
		default:
			break;
		}
	}
}
