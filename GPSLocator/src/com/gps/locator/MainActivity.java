package com.gps.locator;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
public class MainActivity extends Activity {
	Button camera,gallery;
	final static int CAMERA_PIC_REQUEST=1001;
	final static int LOCATION_ENABLE_REQUEST=1002;
	double geo_latitude,geo_longitude;
	Location currentBestLocation;
	LocationManager locationManager;
	String filename;
	SimpleDateFormat fmt ;
	static Uri capturedImageUri=null;
	File file;
	LocationHelper myLocationHelper;
	boolean gotLocation;
	LocationWorker locationTask;
	List<String> locationProviders;
	ProgressDialog pd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		camera =(Button)findViewById(R.id.camera);
		gallery= (Button)findViewById(R.id.gallery);
		myLocationHelper = new LocationHelper(this);
		locationTask = new LocationWorker();
		locationProviders = myLocationHelper.allproviders();
		if(checkState()==true)
		{
			locationTask .execute(new Boolean[] {true});
		}
		File folder = new File(Environment.getExternalStorageDirectory() + "/photosimg");
		if(folder.exists() == false){
			folder.mkdirs();
		}
		fmt = new SimpleDateFormat("yyyyMMdd_HHmmss",java.util.Locale.getDefault());
		filename = fmt.format(new Date());
		camera.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				openCamera();
			}
		});
		gallery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i= new Intent(MainActivity.this, GridActivity.class);
				startActivity(i);
			}
		});
	}
	public void openCamera()
	{
		Calendar cal = Calendar.getInstance();
		file = new File(Environment.getExternalStorageDirectory()+"/photosimg",  (cal.getTimeInMillis()+".jpg"));
		capturedImageUri = Uri.fromFile(file);
		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,capturedImageUri);
		startActivityForResult(intent, CAMERA_PIC_REQUEST);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == CAMERA_PIC_REQUEST) {
			if(resultCode==Activity.RESULT_OK){
				Log.i("Camera", "Image Captured");
				System.out.println("Latitude : "+String.valueOf(geo_latitude));
				System.out.println("Longitude : "+String.valueOf(geo_longitude));
				geoTag(capturedImageUri.getPath(),geo_latitude,geo_longitude);
			}
			else if (requestCode == Activity.RESULT_CANCELED){
				Log.i("Camera", "Cancelled");
			}
			else{
				Log.i("Camera", "Failed");
			}
		} 
		else if(requestCode==LOCATION_ENABLE_REQUEST)
		{
			if(checkState()==true)
			{
				locationTask.execute(new Boolean[] {true});
			}
		}
	}
	public void geoTag(String filename, double latitude, double longitude){
		ExifInterface exif;
		try {
			exif = new ExifInterface(filename);
			int num1Lat = (int)Math.floor(latitude);
			int num2Lat = (int)Math.floor((latitude - num1Lat) * 60);
			double num3Lat = (latitude - ((double)num1Lat+((double)num2Lat/60))) * 3600000;
			int num1Lon = (int)Math.floor(longitude);
			int num2Lon = (int)Math.floor((longitude - num1Lon) * 60);
			double num3Lon = (longitude - ((double)num1Lon+((double)num2Lon/60))) * 3600000;
			exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, num1Lat+"/1,"+num2Lat+"/1,"+num3Lat+"/1000");
			exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, num1Lon+"/1,"+num2Lon+"/1,"+num3Lon+"/1000");
			if (latitude > 0) {
				exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N"); 
			} else {
				exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
			}
			if (longitude > 0) {
				exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");    
			} else {
				exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
			}
			exif.saveAttributes();
		}
		catch (IOException e) {
			Log.e("PictureActivity", e.getLocalizedMessage());
		} 
	}
	class LocationWorker extends AsyncTask<Boolean, Integer, Boolean> {
		Context context;
		@Override
		protected void onPreExecute() {
			pd= new ProgressDialog(MainActivity.this);
			pd.setMessage("Wait for fetching location");
			pd.setCancelable(false);
			pd.show();
		}       
		@Override
		protected void onPostExecute(Boolean result) {
			geo_latitude= myLocationHelper.getLat();
			geo_longitude= myLocationHelper.getLong();
			pd.dismiss();
		}
		@Override
		protected Boolean doInBackground(Boolean... params) {
			while(myLocationHelper.gotLocation() == false){}
			return true;
		}
	}
	public boolean checkState()
	{
		if(locationProviders.contains("gps") && !myLocationHelper.CheckGPS()){
			new AlertDialog.Builder(this)
			.setMessage("GPS is Not Enabled")
			.setPositiveButton("Enable GPS",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int id) {
					startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_ENABLE_REQUEST);
				}
			})
			.setNegativeButton("Close",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int id) {
					dialog.cancel();
				}
			}).create().show();
			return false;
		} 
		else if (!locationProviders.contains("gps") && locationProviders.contains("network")){
			if(!myLocationHelper.CheckNetwork()){
				new AlertDialog.Builder(this)
				.setMessage("Enable Location Services")
				.setPositiveButton("Enable",
						new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog,int id){
						startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_ENABLE_REQUEST);
					}
				})
				.setNegativeButton("Close",
						new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog,
							int id) {
						dialog.cancel();
					}
				}).create().show();
				return false;
			}
			else{
				new AlertDialog.Builder(this)
				.setMessage("This is not GPS Enhanced Device, Only use network so captured location has not been accurate")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener(){	
					@Override
					public void onClick(DialogInterface dialog, int arg1){
						dialog.dismiss();
					}
				}).create().show();
				return true;
			}
		}
		else if (!locationProviders.contains("gps") && !locationProviders.contains("network")){
			new AlertDialog.Builder(this)
			.setMessage("No Location Services in this device")
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).create().show();
			return false;
		}
		else{
			return true;
		}
	}
	public void onStop(){
		super.onStop();
		myLocationHelper.killLocationServices();
	}
	public void onDestroy(){
		super.onDestroy();
		myLocationHelper.killLocationServices();
	}
}
