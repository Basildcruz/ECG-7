package com.pku.wcsp.ecg;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class BMService extends Service{
	private String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
	IntentFilter filter = new IntentFilter();
	private BroadcastReceiver mReceiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	@Override
	public void onCreate(){

		mReceiver= new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				if(BluetoothDevice.ACTION_FOUND.equals(action)){
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					if((device != null)&&(device.getName() != null)&&(device.getName().equals("ECG-BLUE1"))){
						Message msg = BluetoothManager.handler.obtainMessage();
						msg.obj = device;
						BluetoothManager.handler.sendMessage(msg);
						Log.v("Service","��Ӧ�㲥");
					}
				}
				if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")){
						Log.i("ȡ�����", "��ʼ");
						BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
						try{
							ClsUtils.setPin(btDevice.getClass(), btDevice, "1234");
							ClsUtils.createBond(btDevice.getClass(), btDevice);
							ClsUtils.cancelPairingUserInput(btDevice.getClass(), btDevice);
							Log.i("ȡ�����", "ȡ����Գɹ�");
						}catch (Exception e){
							e.printStackTrace(); 
						}
				}
			}
		};
	}
	
	@Override
	public void onDestroy(){
		unregisterReceiver(mReceiver);
		Log.i("Service:onDestroy", "���ע��");
	}
	
	@Override
	public void onStart(Intent intent, int startid){
		
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(ACTION_PAIRING_REQUEST);
        registerReceiver(mReceiver,filter);
        Log.i("Service:onCreate", "ע��ɹ�");
        Log.i("Service:onStart", "����service");
	}

}
