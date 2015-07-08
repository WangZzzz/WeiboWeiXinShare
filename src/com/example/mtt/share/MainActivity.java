package com.example.mtt.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;

public class MainActivity extends Activity implements OnClickListener {

	private Button weibo_share;
	private Button weichat_share;
	private Button pop_share;
	private Button wx_auth;

	private PopupWindow window;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		weibo_share = (Button) findViewById(R.id.weibo_share);
		weichat_share = (Button) findViewById(R.id.weichat_share);
		pop_share = (Button) findViewById(R.id.pop_share);
		wx_auth = (Button) findViewById(R.id.wx_auth);
		weibo_share.setOnClickListener(this);
		weichat_share.setOnClickListener(this);
		pop_share.setOnClickListener(this);
		wx_auth.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.weibo_share:
			Intent intent = new Intent(this, WBAuthActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.weichat_share:
			Intent intent2 = new Intent(this, WXShareActivity.class);
			startActivity(intent2);
			// finish();
			break;
		case R.id.pop_share:
			Intent intent3 = new Intent(MainActivity.this, NaviActivity.class);
			startActivity(intent3);
			finish();
			break;
		case R.id.wx_auth:
			Intent intent4 = new Intent(MainActivity.this, WXAuthActivity.class);
			startActivity(intent4);
			finish();
		default:
			break;
		}
	}
}
