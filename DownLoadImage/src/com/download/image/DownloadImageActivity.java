package com.download.image;

import java.io.File;
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
public class DownloadImageActivity extends BaseActivity {

	private GridView imageGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_down_image);

		for (int i = 0; i < imageName.length; i++) {
			String filePath = getFullFileName(getExternalStoragePath(), imageName[i]);
			File f = new File(filePath);
			if(!f.exists()){
				new ImageStoreSDTask(imageName[i], imageLocation[i]).execute();	
			}else {
				Toast.makeText(DownloadImageActivity.this, "Image Already Exist", Toast.LENGTH_LONG).show();	
			}
		}
		
		imageGridView = (GridView) findViewById(R.id.imageGridView);

		imageGridView.setAdapter(new GridViewAdapter(
				DownloadImageActivity.this, imageName, imageLocation));
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

			Bitmap bm = loadBitmapFromExternalStorage(imageName);
			if(bm != null){
				holder.imageView.setImageBitmap(bm);
			}else {
				holder.imageView.setImageResource(R.drawable.loading);
			}
			holder.txtTitle.setText(imageName);
			return convertView;
		}

	}

	class ImageStoreSDTask extends AsyncTask<String, String, Bitmap> {

		private String imageName;
		private String imageLocation;
		
		public ImageStoreSDTask(String imageName, String imageLocation) {
			this.imageName = imageName;
			this.imageLocation = imageLocation;
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
				storeBitmapImageToExternalStorage(getResizedBitmap(result, 300, 400), imageName);
				Toast.makeText(DownloadImageActivity.this, "Downloaded:: "+imageName, Toast.LENGTH_LONG).show();
			}else {
				Toast.makeText(DownloadImageActivity.this, "Download Failed", Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(result);
		}
	}
}
