package com.guo.ecg.client;

import android.util.Log;

public class CycleByteQueue {
	private byte[] buffer;
	private int HEAD;
	private int TAIL;
	public CycleByteQueue(byte[] buffer){
		HEAD = 0;
		TAIL = 0;
		this.buffer = buffer;
	}
	public void addByte(byte element){
		buffer[TAIL] = element;
		TAIL = (TAIL+1)%buffer.length;
		if( TAIL == HEAD){
			HEAD = (HEAD+1)%buffer.length;
//			Log.v("���룺", "������");
		}
	}
	public void addBytes(byte[] array){
		for(int i = 0; i < array.length; i++){
			addByte(array[i]);
		}
	}
	public byte removeByte(){
		if(HEAD != TAIL){
			byte element = buffer[HEAD];
			HEAD = (HEAD+1)%buffer.length;
			return element;
		}else{
			Log.v("�Ƴ���", "���п�");
			return (Byte) null;
		}
	}
	public byte[] removeBytes(int lenght){
		byte[] array = new byte[lenght];
		for(int i = 0; i < array.length; i++){
			try{
				array[i] = removeByte();
			}catch(NullPointerException e){
				Log.v("ѭ�����У�", "null");
				return null;
			}
		}
		return array;
	}
	void printInfo(byte[] packet){
		StringBuffer sb = new StringBuffer();
		int[] str = new int[packet.length];
		for(int j = 0; j<packet.length; j++){
			str[j] = packet[j];
			sb.append(str[j]+",");
		}
		Log.v("�������Ƴ�����:"+packet.length, sb.toString());
	}

}
