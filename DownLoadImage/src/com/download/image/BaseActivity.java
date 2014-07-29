package com.download.image;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

/**
 * @author dipenp
 *
 */
public class BaseActivity extends Activity {

	public String BASE_IMAGE_URL = "https://res.cloudinary.com/dekkcvxdu/image/upload/";
	public String IMAGE_FOLDER = "downloadimage";

	public String[] imageName = { "Image1", "Image2", "Image3", "Image4",
			"Image5", "Image6", "Image7", "Image8", "Image9", "Image10",
			"Image11", "Image12", "Image13" };

	public String[] imageLocation = { "v1406387371/Yami/yami2.jpg",
			"v1406387369/Yami/yami3.jpg", "v1406387425/Yami/yami4.jpg",
			"v1406387413/Yami/yami5.jpg", "v1406387397/Yami/yami6.jpg",
			"v1406387417/Yami/yami7.jpg", "v1406387434/Yami/yami8.jpg",
			"v1406387425/Yami/yami9.jpg", "v1406387424/Yami/yami10.jpg",
			"v1406387444/Yami/yami11.jpg", "v1406387441/Yami/yami12.jpg",
			"v1406387467/Yami/yami13.jpg", "v1406387450/Yami/yami14.jpg" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

		int width = bm.getWidth();

		int height = bm.getHeight();

		float scaleWidth = ((float) newWidth) / width;

		float scaleHeight = ((float) newHeight) / height;

		// create a matrix for the manipulation
		Matrix matrix = new Matrix();

		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);

		return resizedBitmap;

	}

	public boolean storeBitmapImageToExternalStorage(Bitmap imageData,
			String filename) {
		// get path to external storage (SD card)
		String externalStoragePath = getExternalStoragePath();

		try {
			File sdIconStorageDir = new File(externalStoragePath);

			if (!sdIconStorageDir.exists()) {
				// create storage directories, if they don't exist
				sdIconStorageDir.mkdirs();
			}

			String filePath = getFullFileName(externalStoragePath, filename);
			FileOutputStream fileOutputStream = new FileOutputStream(filePath);

			BufferedOutputStream bos = new BufferedOutputStream(
					fileOutputStream);

			// choose another format if PNG doesn't suit you
			imageData.compress(CompressFormat.JPEG, 100, bos);

			bos.flush();
			bos.close();

		} catch (FileNotFoundException e) {
			Log.w("TAG", "Error saving image file: " + e.getMessage());
			return false;
		} catch (IOException e) {
			Log.w("TAG", "Error saving image file: " + e.getMessage());
			return false;
		}

		return true;
	}

	public String getFullFileName(String externalStoragePath, String filename) {
		return externalStoragePath + filename + "."+CompressFormat.JPEG;
	}

	public Bitmap loadBitmapFromExternalStorage(String filename) {
		try {
			String filepath = getFullFileName(getExternalStoragePath(), filename);
			File f = new File(filepath);
			if (!f.exists()) {
				return null;
			}
			Bitmap tmp = BitmapFactory.decodeFile(filepath);
			return tmp;
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getExternalStoragePath(){
		// get path to external storage (SD card)
		String externalStoragePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/" + IMAGE_FOLDER + "/";
		return externalStoragePath;
	}
}

