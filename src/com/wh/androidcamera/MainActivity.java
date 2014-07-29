package com.wh.androidcamera;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;
import java.util.zip.CRC32;

import com.guo.ecg.client.R;


//import org.crazyit.ui.R;
import android.widget.Switch;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements SurfaceHolder.Callback,
Camera.PreviewCallback{

	/**Debug*/

	private int ssrc=0;
	Switch switcher;
	private SurfaceView mSurfaceView=null;
	private SurfaceHolder mSurfaceHolder=null;
	private Camera mCamera=null;
	private Button mBtnTransmit;
	private Button mBtnconnect;
	private Button mBtnsetting;
	private byte[] ID_number=new byte[2];
	private byte [] top_buffer=new byte[20];
	private int p_number;//��ǰͼƬ��С
	private int total_number;//�ð����ܳ���
	/**�û���*/
    private String pUsername="wh";
    /**��������ַ*/
    private String serverUrl="192.168.1.107";
    /**�������˿�*/
    private int commandPort=2012;
    private int serverPort=-1;
    /**��Ƶˢ�¼��*/
    private int VideoPreRate=1;
    /**��ǰ��Ƶ���*/
    private int tempPreRate=0;
    /**��Ƶ����*/
    private int VideoQuality=85;
    
    /**������Ƶ��ȱ���*/
    private float VideoWidthRatio=1;
    /**������Ƶ�߶ȱ���*/
    private float VideoHeightRatio=1;
    
    /**������Ƶ���*/
    private int VideoWidth=320;
    /**������Ƶ�߶�*/ 
    private int VideoHeight=240;
    /**��Ƶ��ʽ����*/
    private int VideoFormatIndex=0;
    /**�Ƿ�����Ƶ*/
    private boolean startSendVideo=false;
    private boolean whether_connect=false;
    
  //private boolean sign=true;
    private Socket tempSocket = null;
    
	public void setPort(int port)
	{
		serverPort=port;
		whether_connect=true;
		try {
		if(tempSocket != null)
		{
			
				tempSocket.close();
			
		}
		tempSocket = new Socket(serverUrl, serverPort);	
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getServerUrl()
	{
		return serverUrl;
	}
	public int getCommandPort()
	{
		return commandPort;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, 
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); ////���ô���ʼ�յ���
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		
		setContentView(R.layout.activity_main);
		
		//final ReceiveCommandThread commandThread = new ReceiveCommandThread(this);//ʵ��������˿ڵĽ���
		// final   EndReceiveThread endThread=new EndReceiveThread(this);
		mSurfaceView=(SurfaceView) findViewById(R.id.surface_preview);
		switcher = (Switch)findViewById(R.id.switcher);
		OnCheckedChangeListener listener = new OnCheckedChangeListener()
		{
			
			public void onCheckedChanged(CompoundButton button
					, boolean isChecked)
			{
				if(isChecked)
				{
					final ReceiveCommandThread commandThread = new ReceiveCommandThread(MainActivity.this);//ʵ��������˿ڵĽ���
					commandThread.start();
				}
				else
				{  
					final EndReceiveThread endThread=new EndReceiveThread(MainActivity.this);
					endThread.start();
					
					mBtnTransmit.setText("�ȴ�����");
					mBtnTransmit.setEnabled(false);
				}
			}
		};
		
		switcher.setOnCheckedChangeListener(listener);		
	
         mBtnsetting=(Button) findViewById(R.id.setting);	
		 mBtnsetting.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				openOptionsMenu();
			}
		});
		
		
		mBtnTransmit=(Button) findViewById(R.id.transmit);
		mBtnTransmit.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
					mBtnTransmit.setEnabled(false);
					mBtnTransmit.setText("���ݴ�����");
					startSendVideo=true;
					whether_connect=false;
		//		}
			}
		});
	}

	@Override
	/**���ָ�ֵ  ��ʼ�趨*/
	protected void onStart() {
		// TODO Auto-generated method stub
		mSurfaceHolder = mSurfaceView.getHolder(); // ��SurfaceView��ȡ��SurfaceHolder����
		mSurfaceHolder.addCallback(this); // SurfaceHolder����ص��ӿ�       
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// ������ʾ�����ͣ�setType��������   ��������
	    //��ȡ�����ļ�
	    SharedPreferences preParas = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
	    pUsername=preParas.getString("Username", "wh");
	    serverUrl=preParas.getString("serverUrl", "192.168.1.110");
		String tempStr=preParas.getString("commandPort", "2012");
		commandPort=Integer.parseInt(tempStr);
	    tempStr=preParas.getString("VideoPreRate", "1");
	    VideoPreRate=Integer.parseInt(tempStr);	            
	    tempStr=preParas.getString("VideoQuality", "85");
	    VideoQuality=Integer.parseInt(tempStr);
	    tempStr=preParas.getString("VideoWidthRatio", "100");
	    VideoWidthRatio=Integer.parseInt(tempStr);
	    tempStr=preParas.getString("VideoHeightRatio", "100");
	    VideoHeightRatio=Integer.parseInt(tempStr);
	    VideoWidthRatio=VideoWidthRatio/100f;
	    VideoHeightRatio=VideoHeightRatio/100f;
	  
	    
	
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			mCamera=Camera.open(); //������ͷ
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try{//�˳�camera���ر�����ͷ
	        if (mCamera != null) {
	        	mCamera.setPreviewCallback(null); // �������������ǰ����Ȼ�˳�����
	            mCamera.stopPreview();
	            mCamera.release();
	            mCamera = null;
	        } 
	    } catch (Exception e) {
	        e.printStackTrace();
	    } 
	}

	@Override
	//����ѡ���б�
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
    	menu.add(0,0,0,"ϵͳ����");
    	menu.add(0,1,1,"�˳�����"); 
    	return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);//��ȡ�˵�
    	switch(item.getItemId())//�˵����
    	{
    		case 0:
    			//ϵͳ����
    			{
    				Intent intent=new Intent(this,SettingActivity.class);
    				startActivity(intent);  
    			}
    			break;   			
        		case 1://�˳�����
	    		{
	    			//ɱ���߳�ǿ���˳�
	    	//	     EndReceiveThread endThread=new EndReceiveThread(this);
	    	//		 endThread.start();
	    		//     try {
					//	Thread.sleep(1000);
				//	} catch (InterruptedException e) {
						// TODO Auto-generated catch block
				//		e.printStackTrace();
			//		}
					 android.os.Process.killProcess(android.os.Process.myPid());
	    			
	    		}
    			break;
    	}    	
    	return true;
	}

	
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		if (mCamera == null) {
            return;
        }
        mCamera.stopPreview();
        mCamera.setPreviewCallback(this);
        mCamera.setDisplayOrientation(90); //���ú���¼��
        //��ȡ����ͷ����
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(320,240);
        //parameters.setPreviewFrameRate(10);
        mCamera.setParameters(parameters);
        Size size = parameters.getPreviewSize();		
        VideoWidth=size.width;
        VideoHeight=size.height;
        VideoFormatIndex=parameters.getPreviewFormat();    
        mCamera.startPreview();
	}

	
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
	}

	
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		if (null != mCamera) {
            mCamera.setPreviewCallback(null); // �������������ǰ����Ȼ�˳�����
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
		
	}
   //���ñ�������ͼƬ
	
	public void onPreviewFrame(byte[] data, Camera camera) {       
		// TODO Auto-generated method stub
		
			mBtnTransmit.setEnabled(whether_connect&&(!startSendVideo));
		if(!startSendVideo)
		
			return;
		
		if(tempPreRate<VideoPreRate){
		    tempPreRate++;
			return;
			}
		tempPreRate=0;	
		try {
		      if(data!=null)
		      {
		    	
		    	  /**���������߳�*/
		    	  YuvImage image = new YuvImage(data,VideoFormatIndex, VideoWidth, VideoHeight,null);
		        if(image!=null)
		        {
		        	ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		      	  	//�� ������ͼƬ�ĳߴ������ 
		            outstream.write(top_buffer);
		         
		      	  	image.compressToJpeg(new Rect(0, 0, (int)(VideoWidthRatio*VideoWidth), 
		      	  		(int)(VideoHeightRatio*VideoHeight)), VideoQuality, outstream);
		      	  
		             total_number=outstream.size();
		             if (total_number>65536)
		            	 return;
		      	     	outstream.flush();
		      	    //total_number=p_number+5*4;
		      	   
				  	//�����߳̽�ͼ�����ݷ��ͳ�ȥ
		      	  	Thread th = new MySendFileThread(outstream,tempSocket, total_number);
		      	  	th.start();  
		      	//  	sign=false;
		      	  
		        }
		      }
		  } catch (IOException e) {
		      e.printStackTrace();
		  }
	}

   
	
	/**�����ļ��߳�*/
	class MySendFileThread extends Thread{
    	//private byte byteBuffer[] = new byte[1024];
    	
    	private OutputStream outsocket;	
    	private ByteArrayOutputStream myoutputstream;
    	private Socket tempSocket;
    	private int tot_num;
    	
    	public MySendFileThread(ByteArrayOutputStream myoutputstream,Socket socket, int tot_num) {
			// TODO Auto-generated constructor stub
    		tempSocket = socket;
    		this.myoutputstream=myoutputstream;
    		this.tot_num = tot_num;
    		try{
    			myoutputstream.close();
    		}catch (Exception e) {
				// TODO: handle exception
    			e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {//������ͼ��ͨ���ļ����͹�ȥ
			//	Socket tempSocket = new Socket(ipname, port);
				if(tot_num>=65536)
					return;
				outsocket=tempSocket.getOutputStream();
				
				//String msgHead=java.net.URLEncoder.encode("PHONEVIDEO|"+username+"|","utf-8");
				//byte[] buffer=msgHead.getBytes();
				//outsocket.write(buffer);
				
				byte[] data_buffer=myoutputstream.toByteArray();
				
				//д������ͷ2 3����λ����   �����ݰ����ܳ��ȡ�
				int number=tot_num;
	      	  	for(int i=3;i>=2;i--)
	      	  	{
	      	  	data_buffer[i] = (byte) (number% 256);
	      	    number>>= 8;
	      	  	}
	      	  	
	      	  	
	           //��ʾ��1,д��ͷ��4.5������ŵ�λ����   	  	
	      	     ssrc++;
			     int ssrc_2=ssrc;
			     for (int i=5; i >= 4; i--) {
						data_buffer[i] = (byte) (ssrc_2% 256);
						ssrc_2 >>= 8;
			     }
				
				
			  int crcint=CRC32_change(data_buffer,tot_num);
				   
				   for (int i=9; i >= 8; i--) {
						data_buffer[i] = (byte) (crcint%256);
						crcint>>= 8;
			     }
				   outsocket.write(data_buffer,0,tot_num);
			//	int amount;
			//	while((amount = inputstream.read(byteBuffer)) != -1){
			//		outsocket.write(top_buffer);
			//		outsocket.write(byteBuffer, 0, amount);
					
			//	}
				outsocket.flush();
			//	outsocket.close();
		     	
			
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	/**CRC��֤ģ��*/
	public static int CRC32_change(byte[] data,int number){
		CRC32 crc=new CRC32();	
		for(int i=0;i<number;i++)
		{
			crc.update(data[i]);
		}
		   long crclong=crc.getValue();
		   int crcInt=(int)crclong&0xffff;	
		   return crcInt;
	}
	/**
	* byte[]ת����int��
	* 
	* @param data
	*            ����int��byte[]
	* @param offset
	*            ƫ����
	* @return int��
	*/
	public static int bytesToInt(byte[] data, int offset) {
	   int num = 0;
	   for (int i = offset; i < offset + 2; i++) {
	    num <<= 8;
	    num |= (data[i] & 0xff);
	   }
	   return num;
	}
	
	
/**    ����TCP�������� ����ûظ�
 *    �ظ���ȷ��   �����Ƿ�ɹ�������ȡ����14,15����λ��������Ķ˿ڡ�
  *        IP  CRC��֤     �����

	*/
	//���߳���������ն�����˿ں�
	class ReceiveCommandThread extends Thread{
		MainActivity mainActivity; //�����趨�˿ںŵĳ���
		private	int t_port;
		private int ssrc_1;
		private int begin=4;
		private int end=6;
		boolean max;
		
		ReceiveCommandThread(MainActivity activity)
		{ 
			mainActivity=activity;	
		}
		public void run()
		{
			try{
				while(true)
				{    
					
					byte [] bufferforsend =new byte [24];   //���ڷ��͵�byte��
					for(int i=0; i<bufferforsend.length;i++)
					{
						bufferforsend[i]='\0';
					}
					bufferforsend[0]=(byte)Integer.parseInt("00010100",2);//�汾��Ϊ1 ͷ������λ3*32
					bufferforsend[1]=(byte)Integer.parseInt("00100011",2);//����ѡ��
					bufferforsend[3]=(byte)Integer.parseInt("00011000",2);//�ܵ��ֽڳ���24
					bufferforsend[6]=(byte)Integer.parseInt("01000000",2);
				    bufferforsend[15]=(byte)Integer.parseInt("00000111",2);
					/**����һ�������  ���ڵ�4,5�����ֽ���*/
					ssrc_1= new Random().nextInt();   //�������־SSRC
					ssrc=ssrc_1;
					for (end--; end >= begin; end--) {
						bufferforsend[end] = (byte) (ssrc_1% 256);
						ssrc_1 >>= 8;
					}//*����һ�������  ���ڵ�4,5�����ֽ���
					
					//���������ݽ���crcУ��������  �����ڵ�8,9�����ֽ���
					int crcint=CRC32_change(bufferforsend,24);
					for (int i=9;  i>= 8; i--) {
						bufferforsend[i] = (byte) (crcint% 256);
						crcint >>= 8;
					}
					
					Socket socket = new Socket(mainActivity.getServerUrl(),mainActivity.getCommandPort());	
					OutputStream os=socket.getOutputStream();
					//os.write(new String("getport").getBytes("utf-8"));
					os.write(bufferforsend);  //����TCP����
					InputStream br = socket.getInputStream();
					byte [] buffer =new byte[64];
					for(int i=0; i<buffer.length;i++)
					{
						buffer[i]='\0';
					}
	                /**������Ϻ󣬵ȴ�������Ϣ*/				
					br.read(buffer);  //�����������׼�����ܶ˿ڵķ�����Ϣ					
					if(buffer != null) //
					{  if((buffer[12]>=0)&&(buffer[13]%2==1)) //�������˿ڳɹ�
					    {
					      t_port=bytesToInt(buffer, 14);		
					      mainActivity.setPort(t_port);
					      ID_number[0]=buffer[20];
					      ID_number[1]=buffer[21];
					      
					      //��ȡIP��ַ
					    
					      
					     /**�������֮�󣬿�ʼ��д������������Ҫ������ͷ*/
					      for(int i=0; i<top_buffer.length;i++)
							{
								top_buffer[i]='\0';
							}
					         top_buffer[0]=(byte)Integer.parseInt("00010101",2);
						     top_buffer[1]=(byte)Integer.parseInt("01000111",2);
						     
						     top_buffer[12]=ID_number[0];
						     top_buffer[13]=ID_number[1];
						   }
					 //  else
						//   {Toast.makeText(getApplicationContext(), "���Ӵ�����رպ���������", Toast.LENGTH_SHORT).show();
						 //  }

						   //content=content.trim();
						   //int t_port= Integer.parseInt( content.substring( content.indexOf("port:")+5) );
					   // startSendVideo=true;					
						socket.close();
						return;
					}	
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
				
		}
			
	}
	
	
	
	/**
	 * ����������̣߳���Ҫ���ͽ���������ͻ��ˡ�
	 * @author Administrator
	 *
	 */
	class EndReceiveThread extends Thread{
		MainActivity mainActivity;
		EndReceiveThread(MainActivity activity)
		{ 
			mainActivity=activity;
		}
		public void run() 
	
		     {startSendVideo=false;
			  try{	
		    	 byte [] bufferforend=new byte [28];
		    	 for(int i=0; i<bufferforend.length;i++)
					{
						bufferforend[i]='\0';
					}
		    	 bufferforend[0]=(byte)Integer.parseInt("00010101",2);//�汾��
		    	 bufferforend[1]=(byte)Integer.parseInt("00101011",2);//����ѡ��
		    	 bufferforend[3]=(byte)Integer.parseInt("00011100",2);//����Ϊ28�ֽ�
		    	 bufferforend[19]=(byte)Integer.parseInt("00000111",2);
		    	 bufferforend[12]=ID_number[0]; //
		    	 bufferforend[13]=ID_number[1];
		    	 
		    	 //ʵ��crc��֤   ����ֵ�ŵ�8,9����λ���ϡ�
		    	 int crcint=CRC32_change(bufferforend,28);
		    	   for (int i=9; i >= 8; i--) {
						bufferforend[i] = (byte) (crcint%256);
						crcint>>= 8;
			     }
		    	 
		    	 
		    	 Socket socket = new Socket(mainActivity.getServerUrl(),mainActivity.getCommandPort());	
					OutputStream os=socket.getOutputStream();
					os.write(bufferforend);   // �����ر�����
					InputStream br = socket.getInputStream();
				    
					 byte [] buffer=new byte [64];
			    	 for(int i=0; i<buffer.length;i++)
						{
							buffer[i]='\0';
						}
			    	 br.read(buffer);
			    	 socket.close();
			    	 tempSocket.close(); 	 
			    	 
			    //	 Thread.sleep(3000);
				//	 android.os.Process.killProcess(android.os.Process.myPid());
		      }
		    catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		    }
		}
		
	}
}/**main�����Ľ���*/