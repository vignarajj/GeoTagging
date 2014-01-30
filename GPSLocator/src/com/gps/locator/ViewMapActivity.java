package com.gps.locator;

import java.io.IOException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.app.Activity;
import android.content.Intent;
import android.media.ExifInterface;
import android.os.Bundle;
import android.widget.Toast;

public class ViewMapActivity extends Activity
{
	GoogleMap googleMap;
	ExifInterface imageData;
	String attrLATITUDE,attrLATITUDE_REF,attrLONGITUDE, attrLONGITUDE_REF;
	Float Latitude, Longitude;
	boolean valid= false;
	LatLng points;
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewmap);
		try {
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}
		Intent i = getIntent();
		int position = i.getExtras().getInt("position");
		String[] filepath = i.getStringArrayExtra("filepath");
		String[] filename = i.getStringArrayExtra("filename");
		String fullFilepath= filepath[position];
		try {
			imageData = new ExifInterface(fullFilepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		attrLATITUDE = imageData.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
		attrLATITUDE_REF = imageData.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
		attrLONGITUDE = imageData.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
		attrLONGITUDE_REF = imageData.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
		if((attrLATITUDE !=null)
				&& (attrLATITUDE_REF !=null)
				&& (attrLONGITUDE != null)
				&& (attrLONGITUDE_REF !=null))
		{
			valid = true;

			if(attrLATITUDE_REF.equals("N")){
				Latitude = convertToDegree(attrLATITUDE);
			}
			else{
				Latitude = 0 - convertToDegree(attrLATITUDE);
			}

			if(attrLONGITUDE_REF.equals("E")){
				Longitude = convertToDegree(attrLONGITUDE);
			}
			else{
				Longitude = 0 - convertToDegree(attrLONGITUDE);
			}

		}
	}
	public String getLatitude(ExifInterface exif)
	{
		return exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
	}
	public String getLongitude(ExifInterface exif)
	{
		return exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
	}
	private void initilizeMap() {
		getMap();
		if (googleMap == null) {
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
		else{
			double lat= Double.valueOf(String.valueOf(Latitude));
			double longi= Double.valueOf(String.valueOf(Longitude));
			if(lat!=0.0 && longi!=0.0){
				points = new LatLng(lat, longi);
				googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points, 14));
				googleMap.addMarker(new MarkerOptions()
				.title("")
				.snippet("")
				.position(points));
			}
			else{
				Toast.makeText(ViewMapActivity.this, "Check the Location Settings", Toast.LENGTH_LONG).show();
			}
		}
	}
	public void getMap(){
		googleMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		googleMap.setMyLocationEnabled(true);
		googleMap.getUiSettings().setZoomControlsEnabled(true);
		googleMap.getUiSettings().setMyLocationButtonEnabled(true);
		googleMap.getUiSettings().setRotateGesturesEnabled(true);
	}
	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}
	//Convert Methods
	private Float convertToDegree(String stringDMS){
		Float result = null;
		String[] DMS = stringDMS.split(",", 3);
		String[] stringD = DMS[0].split("/", 2);
		Double D0 = new Double(stringD[0]);
		Double D1 = new Double(stringD[1]);
		Double FloatD = D0/D1;
		String[] stringM = DMS[1].split("/", 2);
		Double M0 = new Double(stringM[0]);
		Double M1 = new Double(stringM[1]);
		Double FloatM = M0/M1;
		String[] stringS = DMS[2].split("/", 2);
		Double S0 = new Double(stringS[0]);
		Double S1 = new Double(stringS[1]);
		Double FloatS = S0/S1;
		result = new Float(FloatD + (FloatM/60) + (FloatS/3600));
		return result;
	};
	public boolean isValid(){
		return valid;
	}
	public String toString() {
		// TODO Auto-generated method stub
		return (String.valueOf(Latitude)
				+ ", "
				+ String.valueOf(Longitude));
	}
	public int getLatitudeE6(){
		return (int)(Latitude*1000000);
	}
	public int getLongitudeE6(){
		return (int)(Longitude*1000000);
	}
}
