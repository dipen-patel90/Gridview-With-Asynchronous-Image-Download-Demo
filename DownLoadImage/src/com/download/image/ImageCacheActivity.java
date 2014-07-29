package com.download.image;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author dipenp
 *
 */
public class ImageCacheActivity extends BaseActivity {

	private GridView imageGridView;
	private LruCache<String, Bitmap> mMemoryCache;
	private GridViewAdapter gridViewAdapter;
	/*For test:: checking how many calls are made for download request*/
	private int imageDwldCallCount = 1;
	/*For test:: checking how many image are actually downloded*/
	private int imageDownloadedCount = 0;
	/*Maintaining arraylist of image name whose request is send for download, so that we will not send same image request multiple time, 
		and removing name if download failed, so that we can make request again*/  
//	private ArrayList<String> imageReqSent;
	private DownloadImageApp downloadImageApp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_cache_layout);

		downloadImageApp = (DownloadImageApp)getApplicationContext();
//		imageReqSent = new ArrayList<String>();
		
		mMemoryCache = downloadImageApp.getmMemoryCache();

		imageGridView = (GridView) findViewById(R.id.imageGridView);
		gridViewAdapter = new GridViewAdapter(ImageCacheActivity.this, imageName, imageLocation);
		imageGridView.setAdapter(gridViewAdapter);
	}

	class GridViewAdapter extends BaseAdapter {

		private Context context;
		private String[] imageNameArray;
		private String[] imageLocationArray;

		public GridViewAdapter(Context context, String[] imageNames,
				String[] imageLocation) {
			this.context = context;
			this.imageNameArray = imageNames;
			this.imageLocationArray = imageLocation;
		}

		private class ViewHolder {
			ImageView imageView;
			TextView txtTitle;
		}

		@Override
		public int getCount() {
			return imageNameArray.length;
		}

		@Override
		public Object getItem(int position) {
			return imageNameArray[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.gridview_layout, null);
				holder = new ViewHolder();
				holder.txtTitle = (TextView) convertView
						.findViewById(R.id.capabilitiesTextView);
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.capabilitiesImgView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			String imageName = imageNameArray[position];

			holder.txtTitle.setText(imageName);

			loadBitmap(imageName, imageLocationArray[position],
					holder.imageView);
			return convertView;
		}

	}

	class CacheImageTask extends AsyncTask<String, String, Bitmap> {

		private String imageName;
		private String imageLocation;
		private ImageView imageView;

		public CacheImageTask(String imageName, String imageLocation, ImageView imageView) {
			this.imageName = imageName;
			this.imageLocation = imageLocation;
			this.imageView = imageView;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Bitmap doInBackground(String... params) {

			DefaultHttpClient dfaultHttpClient = new DefaultHttpClient();
			HttpGet getRequest = new HttpGet(BASE_IMAGE_URL + imageLocation);

			try {

				HttpResponse response = dfaultHttpClient.execute(getRequest);

				// check 200 OK for success
				int statusCode = response.getStatusLine().getStatusCode();

				if (statusCode != HttpStatus.SC_OK) {
					return null;
				}

				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream inputStream = null;
					try {
						// getting contents from the stream
						inputStream = entity.getContent();

						Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

						return bitmap;
					} finally {
						if (inputStream != null) {
							inputStream.close();
						}
						entity.consumeContent();
					}
				}
			} catch (Exception e) {
				getRequest.abort();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				addBitmapToMemoryCache(imageName,
						getResizedBitmap(result, 300, 400));
				imageView.setImageBitmap(getBitmapFromMemCache(imageName));
				imageDownloadedCount = imageDownloadedCount + 1;
			}else {
				downloadImageApp.getImageReqSent().remove(imageName);
			}
			 Toast.makeText(ImageCacheActivity.this, "Image Download Call Count::: " + imageDwldCallCount+ " downloaded "+imageDownloadedCount, Toast.LENGTH_LONG).show();
			 imageDwldCallCount = imageDwldCallCount + 1;
			super.onPostExecute(result);
		}
	}

	// public void loadBitmap(int resId, ImageView imageView) {
	// final String imageKey = String.valueOf(resId);
	public void loadBitmap(String imageName, String imageLocation,
			ImageView imageView) {
		final String imageKey = imageName;
		
		final Bitmap bitmap = getBitmapFromMemCache(imageKey);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			imageView.setImageResource(R.drawable.loading);
			if(!downloadImageApp.getImageReqSent().contains(imageKey)){
				downloadImageApp.getImageReqSent().add(imageKey);

				new CacheImageTask(imageName, imageLocation, imageView).execute();
			}
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}
}
