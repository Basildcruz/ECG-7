package com.guo.ecg.client;

import com.baidu.location.*;

import android.app.Application;
import android.util.Log;
import android.widget.TextView;
import android.os.Process;
import android.os.Vibrator;

public class Location extends Application {

	public LocationClient mLocationClient = null;
//	public LocationClient locationClient = null;
//	public LocationClient LocationClient = null;
	private String mData;  
	public MyLocationListenner myListener = new MyLocationListenner();
//	public MyLocationListenner listener = new MyLocationListenner();
//	public MyLocationListenner locListener = new MyLocationListenner();
	public TextView mTv;
	public NotifyLister mNotifyer=null;
	public Vibrator mVibrator01;
	public static String TAG = "LocTestDemo";
	
	@Override
	public void onCreate() {
		mLocationClient = new LocationClient( this );
//		locationClient = new LocationClient( this );
//		LocationClient = new LocationClient( this );
		mLocationClient.registerLocationListener( myListener );
//		locationClient.registerLocationListener( listener );
//		LocationClient.registerLocationListener( locListener );
		//λ��������ش���
//		mNotifyer = new NotifyLister();
//		mNotifyer.SetNotifyLocation(40.047883,116.312564,3000,"gps");//4����������Ҫλ�����ѵĵ�����꣬���庬������Ϊ��γ�ȣ����ȣ����뷶Χ������ϵ����(gcj02,gps,bd09,bd09ll)
//		mLocationClient.registerNotify(mNotifyer);
		
		super.onCreate(); 
		Log.d(TAG, "... Application onCreate... pid=" + Process.myPid());
	}
	
	/**
	 * ��ʾ�ַ���
	 * @param str
	 */
	public void logMsg(String str) {
		try {
			mData = str;
			if ( mTv != null )
				mTv.setText(mData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��������������λ�õ�ʱ�򣬸�ʽ�����ַ������������Ļ��
	 */
	public class MyLocationListenner implements BDLocationListener {
		
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return ;
			StringBuffer sb = new StringBuffer(1000);
//			sb.append("ʱ��: ");
//			sb.append(location.getTime());
//			sb.append("\nerror code : ");
//			sb.append(location.getLocType());
//			sb.append("\n����: ");
//			sb.append(location.getLatitude());
//			sb.append("\nγ��: ");
//			sb.append(location.getLongitude());
//			sb.append("\nradius : ");
//			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append("\n"+location.getCity());
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
//				sb.append("\nʡ��");
//				sb.append(location.getProvince());
//				sb.append("\n�У�");
//				sb.append(location.getCity());
//				sb.append("\n��/�أ�");
//				sb.append(location.getDistrict());
//				sb.append("��ַ: ");
//				sb.append(location.getAddrStr());
			}
			sb.append("http://api.map.baidu.com/geocoder?location="+location.getLatitude()+","+location.getLongitude()+"&coord_type=gcj02&output=html");
			logMsg(sb.toString());
//			sb.append("\nsdk version : ");
//			sb.append(mLocationClient.getVersion());
//			sb.append("\nisCellChangeFlag : ");
//			sb.append(location.isCellChangeFlag());
//			Log.i(TAG, sb.toString());
		}
		
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null){
				return ; 
			}
			StringBuffer sb = new StringBuffer(1000);
			sb.append("ʱ��: ");
			sb.append(poiLocation.getTime());
			sb.append("\n����: ");
			sb.append(poiLocation.getLatitude());
			sb.append("\nγ�� : ");
			sb.append(poiLocation.getLongitude());
			if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\n��ַ : ");
				sb.append(poiLocation.getAddrStr());
			} 
			sb.append("http://api.map.baidu.com/geocoder?location="+poiLocation.getLatitude()+","+poiLocation.getLongitude()+"&coord_type=gcj02&output=html");
//			if(poiLocation.hasPoi()){
//				sb.append("\nPoi:");
//				sb.append(poiLocation.getPoi());
//			}else{				
//				sb.append("noPoi information");
//			}
			logMsg(sb.toString());
		}
	}
	
	public class NotifyLister extends BDNotifyListener{
		public void onNotify(BDLocation mlocation, float distance){
			mVibrator01.vibrate(1000);
		}
	}
}
