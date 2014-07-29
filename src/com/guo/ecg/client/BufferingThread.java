package com.guo.ecg.client;

import java.util.Iterator;
import java.util.LinkedList;

import com.pku.wcsp.ecg.AlarmMessage;
import com.pku.wcsp.ecg.BluetoothManager;
import com.pku.wcsp.ecg.ECGdata;
import com.pku.wcsp.ecg.ECGdata_12;

import android.os.Message;
import android.util.Log;

public class BufferingThread extends Thread {
	
	private BluetoothManager manager;
	public static CycleByteQueue buffer,buffer2,buffer3,buffer4,buffer5,buffer6,buffer7;
	public static CycleIntQueue heartRateBuffer;
	public static boolean BufferingTag;
	public static int LEAD;
	int leadNumber;
	
	
	public BufferingThread(BluetoothManager manager, byte[] buffer, int leadNumber){
		this.manager = manager;
		BufferingTag = true;
		LEAD = 1;
		this.buffer = new CycleByteQueue(buffer);
		heartRateBuffer = new CycleIntQueue(100);
		this.leadNumber = leadNumber;
	}
	
	
	public void run(){
		if(leadNumber == 7){
			while(true && BufferingTag){
				ECGdata tmpData = manager.getECGdata();
				byte[] lead;
				int heartRate;
				int alarmType;
				if(tmpData != null){
					lead = tmpData.getData(LEAD);

					buffer.addBytes(lead);
					
					printInfo("导联1:",lead);
					heartRate = tmpData.getHeartRate();
					heartRateBuffer.addInt(heartRate);
					alarmType = tmpData.getECGalarm().type;
					LinkedList<AlarmMessage> alarms = tmpData.getAlarms();
					if(alarms.size()!=0){
						Iterator<AlarmMessage> iterator = alarms.iterator();
						while(iterator.hasNext()){
							AlarmMessage alarm = iterator.next();
							Message msg = NewClientActivity.alarmHandler.obtainMessage();
							msg.what = alarm.type;
							NewClientActivity.alarmHandler.sendMessage(msg);
							
							if(alarm.type == 1 || alarm.type == 2 || alarm.type == 3 || alarm.type == 4){
								Message msg3 = NewClientActivity.alarmHandler.obtainMessage();
								msg3.what = 11;
								NewClientActivity.alarmHandler.sendMessage(msg3);
							}else if(alarm.type == 5 || alarm.type == 6 || alarm.type == 9){
								Message msg2 = NewClientActivity.alarmHandler.obtainMessage();
								msg2.what = 14;
								NewClientActivity.alarmHandler.sendMessage(msg2);
							}
						}
					}else{
						Message msg = NewClientActivity.alarmHandler.obtainMessage();
						msg.what = 10;
						NewClientActivity.alarmHandler.sendMessage(msg);
						Message msg2 = NewClientActivity.alarmHandler.obtainMessage();
						msg2.what = 15;
						NewClientActivity.alarmHandler.sendMessage(msg2);
					}
				}else{
				}
			}
		}else if(leadNumber == 12){
			while(true && BufferingTag){
				ECGdata_12 tmpData = manager.getECGdata_12();
				byte[] lead;
				int heartRate;
				int alarmType;
				if(tmpData != null){
					lead = tmpData.getData(LEAD);

					buffer.addBytes(lead);
					
					printInfo("导联:",lead);
					heartRate = tmpData.getHeartRate();
					heartRateBuffer.addInt(heartRate);
					alarmType = tmpData.getECGalarm().type;
					LinkedList<AlarmMessage> alarms = tmpData.getAlarms();
					if(alarms.size()!=0){
						Iterator<AlarmMessage> iterator = alarms.iterator();
						while(iterator.hasNext()){
							AlarmMessage alarm = iterator.next();
							Message msg = NewClientActivity.alarmHandler.obtainMessage();
							msg.what = alarm.type;
							NewClientActivity.alarmHandler.sendMessage(msg);
							
							if(alarm.type == 1 || alarm.type == 2 || alarm.type == 3 || alarm.type == 4){
								Message msg3 = NewClientActivity.alarmHandler.obtainMessage();
								msg3.what = 11;
								NewClientActivity.alarmHandler.sendMessage(msg3);
							}else if(alarm.type == 5 || alarm.type == 6 || alarm.type == 9){
								Message msg2 = NewClientActivity.alarmHandler.obtainMessage();
								msg2.what = 14;
								NewClientActivity.alarmHandler.sendMessage(msg2);
							}
						}
					}else{
						Message msg = NewClientActivity.alarmHandler.obtainMessage();
						msg.what = 10;
						NewClientActivity.alarmHandler.sendMessage(msg);
						Message msg2 = NewClientActivity.alarmHandler.obtainMessage();
						msg2.what = 15;
						NewClientActivity.alarmHandler.sendMessage(msg2);
					}
				}else{
				}
			}
		}
	}
	void printInfo(String info, byte[] packet){
		if(packet != null){
			StringBuffer sb = new StringBuffer();
			int[] str = new int[packet.length];
			for(int j = 0; j<packet.length; j++){
				str[j] = packet[j];
				sb.append(str[j]+",");
			}
			Log.v(info+"插入个数："+packet.length, sb.toString());
		}
	}

}
