package com.gps.locator;

import java.io.File;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GridActivity extends Activity
{	
	public class ImageAdapter extends BaseAdapter {

		private Context mContext;
		ArrayList<String> itemList = new ArrayList<String>();

		public ImageAdapter(Context c) {
			mContext = c; 
		}

		void add(String path){
			itemList.add(path); 
		}

		@Override
		public int getCount() {
			return itemList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {  // if it's not recycled, initialize some attributes
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
				imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				imageView.setPadding(8, 8, 8, 8);
				imageView.setBackgroundColor(Color.WHITE);
			} else {
				imageView = (ImageView) convertView;
			}

			Bitmap bm = decodeSampledBitmapFromUri(itemList.get(position), 150, 150);

			imageView.setImageBitmap(bm);
			return imageView;
		}

		public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {

			Bitmap bm = null;
			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);       
			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			bm = BitmapFactory.decodeFile(path, options); 	       
			return bm;   
		}  
		public int calculateInSampleSize(

				BitmapFactory.Options options, int reqWidth, int reqHeight) {
			// Raw height and width of image
			final int height = options.outHeight;
			final int width = options.outWidth;
			int inSampleSize = 1;

			if (height > reqHeight || width > reqWidth) {
				if (width > height) {
					inSampleSize = Math.round((float)height / (float)reqHeight);    
				} else {
					inSampleSize = Math.round((float)width / (float)reqWidth);    
				}   
			}
			return inSampleSize;    
		}
	}
	private String[] FilePathStrings;
	private String[] FileNameStrings;
	String targetPath;
	GridView grid;
	GridViewAdapter adapter;
	File file;
	ImageAdapter adapter1;
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grid_layout);
		grid = (GridView) findViewById(R.id.gridview);
		adapter1= new ImageAdapter(this);
		grid.setAdapter(adapter1);
		System.out.println("Error on Grid View Activity");
		String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		String targetPath = ExternalStorageDirectoryPath + "/photosimg/";
		File targetDirector = new File(targetPath);
		File[] files = targetDirector.listFiles();
		for (File file : files){
			adapter1.add(file.getAbsolutePath());
		} 
		FilePathStrings= new String[files.length];
		FileNameStrings= new String[files.length];
		for(int i=0;i<files.length;i++)
		{
			FilePathStrings[i]= files[i].getAbsolutePath();
			FileNameStrings[i] = files[i].getName();
		}
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent i = new Intent(GridActivity.this, ViewMapActivity.class);
				i.putExtra("filepath", FilePathStrings);
				i.putExtra("filename", FileNameStrings);
				i.putExtra("position", arg2);
				startActivity(i);
			}
		});
	}
}
