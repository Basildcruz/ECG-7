package com.pku.wcsp.ecg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SendCommand extends Thread {
	
	private InputStream is;
	private OutputStream os;
	private String command = "";
	private int value =0;
	private boolean isSuccess;
	
	public SendCommand(BluetoothSocket socket, String command){
		
		try {
			is = socket.getInputStream();
			os = socket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.command = command;
		isSuccess = false;
	}
	
	public SendCommand(BluetoothSocket socket, String command, int value){
		
		try {
			is = socket.getInputStream();
			os = socket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.command = command;
		this.value = value;
		isSuccess = false;
	}
	
	public void run(){
		try {
			if(value == 0){
				os.write((command+"\r\n").getBytes());
//				isSuccess = true;
//				os.flush();
			}else {
				os.write((command+"\r\n").getBytes());
				os.write(intToByteArray(value));
				os.write("\r\n".getBytes());
//				os.flush();
				
				byte[] array = intToByteArray(value);
				for(int i = 0; i<array.length; i++){
					Log.v("a["+i+"]",""+array[i]);
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String reply = "";
		try {
			Log.v("反馈","读反馈");
			reply = reader.readLine();
			Log.v("反馈",reply);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		if(reply.equals("OK")){
			isSuccess = true;
			Log.v("反馈",reply);
		}else if(reply.equals("ERROR")){
			Log.v("反馈",reply);
		}else{
			Log.v("反馈1",reply);
					
//			Thread thread = new Thread(new Runnable(){
//
//				public void run() {
//					// TODO Auto-generated method stub
//					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//					String reply = "";
//					do{
//						try {
//							reply = reader.readLine();
//							Log.v("清空",reply);
//							if(reply.equals("OK"))
//								break;
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}while(true);
//					Log.v("清空","清空完成");
//					sendMessage(ECGMonitorActivity.outHandler,2);
//					isSuccess = true;
//				}
//			});
//			thread.start();
		}
	}
	
	public boolean isSuccess(){
		return isSuccess;
	}
	
	private byte[] intToByteArray(int integer){
    	int byteNum = (40 -Integer.numberOfLeadingZeros (integer < 0 ? ~integer : integer))/ 8;
    	byte[] byteArray = new byte[4];
    	for (int n = 0; n < byteNum; n++)
    		byteArray[3 - n] = (byte) (integer>>> (n * 8));
    	return (byteArray);
    }
	private byte[] int2ByteArray(int integer){
    	byte[] byteArray = new byte[4];
    	byteArray[0]=(byte)(integer>>24);
    	byteArray[1]=(byte)(integer>>16);
    	byteArray[2]=(byte)(integer>>8);
    	byteArray[3]=(byte)integer;
    	return byteArray;
    }
	
	void sendMessage(Handler handler, int i){
		Message msg = handler.obtainMessage();
		msg.what = i;
		handler.sendMessage(msg);
	}
	
	void printInfo(byte[] packet){
		StringBuffer sb = new StringBuffer();
		int[] str = new int[packet.length];
		for(int j = 0; j<packet.length; j++){
			str[j] = packet[j];
			sb.append(str[j]+",");
		}
		Log.v("Recieve_left", sb.toString());
	}

}
