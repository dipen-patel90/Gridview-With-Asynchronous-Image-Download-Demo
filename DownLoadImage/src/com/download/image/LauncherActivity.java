package com.download.image;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author dipenp
 *
 */
public class LauncherActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);
		
		((Button)findViewById(R.id.image_cache_demo_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LauncherActivity.this, ImageCacheActivity.class));
			}
		});
		
		((Button)findViewById(R.id.image_sd_store_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LauncherActivity.this, DownloadImageActivity.class));
			}
		});
	}
}
