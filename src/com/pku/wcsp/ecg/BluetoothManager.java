package com.pku.wcsp.ecg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.guo.ecg.client.NewClientActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BluetoothManager {
	
	private boolean testMode = false;
	private boolean normalMode = false;
	private boolean alarmMode = false;
	private boolean historyMode = false;
	private BluetoothAdapter adapter;
	private BluetoothDevice btdevice;
	private BluetoothSocket socket;
	private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static final UUID uuid2 = UUID.fromString("0f3561b9-bda5-4672-84ff-ab1f98e349b6");
	private boolean isDiscoveried = false;
	private boolean isConnected = false;
	private static boolean isTransporting = false;
	private DeviceMessage device = null;
	private PatientMessage patient = null;
	private Queue<AlarmMessage> alarmQueue = null;
	private int lead = 7;
	private int ECGsampingRate = 500;
	private double gain = 1.24;
	private static String find_address = "";
	private static HashMap<String,String> hashmap = new HashMap<String, String>();
	public static BlockingQueue<byte[]> toSendQueue;//7导数据
	public static BlockingQueue<byte[]> toSendQueue2;//12导数据
	public static Handler handler = new Handler(){
		public void handleMessage(Message msg){
			BluetoothDevice device = (BluetoothDevice) msg.obj;
			String name = device.getName();
			String address = device.getAddress();
			
			find_address = address;
			SendMessage(NewClientActivity.handler, 1);
			
			hashmap.put(address, name);
			Log.e("写入内部hash",address+name);
		}
	};
	public static Handler handleStartTag = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what == 1){
				isTransporting = true;
			}else if(msg.what == 2){
				isTransporting = false;
			}
		}
	};
	
	public BluetoothManager(BluetoothAdapter adapter){
		toSendQueue = new LinkedBlockingQueue<byte[]>(1000);
		toSendQueue2 = new LinkedBlockingQueue<byte[]>(2635);
		alarmQueue = new LinkedList<AlarmMessage>();
		this.adapter = adapter;
	}
	
	public BluetoothManager(){
		toSendQueue = new LinkedBlockingQueue<byte[]>(1000);
		toSendQueue2 = new LinkedBlockingQueue<byte[]>(2635);
		alarmQueue = new LinkedList<AlarmMessage>();
		this.adapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	public boolean setTestMode(){
		normalMode = false;
		alarmMode = false;
		historyMode = false;
		testMode = true;
		return true;
	}
	
	public void setRealTimeWorkMode_12lead(){
		normalMode = true;
		alarmMode = false;
		historyMode = false;
		testMode = false;
	}
	
	public boolean cancleTestMode(){
		testMode = false;
		return true;
	}
	
	public int getWorkMode(){
		if(isConnected){
			if(testMode)
				return 1;
			else if(normalMode)
				return 2;
			else if(alarmMode)
				return 3;
			else if(historyMode)
				return 4;
			else
				return 0;
		}else
			return 1;
	}
	
	public boolean setAlarmMode()throws IOException{
		testMode = false;
		normalMode = false;
		alarmMode = true;
		historyMode = false;
		if(isConnected && !isTransporting){
			if(testMode){
				return false;
			}else{
				if(isConnected && !isTransporting){
					OutputStream os = socket.getOutputStream();
					os.write("ECG:SETMODE\r\n".getBytes());
					os.write("ALARM\r\n".getBytes());
					InputStream is = socket.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					String reply = "";
					reply = reader.readLine();
					Log.v("设置模式",reply);
					if(reply.contains("OK")){
						
						Log.e("设置模式","成功");
						return true;
					}else{
						Log.e("设置模式",reply);
						return false;
					}
				}
				return false;
			}
		}else{
			return false;
		}
	}
	
	public boolean setNormalMode()throws IOException{
		testMode = false;
		normalMode = true;
		alarmMode = false;
		historyMode = false;
		if(isConnected && !isTransporting){
			if(testMode){
				return false;
			}else{
				if(isConnected && !isTransporting){
					OutputStream os = socket.getOutputStream();
					os.write("ECG:SETMODE\r\n".getBytes());
					os.write("NORMAL\r\n".getBytes());
					InputStream is = socket.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					String reply = "";
					reply = reader.readLine();
					Log.v("设置模式",reply);
					if(reply.contains("OK")){
						Log.e("设置模式","成功");
						return true;
					}else{
						Log.e("设置模式",reply);
						return false;
					}
				}
				return false;
			}
		}else{
			return false;
		}
	}
	
	public boolean setHistoryMode()throws IOException{
		testMode = false;
		normalMode = false;
		alarmMode = false;
		historyMode = true;
		if(isConnected && !isTransporting){
			if(testMode){
				return false;
			}else{
				if(isConnected && !isTransporting){
					OutputStream os = socket.getOutputStream();
					os.write("ECG:SETMODE\r\n".getBytes());
					os.write("HISTORY\r\n".getBytes());
					InputStream is = socket.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					String reply = "";
					reply = reader.readLine();
					Log.v("设置模式",reply);
					if(reply.contains("OK")){
						Log.e("设置模式","成功");
						return true;
					}else{
						Log.e("设置模式",reply);
						return false;
					}
				}
				return false;
			}
		}else{
			return false;
		}
	}
	
	public void startDiscovery(final HashMap<String, String> map, final int expected){
		
		Thread thread = new Thread(new Runnable(){
			public void run() {
				// TODO Auto-generated method stub
				adapter.startDiscovery();
		        int count = 0;
		        while(hashmap.size() < expected && count<10){
		        	try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        	count++;
		        	Log.e("搜索","搜索"+count+"秒");
		        }
		        if(!hashmap.isEmpty()){
		        	Iterator<String> iterator = hashmap.keySet().iterator();
		        	while(iterator.hasNext()){
		        		String key = iterator.next();
		        		String value = hashmap.get(key);
		        		map.put(key, value);
		        		Log.e("写入用户哈希:","address:"+key+"name"+value);
		        	}
		        }
		        isDiscoveried = true;
		        Log.e("搜索","是否完成："+isDiscoveried);
			}
		});
		thread.start();
	}
	
	public void startDiscovery(){
		adapter.startDiscovery();
		Log.v("start","搜索");
		
	}
	
	public void stopDiscovery(){
		adapter.cancelDiscovery();
	}
	
	public boolean isDiscoveried(){
		return isDiscoveried;
	}
	
	public void connect(String address) throws IOException{
		
		if(adapter.isDiscovering())
			adapter.cancelDiscovery();
		if(testMode){
			Log.i("连接","测试模式，产生虚拟数据");
			isConnected = true;
		}else{
			btdevice = adapter.getRemoteDevice(address);
			socket = btdevice.createRfcommSocketToServiceRecord(uuid);
			
			Thread thread = new Thread(new Runnable(){

				public void run() {
					// TODO Auto-generated method stub
					try {
						socket.connect();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(socket != null){
						
						try {
							InputStream is = socket.getInputStream();
							BufferedReader reader = new BufferedReader(new InputStreamReader(is));
							String reply = "";
							reply = reader.readLine();
							Log.e("连接",reply);
							if(reply.contains("ECG")){
								OutputStream os = socket.getOutputStream();
								os.write("OK\r\n".getBytes());
							}
							reply = reader.readLine();
							Log.e("连接",reply);
							if(reply.equals("OK")){
								isConnected = true;
								Log.e("连接","连接完成");
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
			thread.start();
		}
	}
	
	public void connect() throws IOException{
		
		if(adapter.isDiscovering())
			adapter.cancelDiscovery();
		if(testMode){
			Log.i("连接","测试模式，产生虚拟数据");
			isConnected = true;
		}else{
			btdevice = adapter.getRemoteDevice(find_address);
			socket = btdevice.createRfcommSocketToServiceRecord(uuid);
			
			Thread thread = new Thread(new Runnable(){

				public void run() {
					// TODO Auto-generated method stub
					try {
						socket.connect();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						SendMessage(NewClientActivity.handler,3);
						Log.e("连接","Socket连接失败");
					}
					if(socket != null){
						
						try {
							InputStream is = socket.getInputStream();
							BufferedReader reader = new BufferedReader(new InputStreamReader(is));
							String reply = "";
							reply = reader.readLine();
							Log.e("连接",reply);
							if(reply.contains("ECG")){
								OutputStream os = socket.getOutputStream();
								os.write("OK\r\n".getBytes());
							}
							reply = reader.readLine();
							Log.e("连接",reply);
							if(reply.equals("OK")){
								isConnected = true;
								SendMessage(NewClientActivity.handler,2);
								Log.e("连接","连接完成");
							}
							getECGleadNumber();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.e("连接","Socket为空");
							SendMessage(NewClientActivity.handler,3);
						}
					}
				}
			});
			thread.start();
		}
	}
	
	public boolean disconnect(){
		try {
			if(socket != null)
				socket.close();
			isConnected = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean isconnected(){
		return isConnected;
	}
	
	public boolean connectToServer(String IP, int port){
		SvrConnector sctr = new SvrConnector(toSendQueue, IP, port);
		sctr.start();
		return true;
	}
	
	public boolean startECGTransport(){
		
		if(testMode){
			ECGfactory factory = new ECGfactory();
			factory.start();
			Log.e("testMode","factory start");
			isTransporting = true;
			return true;
		}else{
			
			if(isConnected && !isTransporting){
				
				try {
					OutputStream os = socket.getOutputStream();
					os.write(("ECG:START\r\n").getBytes());
					
					InputStream is = socket.getInputStream();
					byte[] OK = new byte[4];
					try {
						readInto(OK,is);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(OK[0] != 0x4F || OK[1] !=0x4B || OK[2] != 0x0D || OK[3] != 0x0A){
						Log.e("Reciever", "读取OK错误");
						for(int i = 0; i<OK.length; i++){
							Log.v("byte["+i+"]",""+(0x000000FF&(int)OK[i]));
						}
						BufferedReader reader2 = new BufferedReader(new InputStreamReader(is));
						String reply2 = "";
						try {
							reply2 = reader2.readLine();
							Log.e("Reciever", reply2);
							byte[] replybyte = reply2.getBytes();
							for(int i = 0; i<replybyte.length; i++){
								Log.v("byte["+i+"]",""+(0x000000FF&(int)replybyte[i]));
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return false;
						
					}else{
						Log.e("Reciever", "读取OK正确");
						for(int i = 0; i<OK.length; i++){
							Log.v("byte["+i+"]",""+(0x000000FF&(int)OK[i]));
						}
						
						Reciever reciever = new Reciever(socket);
						reciever.start();
						isTransporting = true;
						Log.v("reciever","接收线程启动");
						return true;
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}else{
				return false;
			}
		}
	}
	
	public boolean stopECGTransport(){

		if(testMode){
			ECGfactory.isBuilding = false;
			isTransporting = false;
			return true;
		}else{
			if(isConnected){
				try {
					OutputStream os = socket.getOutputStream();
					os.write(("ECG:STOP\r\n").getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return true;
		}
	}
	
	public boolean isTransporting(){
		return isTransporting;
	}
	
	public boolean setDevice(DeviceMessage device){
		this.device = device;
		Date date = device.date;
		long longtime = date.UTC(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
		int time = (int) (longtime/1000);
		Log.v("时间",""+String.valueOf(time));
		SendCommand sendTime = new SendCommand(socket,"ECG:SETTIME",time);
		sendTime.run();
		return true;
	}
	
	public boolean setPatient(PatientMessage patient){
		this.patient = patient;
		return true;
	}
	
	public DeviceMessage getDevice() throws IOException{
		
		if(NewClientActivity.LeadNumber == 7){
			DeviceMessage device = new DeviceMessage();
			OutputStream os = socket.getOutputStream();
			os.write("ECG:DEVICEINFO\r\n".getBytes());
			Log.e("Device","写入命令");
			InputStream is = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String reply = "";
			Log.e("Device","准备读OK");
			reply = reader.readLine();
			Log.v("接收设置信息",reply);
			if(reply.equals("OK")){
				Log.e("接收设备信息","准备好");
				reply = reader.readLine();
				
//				byte[] timebytes = reply.getBytes();
//				for(int i=0; i<timebytes.length; i++){
//				Log.e("timebytes["+i+"]",""+timebytes[i]);
//				}
//				
//				byte[] timebyte = new byte[4];
//				for(int i=0; i<4; i++){
//					timebyte[i] = timebytes[i];
//				}
//				int timeint = byte4toInt(timebytes);
				
				int timeint = Integer.parseInt(reply);
				Log.e("setTime","UTC:"+timeint);
				long timelong = (long) timeint;
				timelong = timelong*1000;
				Date date = new Date(timelong);
				device.date = date;
				
				reply = reader.readLine();
				
				if(reply.equals("NORMAL")){
					device.mode = 2;
					normalMode = true;
					testMode = false;
					alarmMode = false;
					historyMode = false;
				}
				else if(reply.equals("ALARM")){
					device.mode = 3;
					alarmMode = true;
					testMode = false;
					normalMode = false;
					historyMode = false;
				}
				else if(reply.equals("HISTORY")){
					device.mode = 4;
					historyMode = true;
					alarmMode = false;
					normalMode = false;
					testMode = false;
				}
				else{
					device.mode = 1;
					testMode = true;
					alarmMode = false;
					normalMode = false;
					historyMode = false;
				}
				device.type = 1;
				return device;
			}else{
				Log.e("不能设置信息",reply);
				return null;
			}
		}else{
			DeviceMessage device = new DeviceMessage();
			device.mode = 2;
			normalMode = true;
			testMode = false;
			alarmMode = false;
			historyMode = false;
			return device;
		}
		
		
	}
	
	public PatientMessage getPatient(){
		return patient;
	}
	
	public boolean setAlarm(AlarmMessage alarm){
		alarmQueue.offer(alarm);
		return true;
	}
	
	public AlarmMessage[] getAlarms(){
		AlarmMessage[] alarm = new AlarmMessage[alarmQueue.size()];
		for(int i = 0; i<alarmQueue.size(); i++){
			alarm[i] = alarmQueue.poll();
		}
		return alarm;
	}
	
	public boolean setAlarm(AlarmMessage[] alarm){
		for(int i = 0; i<alarm.length; i++){
			alarmQueue.offer(alarm[i]);
		}
		return true;
	}
	
	public void getECGleadNumber(){
		
		try{
			OutputStream os = socket.getOutputStream();
			os.write("ECG:GETPARA\r\n".getBytes());
			Log.e("leadNumber","请求心电参数");
			InputStream is = socket.getInputStream();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.e("leadNumber",""+is.available());
			if(is.available() != 0){
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String reply = "";
				reply = reader.readLine();
				Log.v("leadNumber",reply);
				if(reply.equals("OK")){
					reply = reader.readLine();
					Log.v("LEADS",reply);
					reply = reader.readLine();
					Log.v("FREQ",reply);
					NewClientActivity.LeadNumber = 12;
				}else{
					Log.v("leadNumber",reply);
				}
			}else{
				Log.v("leadNumber","无响应，导联数为7");
				NewClientActivity.LeadNumber = 7;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
	
	public void getVersion(){
		try{
			OutputStream os = socket.getOutputStream();
			os.write("ECG:GETVERSION\r\n".getBytes());
			Log.e("ecgVersion","请求协议版本号");
			InputStream is = socket.getInputStream();
			if(is.available() != 0){
				NewClientActivity.LeadNumber = 12;
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String reply = "";
				reply = reader.readLine();
				Log.v("ecgVersion",reply);
				reply = reader.readLine();
				Log.v("ecgVersion",reply);
			}else{
				Log.v("ecgVersion","无响应，7导联旧版本");
				NewClientActivity.LeadNumber = 12;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
	
	public void getTime(){
		try{
			OutputStream os = socket.getOutputStream();
			os.write("ECG:GETTIME\r\n".getBytes());
			Log.e("getTime","请求时间");
			InputStream is = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String reply = "";
			reply = reader.readLine();
			Log.e("getTime",reply);
			reply = reader.readLine();
			Log.e("getTime",reply);
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setTime(){
		try{
			OutputStream os = socket.getOutputStream();
			os.write("ECG:SETTIME\r\n".getBytes());
			Log.e("getTime","请求时间");
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = df.format(new Date(System.currentTimeMillis()));
			os.write(("TIME:"+time+"\r\n").getBytes());
			Log.e("getTime","设置时间"+time);
			InputStream is = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String reply = "";
			reply = reader.readLine();
			Log.e("getTime",reply);
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public int getECGsampingRate(){
		
		return ECGsampingRate;
	}
	
	public double getECGgain(){
		
		return gain;
	}
	
	public ECGdata getECGdata(){
		ECGdata data = null;
		data = Reciever.ecgBuffer.remove();
		return data;
	}
	public ECGdata_12 getECGdata_12(){
		ECGdata_12 data = null;
		data = Reciever.ecgBuffer_12.remove();
		return data;
	}
	
	public ECGdata[] getECGhistory(Date begin, int lenght){
		if(testMode){
			ECGdata[] history = new ECGdata[4];
			ECGfactory factory = new ECGfactory();
			history = factory.getSampleECG();
			return history;
		}else{
			if(isConnected){
				
			}
			return null;
		}
	}
	
	public boolean getECGhistory(String filename) throws IOException{
		if(testMode){
			return false;
		}else{
			if(isConnected && !isTransporting){
				OutputStream os = socket.getOutputStream();
				os.write("ECG:GETFILE\r\n".getBytes());
				os.write((filename+"\r\n").getBytes());
				InputStream is = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String reply = "";
				reply = reader.readLine();
				Log.v("接收历史文件",reply);
				if(reply.contains("OK")){
					Log.v("传输历史","准备好");
					return true;
				}else{
					Log.e("不能接收历史",reply);
					return false;
				}
			}
			return false;
		}
	}
	
	public LinkedList<String> getECGFilesList(){
		
		LinkedList<String> list = new LinkedList<String>();
		
		if(isConnected){
			
			try {
				OutputStream os = socket.getOutputStream();
				os.write("ECG:GETFILELIST\r\n".getBytes());
				InputStream is = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String reply = "";
				reply = reader.readLine();
				if(reply.contains("OK")){
					do{
						reply = reader.readLine();
						if(!reply.equals("EOF")){
							list.offer(reply);
						}else{
							Log.v("读到末尾",reply);
							break;
						}
					}while(true);
				}
				return list;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}else{
			return null;
		}
	}
	
	private static void SendMessage(Handler handler, int i){
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
	
}
