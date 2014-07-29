package com.guo.ecg.client;

import android.util.Log;

public class CycleIntQueue {
	public static int[] buffer;
	private int HEAD;
	private int TAIL;
	public CycleIntQueue(int length){
		HEAD = 0;
		TAIL = 0;
		buffer = new int[length];
	}
	public void addInt(int element){
		buffer[TAIL] = element;
		TAIL = (TAIL+1)%buffer.length;
		if( TAIL == HEAD){
			HEAD = (HEAD+1)%buffer.length;
//			Log.v("插入：", "队列满");
		}
	}

	public int removeInt(){
		if(HEAD != TAIL){
			int element = buffer[HEAD];
			HEAD = (HEAD+1)%buffer.length;
			return element;
		}else{
			Log.v("移出：", "队列空");
			return 0;
		}
	}
}
