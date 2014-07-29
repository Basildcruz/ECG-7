package com.pku.wcsp.ecg;

public class ECGdataBuffer_12 {
	
	private ECGdata_12[] buffer;
	private int HEAD;
	private int TAIL;
	
	public ECGdataBuffer_12(int lenght){
		HEAD = 0;
		TAIL = 0;
		buffer = new ECGdata_12[lenght];
	}
	
	public void add(ECGdata_12 element){
		buffer[TAIL] = element;
		TAIL = (TAIL+1)%buffer.length;
		if( TAIL == HEAD){
			HEAD = (HEAD+1)%buffer.length;
		}
	}
	
	public ECGdata_12 remove(){
		if(HEAD != TAIL){
			ECGdata_12 element = buffer[HEAD];
			HEAD = (HEAD+1)%buffer.length;
			return element;
		}else{
//			Log.v("ÒÆ³ö£º", "¶ÓÁÐ¿Õ");
			return null;
		}
	}

}
