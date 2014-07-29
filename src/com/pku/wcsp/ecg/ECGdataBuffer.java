package com.pku.wcsp.ecg;

public class ECGdataBuffer {
	
	private ECGdata[] buffer;
	private int HEAD;
	private int TAIL;
	
	public ECGdataBuffer(int lenght){
		HEAD = 0;
		TAIL = 0;
		buffer = new ECGdata[lenght];
	}
	
	public void add(ECGdata element){
		buffer[TAIL] = element;
		TAIL = (TAIL+1)%buffer.length;
		if( TAIL == HEAD){
			HEAD = (HEAD+1)%buffer.length;
		}
	}
	
	public ECGdata remove(){
		if(HEAD != TAIL){
			ECGdata element = buffer[HEAD];
			HEAD = (HEAD+1)%buffer.length;
			return element;
		}else{
//			Log.v("ÒÆ³ö£º", "¶ÓÁÐ¿Õ");
			return null;
		}
	}

}
