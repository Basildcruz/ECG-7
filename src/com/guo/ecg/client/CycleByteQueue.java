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
//			Log.v("插入：", "队列满");
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
			Log.v("移出：", "队列空");
			return (Byte) null;
		}
	}
	public byte[] removeBytes(int lenght){
		byte[] array = new byte[lenght];
		for(int i = 0; i < array.length; i++){
			try{
				array[i] = removeByte();
			}catch(NullPointerException e){
				Log.v("循环队列：", "null");
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
		Log.v("队列中移出个数:"+packet.length, sb.toString());
	}

}
