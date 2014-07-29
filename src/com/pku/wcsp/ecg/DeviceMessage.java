package com.pku.wcsp.ecg;

import java.util.Date;

public class DeviceMessage {
	
	public String name;
	public int type;
	public Date date;
	public int mode;
	
	public DeviceMessage(){
		
	}
	public DeviceMessage(String name, int type, Date date){
		this.name = name;
		this.type = type;
		this.date = date;
	}

}
