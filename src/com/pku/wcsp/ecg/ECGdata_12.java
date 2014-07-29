package com.pku.wcsp.ecg;

import java.sql.Date;
import java.util.LinkedList;

import android.util.Log;

public class ECGdata_12 {
	
	private int heartRate;
	private byte alarmByte;
	private AlarmMessage alarm;
	private LinkedList<AlarmMessage> alarmList;
	private byte[] lead1;
	private byte[] lead2;
	private byte[] lead3;
	private byte[] lead4;
	private byte[] lead5;
	private byte[] lead6;
	private byte[] lead7;
	private byte[] lead8;
	private byte[] lead9;
	private byte[] lead10;
	private byte[] lead11;
	private byte[] lead12;
	
	public ECGdata_12(){
		
	}
	//
	public ECGdata_12(byte [] packet){
		alarmList = new LinkedList<AlarmMessage>();
		
		alarm = new AlarmMessage();
		byte[] time = new byte[4];
		for(int i=0; i<4; i++){
			time[i] = packet[3+i];
//			Log.e("time["+i+"]",""+time[i]);
		}
		int timeint = byte4toInt(time);
		long longtime = timeint;
		longtime = longtime*1000;
		alarm.time = new Date(longtime);

		heartRate = packet[8];//心率
		alarmByte = packet[10];
		
		if(alarmByte == (byte) 0x01){
			Log.e("报警"," 导联脱落");
			AlarmMessage alarm = new AlarmMessage();
			alarm.type = 5;
			alarm.time = new Date(longtime);
			alarmList.offer(alarm);
		}
		if(alarmByte == (byte) 0x02){
			Log.e("报警","波形过载");
			AlarmMessage alarm = new AlarmMessage();
			alarm.type = 6;
			alarm.time = new Date(longtime);
			alarmList.offer(alarm);
		}
		if(alarmByte == (byte) 0x04){
			Log.e("报警","波形异常");
			AlarmMessage alarm = new AlarmMessage();
			alarm.type = 3;
			alarm.time = new Date(longtime);
			alarmList.offer(alarm);
		}
		if(alarmByte == (byte) 0x08){
			Log.e("报警","手动报警");
			AlarmMessage alarm = new AlarmMessage();
			alarm.type = 9;
			alarm.time = new Date(longtime);
			alarmList.offer(alarm);
		}
//		else if(hr_alarm == (byte) 0x01){
//			Log.e("报警","心率过高");
//			AlarmMessage alarm = new AlarmMessage();
//			alarm.type = 2;
//			alarm.time = new Date(longtime);
//			alarmList.offer(alarm);
//		}
		
		byte[] DataValues = new byte[2616];
		
		for(int i = 0; i<2616; i++){
			DataValues[i] = packet[i+13];
		}
		lead1 = new byte[327];
		lead2 = new byte[327];
		lead3 = new byte[327];
		lead4 = new byte[327];
		lead5 = new byte[327];
		lead6 = new byte[327];
		lead7 = new byte[327];
		lead8 = new byte[327];
		lead9 = new byte[327];
		lead10 = new byte[327];
		lead11 = new byte[327];
		lead12 = new byte[327];
		
		for(int i = 0; i<327; i++){
			lead1[i] = DataValues[8*i];
			lead3[i] = DataValues[8*i + 1];
			lead7[i] = DataValues[8*i + 2];
			
			lead2[i] = (byte) (lead1[i] + lead3[i]);
			lead4[i] = (byte) (0 - (lead1[i] + lead2[i])/2);
			lead5[i] = (byte) (lead1[i] - (lead2[i]/2));
			lead6[i] = (byte) (lead2[i] - (lead1[i]/2));

			lead8[i] = DataValues[8*i + 3];
			lead9[i] = DataValues[8*i + 4];
			lead10[i] = DataValues[8*i + 5];
			lead11[i] = DataValues[8*i + 6];
			lead12[i] = DataValues[8*i + 7];
			
			
		}
	}
	
	public byte[] getData(int lead){
		switch(lead){
		case 1:
			return lead1;
		case 2:
			return lead2;
		case 3:
			return lead3;
		case 4:
			return lead4;
		case 5:
			return lead5;
		case 6:
			return lead6;
		case 7:
			return lead7;
		case 8:
			return lead8;
		case 9:
			return lead9;
		case 10:
			return lead10;
		case 11:
			return lead11;
		case 12:
			return lead12;
		default:
			return null;
		}
	}
	
	public int getHeartRate(){
		return heartRate;
	}
	
	public AlarmMessage getECGalarm(){
		return alarm;
	}
	
	public LinkedList<AlarmMessage> getAlarms(){
		return alarmList;
	}
	
	private int byte4toInt(byte[] buf){

		int firstByte = (0x000000FF & ((int) buf[0]));
		int secondByte = (0x000000FF & ((int) buf[1]));
		int thirdByte = (0x000000FF & ((int) buf[2]));
		int fourthByte = (0x000000FF & ((int) buf[3]));
	
		return ( firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte ) & 0xFFFFFFFF; 
	}

}
