package com.example.mtt.share;

import android.R.integer;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NaviActivity extends Activity implements OnClickListener{

	private Button navi_wx_moments;
	private Button navi_wx_frients;
	private Button navi_wb;
	
	private Bitmap bitmap;
	private String text;
	private int type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navi);
		initView();
		initData();
	}
	
	private void initView(){
		navi_wx_frients = (Button)findViewById(R.id.navi_wx_friends);
		navi_wx_moments = (Button)findViewById(R.id.navi_wx_moments);
		navi_wb = (Button)findViewById(R.id.navi_wb);
		
		navi_wx_moments.setOnClickListener(this);
		navi_wx_frients.setOnClickListener(this);
		navi_wb.setOnClickListener(this);
	}

	private void initData(){
		Resources resources = getResources();
		bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher);
		text = "测试分享功能";
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.navi_wb:
			type = 0;
			Intent intent1 = new Intent(this, AllInOneShareActivity.class);
			Bundle bundle1 = new Bundle();
			bundle1.putInt("type", type);
			bundle1.putString("text", text);
			bundle1.putParcelable("image", bitmap);
			intent1.putExtras(bundle1);
			startActivity(intent1);
//			finish();
			break;
		case R.id.navi_wx_friends:
			type = 1;
			Intent intent2 = new Intent(this, AllInOneShareActivity.class);
			Bundle bundle2 = new Bundle();
			bundle2.putInt("type", type);
			bundle2.putString("text", text);
			bundle2.putParcelable("image", bitmap);
			intent2.putExtras(bundle2);
			startActivity(intent2);
//			finish();
			break;
		case R.id.navi_wx_moments:
			type = 2;
			Intent intent3 = new Intent(this, AllInOneShareActivity.class);
			Bundle bundle3 = new Bundle();
			bundle3.putInt("type", type);
			bundle3.putString("text", text);
			bundle3.putParcelable("image", bitmap);
			intent3.putExtras(bundle3);
			startActivity(intent3);
//			finish();
			break;
		default:
			break;
		}
	}

}
