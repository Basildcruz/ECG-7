package com.pku.wcsp.ecg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.guo.ecg.client.NewClientActivity;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class Reciever extends Thread{
	public static int PATIENT_ID;
	public static ECGdataBuffer ecgBuffer = new ECGdataBuffer(20);
	public static ECGdataBuffer_12 ecgBuffer_12 = new ECGdataBuffer_12(20);
	private boolean recieving;
	private BluetoothSocket socket;
	private InputStream inputStream;
	private ECGfactory ecg;
	
	private byte[] toSendFrame;
	private byte[] toSendFrame_12;
	
	public Reciever(BluetoothSocket socket){
		this.socket = socket;
		toSendFrame = new byte[1000];
		toSendFrame[0] = 0x7F;
		toSendFrame[1] = (byte) 0x80;
		toSendFrame[2] = 0x7F;
		toSendFrame[3] = (byte) 0x80;
		ecg = new ECGfactory();
		
		toSendFrame_12 = new byte[2635];
		toSendFrame_12[0] = 0x7F;
		toSendFrame_12[1] = (byte) 0x80;
		toSendFrame_12[2] = 0x7F;
		toSendFrame_12[3] = (byte) 0x80;
	}
	
	public void run(){
		try {
			inputStream = socket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		recieving = true;
		Log.v("Reciever", "开始接收");
		while(recieving){
			/*byte[] array = new byte[2635];
			try {
				printInfo(array);
				readInto(array,inputStream);
				printInfo(array);
				Log.e("数组长度",""+array.length);
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}*/
			byte[] head = new byte[4];
			boolean found = false;
			do{
				try {
					readInto(head,inputStream);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
				if(head[0] != (byte) 0x7F || head[1] !=(byte) 0x80 || head[2] != (byte) 0x7F || head[3] != (byte) 0x80){
					Log.e("Reciever", "头部错误");
					for(int i = 0; i<head.length; i++){
						Log.v("head["+i+"]",""+(0x000000FF&(int)head[i]));
					}
					try {
						sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					Log.e("Reciever", "头部正确");
					found = true;
					break;
				}
			} while(!found);
			
			byte[] lenght_2byte = new byte[2];
			try {
				readInto(lenght_2byte,inputStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			byte[] lenght_4byte = new byte[]{0,0,lenght_2byte[0],lenght_2byte[1]};
			toSendFrame[4] = lenght_2byte[0];
			toSendFrame[5] = lenght_2byte[1];//长度
			toSendFrame_12[4] = lenght_2byte[0];
			toSendFrame_12[5] = lenght_2byte[1];//长度
			int lenght = byte4toInt(lenght_4byte);
			
			Log.e("Reciever", "包长度为"+lenght);
			if(lenght == 994){
				byte[] frame = new byte[lenght];
				try {
					readInto(frame,inputStream);
					printInfo(frame);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for(int i=0; i<994; i++){
					toSendFrame[i+6] = frame[i];
				}
				
				toSendFrame = convertFrame2(toSendFrame);//转换格式
				
				
				BluetoothManager.toSendQueue.offer(toSendFrame);
				Log.e("SvrConnector","加入转发序列");
				printInfo2(toSendFrame);
				
				ECGdata packet = new ECGdata(frame);
				ecgBuffer.add(packet);
				
			}else if(lenght == 2629){
				byte[] frame_12 = new byte[lenght];
				try {
					readInto(frame_12,inputStream);
					printInfo(frame_12);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for(int i=0; i<2629; i++){
					toSendFrame_12[i+6] = frame_12[i];
				}
				BluetoothManager.toSendQueue.offer(toSendFrame_12);
				Log.e("SvrConnector","加入转发序列_12导");
				printInfo2(toSendFrame_12);
				ECGdata_12 packet_12 = new ECGdata_12(frame_12);
				ecgBuffer_12.add(packet_12);
			}
			else if(lenght == 3){
				byte[] stop = new byte[lenght];
				try {
					readInto(stop,inputStream);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(stop[0] != (byte) 0x45 || stop[1] != (byte) 0x4F || stop[2] != (byte) 0x46){
					Log.e("reciever","停止帧错误");
					for(int i = 0; i<stop.length; i++){
						Log.v("stop["+i+"]",""+(0x000000FF&(int)stop[i]));
					}
					recieving = false;
					sendMessage(BluetoothManager.handleStartTag, 2);
					sendMessage(NewClientActivity.handler,95);
				}else{
					Log.e("reciever","收到停止帧");
					for(int i = 0; i<stop.length; i++){
						Log.v("stop["+i+"]",""+(0x000000FF&(int)stop[i]));
					}
					recieving = false;
					sendMessage(BluetoothManager.handleStartTag, 2);
					sendMessage(NewClientActivity.handler,95);
				}
			}
		}

		
//		byte[] packet = new byte[1000];
//		
//		isTransporting = true;
//		
//		while(isTransporting){
//			try {
//				readInto(packet,inputStream);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				Log.e("Reciever", "读取byte流错误");
//				break;
//			}
//			if(packet[0] != 0x7F || packet[2] != 0x7F){
//				Log.v("Reciever", "包头错误");
//				ECGdata  tmpData = new ECGdata(packet);
//				ecgBuffer.add(tmpData);
//				printInfo(packet);
//			}else{
//				Log.v("Reciever", "包头正确");
//				printInfo(packet);
//				ECGdata  tmpData = new ECGdata(packet);
//				ecgBuffer.add(tmpData);
//			}
//		}
	}
	
	void sendMessage(Handler handler, int i){
		Message msg = handler.obtainMessage();
		msg.what = i;
		handler.sendMessage(msg);
	}
	
	private void readInto(byte[] packet, InputStream inputStream) throws IOException{
		int readCount = 0; // 已经成功读取的字节的个数
		while (readCount < packet.length) {
			 readCount += inputStream.read(packet, readCount, packet.length - readCount);
		}
	}
	
	private int byte4toInt(byte[] buf){

		int firstByte = (0x000000FF & ((int) buf[0]));
		int secondByte = (0x000000FF & ((int) buf[1]));
		int thirdByte = (0x000000FF & ((int) buf[2]));
		int fourthByte = (0x000000FF & ((int) buf[3]));
	
		return ( firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte ) & 0xFFFFFFFF; 
	}
	
	void printInfo(byte[] packet){
		String sb = new String();
		int[] str = new int[packet.length];
		if(packet.length<1000){
			for(int j = 0; j<packet.length; j++){
				str[j] = packet[j];
				sb=sb+(str[j]+",");
			}
			Log.e("ECG_12:", sb.toString());
		}else{
			for(int jj = 0; jj<1000; jj++){
				str[jj] = packet[jj];
				sb=sb+(str[jj]+",");
			}
			Log.e("ECG_12第一部分:", sb.toString());
			for(int jjj = 1000; jjj<packet.length; jjj++){
				str[jjj] = packet[jjj];
				sb=sb+(str[jjj]+",");
			}
			Log.e("ECG_12第一部分:", sb.toString());
		}
		
	}
	void printInfo2(byte[] packet){
		StringBuffer sb = new StringBuffer();
		int[] str = new int[packet.length];
		for(int j = 0; j<packet.length; j++){
			str[j] = packet[j];
			sb.append(str[j]+",");
		}
		Log.e("SvrConnector", sb.toString());
	}
	
	byte[] convertFrame(byte[] oldFrame){
		byte[] newFrame = new byte[1000];
		if(oldFrame != null){
			for(int i=0; i<6; i++){
				newFrame[i] = oldFrame[i];
			}
			newFrame[6] = 2;//version
			newFrame[7] = 0;//id_1
			newFrame[8] = (byte) PATIENT_ID;//id_2
			newFrame[19] = 30;
			for(int i=0; i<991; i++){
				newFrame[i+9] = oldFrame[i+6];
			}
		}
		return newFrame;
	}
	
	byte[] convertFrame2(byte[] oldFrame){
		byte[] newFrame = new byte[1000];
		if(oldFrame != null){
			for(int i=0; i<19; i++){
				newFrame[i] = oldFrame[i];
			}
			newFrame[19] = 50;//血氧值
			for(int i=0; i<980; i++){
				newFrame[i+20] = oldFrame[i+19];
			}
		}
		return newFrame;
	}
	
	byte[] convertTo12LeadFrame(byte[] oldFrame){
		byte[] newFrame = new byte[2635];
		if(oldFrame != null){
			for(int i=0; i<19; i++){
				newFrame[i] = oldFrame[i];
			}
			newFrame[19] = 50;//血氧值
		}
		byte[] temp = new byte[981];
		for(int x = 0; x<981; x++){
			temp[x] = oldFrame[x+19];
		}
		
		byte[] afterInserted = insert5bytesEvery3bytes(temp);

		for(int y=0; y<afterInserted.length; y++){
			newFrame[y+19] = afterInserted[y];
		}
		
		return newFrame;
	}
	
	private byte[] insert5bytesEvery3bytes(byte[] oldFrame){
		byte[] result = new byte[2616];
		int point_old = 0;
		int point_rst = 0;
		while(point_old < oldFrame.length && point_rst < result.length){
			
			if((point_old+1)%3 != 0){
				result[point_rst] = oldFrame[point_old];
				point_old++;
				point_rst++;
			}else{
				result[point_rst] = oldFrame[point_old];
				point_rst++;
				for(int j = 0; j<5; j++){
					result[point_rst] = oldFrame[point_old];
					point_rst++;
				}
				point_old++;
			}
		}
		return result;
	}
	
	
	
}
