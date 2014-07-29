package com.download.image;

import java.util.ArrayList;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * @author dipenp
 * 
 *         Application level class, which will initialize when application load
 */
public class DownloadImageApp extends Application {

	private LruCache<String, Bitmap> mMemoryCache;
	private ArrayList<String> imageReqSent;

	@Override
	public void onCreate() {
		imageReqSent = new ArrayList<String>();
		initializeLruCache();

		super.onCreate();
	}

	/**
	 * Initializing LRU Cache, When application load
	 */
	private void initializeLruCache() {
		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}
		};

	}

	public LruCache<String, Bitmap> getmMemoryCache() {
		return mMemoryCache;
	}

	public void setmMemoryCache(LruCache<String, Bitmap> mMemoryCache) {
		this.mMemoryCache = mMemoryCache;
	}
	
	public ArrayList<String> getImageReqSent() {
		return imageReqSent;
	}

	public void setImageReqSent(ArrayList<String> imageReqSent) {
		this.imageReqSent = imageReqSent;
	}
}
