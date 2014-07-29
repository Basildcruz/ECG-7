package com.pku.wcsp.ecg;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

import android.util.Log;

public class ECGdata {
	private int heartRate;
	private byte leaddrop;
	private byte inputover;
	private byte t_alarm;
	private byte st_alarm;
	private byte hr_alarm;
	private byte manual_alarm;
	private AlarmMessage alarm;
	private LinkedList<AlarmMessage> alarmList;
	private byte[] lead1;
	private byte[] lead2;
	private byte[] lead3;
	private byte[] lead4;
	private byte[] lead5;
	private byte[] lead6;
	private byte[] lead7;
	
	public ECGdata(){
		
	}
	//1.3.7
	public ECGdata(byte [] packet){
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

		heartRate = packet[7];//心率
		leaddrop = packet[8];
		hr_alarm = packet[9];
		t_alarm = packet[10];
		st_alarm = packet[11];
		manual_alarm = packet[12];
		
		if(leaddrop == (byte) 0x80){
			Log.e("报警"," 导联脱落");
			AlarmMessage alarm = new AlarmMessage();
			alarm.type = 5;
			alarm.time = new Date(longtime);
			alarmList.offer(alarm);
		}
		if(inputover == (byte) 0x80){
			Log.e("报警","心率异常");
			AlarmMessage alarm = new AlarmMessage();
			alarm.type = 6;
			alarm.time = new Date(longtime);
			alarmList.offer(alarm);
		}
		if(t_alarm == (byte) 0x80){
			Log.e("报警","T异常");
			AlarmMessage alarm = new AlarmMessage();
			alarm.type = 3;
			alarm.time = new Date(longtime);
			alarmList.offer(alarm);
		}
		if(st_alarm == (byte) 0x80){
			Log.e("报警","ST异常");
			AlarmMessage alarm = new AlarmMessage();
			alarm.type = 4;
			alarm.time = new Date(longtime);
			alarmList.offer(alarm);
		}
		if(manual_alarm == (byte) 0x80){
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
		
		byte[] DataValues = new byte[981];
		
		for(int i = 0; i<981; i++){
			DataValues[i] = packet[i+13];
		}
		lead1 = new byte[327];
		lead2 = new byte[327];
		lead3 = new byte[327];
		lead4 = new byte[327];
		lead5 = new byte[327];
		lead6 = new byte[327];
		lead7 = new byte[327];
		
		for(int i = 0; i<327; i++){
			lead1[i] = DataValues[3*i];
			lead3[i] = DataValues[3*i + 1];
			lead7[i] = DataValues[3*i + 2];
			
			lead2[i] = (byte) (lead1[i] + lead3[i]);
			lead4[i] = (byte) (0 - (lead1[i] + lead2[i])/2);
			lead5[i] = (byte) (lead1[i] - (lead2[i]/2));
			lead6[i] = (byte) (lead2[i] - (lead1[i]/2));
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
