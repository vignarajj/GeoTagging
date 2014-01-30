package com.gps.locator;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GridViewAdapter extends BaseAdapter {

	private Context mContext;
    ArrayList<String> itemList = new ArrayList<String>();
	// Declare variables
	private Activity activity;
	private String[] filepath;
//	private String[] filename;
	private static LayoutInflater inflater = null;

	public GridViewAdapter(Activity a, String[] fpath, String[] fname) {
		activity = a;
		filepath = fpath;
//		filename = fname;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}
	public GridViewAdapter(Context c) {
	      mContext = c; 
	     }
	void add(String path){
	      itemList.add(path); 
	     }
	public int getCount() {
		return filepath.length;

	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(220, 220));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        Bitmap bm = decodeSampledBitmapFromUri(itemList.get(position), 220, 220);

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