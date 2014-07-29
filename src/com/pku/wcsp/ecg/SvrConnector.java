package com.pku.wcsp.ecg;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;

import com.guo.ecg.client.NewClientActivity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SvrConnector extends Thread implements Runnable 
{
	private Socket socketSV;
	private final BlockingQueue<byte[]> queue;
	private static boolean ConnectionStatus=false;
	public static boolean sendTag;
	private String IP = "";
	private int port = 0;
	private ECGfactory ecg;
	private String newIP;
	private int newPort;
	private static byte[] authCode;
    public SvrConnector(BlockingQueue<byte[]> queue, String IP, int port)
    {
        sendTag = true;
    	this.queue=queue;
    	this.IP = IP;
    	this.port = port;
    	ecg = new ECGfactory();
    	authCode = new byte[2];
    }
    public void exit(){
    	try {
			if (socketSV!=null)
				socketSV.close();
			Log.v("SvrConnector","服务器转发线程关闭");
			ConnectionStatus=false;
		} catch (IOException e) {
			Log.v("SvrConnector","服务器转发线程关闭error");
		}
    }
    public static boolean isConnect(){
    	return ConnectionStatus;
    }
    @Override
    public void run(){
        try
        {
        	socketSV = new Socket(IP,port);
            Log.e("SvrConnector","连接服务器成功");
            Message msg = NewClientActivity.handler.obtainMessage();
            msg.what = 7;
            NewClientActivity.handler.sendMessage(msg);
            ConnectionStatus=true;
            OutputStream os=null;
            os=socketSV.getOutputStream();
            Log.e("SvrConnector","获取OutputStream");
            os.write(ecg.getRequst());
            InputStream is = socketSV.getInputStream();
            byte[] respond = new byte[24];
            readInto(respond,is);
            byte[] port = new byte[4];
			port[0] = 0;
			port[1] = 0;
			port[2] = respond[14];
			port[3] = respond[15];
			newPort = byte4toInt(port);
			Log.e("SvrConnector","端口号："+newPort);
			byte[] IP_byte1 = new byte[]{0, 0, 0, respond[16]};
			byte[] IP_byte2 = new byte[]{0, 0, 0, respond[17]};
			byte[] IP_byte3 = new byte[]{0, 0, 0, respond[18]};
			byte[] IP_byte4 = new byte[]{0, 0, 0, respond[19]};
			int IP1 = byte4toInt(IP_byte1);
			int IP2 = byte4toInt(IP_byte2);
			int IP3 = byte4toInt(IP_byte3);
			int IP4 = byte4toInt(IP_byte4);
			newIP = IP1 + "." + IP2 + "." + IP3 + "." + IP4;
			Log.e("SvrConnector","IP地址："+newIP);
			authCode[0] = respond[20];
			authCode[1] = respond[21];
			Log.e("SvrConnector","授权码："+authCode.toString());
			
			Socket socket = new Socket(newIP,newPort);
			Log.e("SvrConnector","ECG数据通道建立");
			OutputStream outputStream = socket.getOutputStream();
			
			
            byte[] temp=new byte[1000];
            while(true && sendTag)
            {
            	temp=queue.poll(1, TimeUnit.SECONDS);
                printInfo(temp);
            	Log.e("SvrConnector","循环转发");
                
                if (temp!=null)
                {
                	byte[] frame = new byte[1020];
                	frame[0] = 0x15;
                	frame[1] = 0x41;
                	frame[2] = 0x03;
                	frame[3] = (byte)0xFC;
                	frame[6] = 0x20;
                	frame[12] = authCode[0];
                	frame[13] = authCode[1];
                	for(int i=0; i<1000; i++){
                		frame[20+i] = temp[i];
                	}
                	crc(frame);
                	outputStream.write(frame, 0, frame.length);
                   	Log.e("SvrConnector","队列已转发"+frame.length+"字节至服务器！"+"队列容量"+queue.remainingCapacity());
                   	Log.e("SvrConnector","发送成功");
                }else{
                	
                }
            }
        }
        catch(Exception e)
        {
//            Message msg = NewClientActivity.handler.obtainMessage();
//            msg.what = 8;
//            NewClientActivity.handler.sendMessage(msg);
//        	Log.v("SvrConnector",e.getMessage());
        	e.printStackTrace();
        	Log.v("SvrConnector","服务器转发线程错误");
        }
    }
    void sendMessage(Handler handler, int i){
		Message msg = handler.obtainMessage();
		msg.what = i;
		handler.sendMessage(msg);
	}
    void printInfo(byte[] packet){
    	if(packet != null){
    		StringBuffer sb = new StringBuffer();
    		int[] str = new int[packet.length];
    		for(int j = 0; j<packet.length; j++){
    			str[j] = packet[j];
    			sb.append(str[j]+",");
    		}
    		Log.e("send", sb.toString());
    	}else{
    	}
		
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
    private void crc(byte[] array){
		CRC32 crc32 = new CRC32();
		crc32.update(array);
		long crcLong = crc32.getValue();
		short crcShort = (short) (crcLong&0xffff);
		array[8] = (byte)(crcShort>>>8);
		array[9] = (byte)(crcShort&0xff);
	}
    
    public static byte[] stopSend(byte[] authCode){
		byte[] stopFrame = new byte[1020];
		stopFrame[0] = 0x15;
		stopFrame[1] = 0x41;
		stopFrame[2] = 0x03;
		stopFrame[3] = (byte)0xFC;
		
		stopFrame[12] = authCode[0];
		stopFrame[13] = authCode[1];
		
		CRC32 crc32 = new CRC32();
		crc32.update(stopFrame);
		long crcLong = crc32.getValue();
		short crcShort = (short) (crcLong&0xffff);
		stopFrame[8] = (byte)(crcShort>>>8);
		stopFrame[9] = (byte)(crcShort&0xff);
		
		return stopFrame;
	}
}
