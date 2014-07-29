package com.guo.ecg.client;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import android.os.Message;
import android.util.Log;

public class SendMail extends Thread {
	
	private String recieverAddress;
	private String mailContent;
	private EmailSender sender;
	
	public SendMail(String recieverAddress, String mailContent, String filename){
		this.recieverAddress = recieverAddress;
		this.mailContent = mailContent;
		sender = new EmailSender();
		sender.setProperties("smtp.126.com", "25");
		try {
			sender.setMessage("pku_wxtx@126.com", "ECG-Screen Shot", mailContent);
			sender.setReceiver(new String[]{recieverAddress});
			sender.addAttachment("/mnt/sdcard/ECG-Shot/"+filename);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public SendMail(String recieverAddress, String filename){
		this.recieverAddress = recieverAddress;
		mailContent = "";
		sender = new EmailSender();
		sender.setProperties("smtp.126.com", "25");
		try {
			sender.setMessage("pku_wxtx@126.com", "30'' of Zhang San's ECG ", mailContent);
			sender.setReceiver(new String[]{recieverAddress});
			sender.addAttachment("/mnt/sdcard/ECG-Shot/"+filename);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void run(){
		try {
			sender.sendEmail("smtp.126.com", "pku_wxtx", "pkuwxtx");
			Log.e("send","done");
			Message msg = NewClientActivity.handler.obtainMessage();
			msg.what = 98;
			NewClientActivity.handler.sendMessage(msg);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Message msg = NewClientActivity.handler.obtainMessage();
			msg.what = 99;
			NewClientActivity.handler.sendMessage(msg);
		}
	}

}
