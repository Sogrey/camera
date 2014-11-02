package org.sogrey.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Administrator
 * 
 */
public class Main extends Activity {
	/** SD卡路径 */
	public final static String PATH_SDCARD = Environment
			.getExternalStorageDirectory().getPath();
	private static final int CAMERA_REQUEST = 1888;
	private ImageView imageView;
	private TextView txt;
	private Button photoButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.imageView = (ImageView) this.findViewById(R.id.imageView1);
		 txt= (TextView) this.findViewById(R.id.textView1);
		 photoButton = (Button) this.findViewById(R.id.button1);
		photoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, CAMERA_REQUEST);
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
			final Bitmap photo = (Bitmap) data.getExtras().get("data");
			imageView.setImageBitmap(photo);
			new Thread(new Runnable() {

				@Override
				public void run() {
//					// TODO Auto-generated method stub
					Message logMsg = handler.obtainMessage();
					saveBitmapToSDCard(PATH_SDCARD+"/tmp/" , "pic", photo);
					logMsg.what = 1001;
					logMsg.sendToTarget();
				}
			}).start();
		}
	}

	/**
	 * 保存题目图片到SDCARD
	 * 
	 * @param path
	 * @param bitName
	 * @param mBitmap
	 */
	public static void saveBitmapToSDCard(String path, String bitName,
			Bitmap mBitmap) {
		File f = new File(path);
//		File f = null ;
		try {
			 if (f.exists()) {
			 f.delete();
			 }
			if (!f.exists()) {
				f.mkdirs();
			}

			f = new File(path + bitName + ".png");
			if (!f.isFile()) {
				f.createNewFile();
			}
		} catch (Exception e) {
			Log.e("", "创建文件时出错：" + e.toString());
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			txt.setText("保存路径："+PATH_SDCARD+"/tmp/pic.png" );
		}
	};
}
