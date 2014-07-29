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
	private int p_number;//当前图片大小
	private int total_number;//该包的总长度
	/**用户名*/
    private String pUsername="wh";
    /**服务器地址*/
    private String serverUrl="192.168.1.107";
    /**服务器端口*/
    private int commandPort=2012;
    private int serverPort=-1;
    /**视频刷新间隔*/
    private int VideoPreRate=1;
    /**当前视频序号*/
    private int tempPreRate=0;
    /**视频质量*/
    private int VideoQuality=85;
    
    /**发送视频宽度比例*/
    private float VideoWidthRatio=1;
    /**发送视频高度比例*/
    private float VideoHeightRatio=1;
    
    /**发送视频宽度*/
    private int VideoWidth=320;
    /**发送视频高度*/ 
    private int VideoHeight=240;
    /**视频格式索引*/
    private int VideoFormatIndex=0;
    /**是否发送视频*/
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
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); ////设置窗体始终点亮
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		
		setContentView(R.layout.activity_main);
		
		//final ReceiveCommandThread commandThread = new ReceiveCommandThread(this);//实例化请求端口的进程
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
					final ReceiveCommandThread commandThread = new ReceiveCommandThread(MainActivity.this);//实例化请求端口的进程
					commandThread.start();
				}
				else
				{  
					final EndReceiveThread endThread=new EndReceiveThread(MainActivity.this);
					endThread.start();
					
					mBtnTransmit.setText("等待连接");
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
					mBtnTransmit.setText("数据传输中");
					startSendVideo=true;
					whether_connect=false;
		//		}
			}
		});
	}

	@Override
	/**各种赋值  初始设定*/
	protected void onStart() {
		// TODO Auto-generated method stub
		mSurfaceHolder = mSurfaceView.getHolder(); // 绑定SurfaceView，取得SurfaceHolder对象
		mSurfaceHolder.addCallback(this); // SurfaceHolder加入回调接口       
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 设置显示器类型，setType必须设置   缓冲类型
	    //读取配置文件
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
			mCamera=Camera.open(); //打开摄像头
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try{//退出camera，关闭摄像头
	        if (mCamera != null) {
	        	mCamera.setPreviewCallback(null); // ！！这个必须在前，不然退出出错
	            mCamera.stopPreview();
	            mCamera.release();
	            mCamera = null;
	        } 
	    } catch (Exception e) {
	        e.printStackTrace();
	    } 
	}

	@Override
	//定义选项列表
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
    	menu.add(0,0,0,"系统设置");
    	menu.add(0,1,1,"退出程序"); 
    	return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);//获取菜单
    	switch(item.getItemId())//菜单序号
    	{
    		case 0:
    			//系统设置
    			{
    				Intent intent=new Intent(this,SettingActivity.class);
    				startActivity(intent);  
    			}
    			break;   			
        		case 1://退出程序
	    		{
	    			//杀掉线程强制退出
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
        mCamera.setDisplayOrientation(90); //设置横行录制
        //获取摄像头参数
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
            mCamera.setPreviewCallback(null); // ！！这个必须在前，不然退出出错
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
		
	}
   //设置变量发送图片
	
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
		    	
		    	  /**发送命令线程*/
		    	  YuvImage image = new YuvImage(data,VideoFormatIndex, VideoWidth, VideoHeight,null);
		        if(image!=null)
		        {
		        	ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		      	  	//在 此设置图片的尺寸和质量 
		            outstream.write(top_buffer);
		         
		      	  	image.compressToJpeg(new Rect(0, 0, (int)(VideoWidthRatio*VideoWidth), 
		      	  		(int)(VideoHeightRatio*VideoHeight)), VideoQuality, outstream);
		      	  
		             total_number=outstream.size();
		             if (total_number>65536)
		            	 return;
		      	     	outstream.flush();
		      	    //total_number=p_number+5*4;
		      	   
				  	//启用线程将图像数据发送出去
		      	  	Thread th = new MySendFileThread(outstream,tempSocket, total_number);
		      	  	th.start();  
		      	//  	sign=false;
		      	  
		        }
		      }
		  } catch (IOException e) {
		      e.printStackTrace();
		  }
	}

   
	
	/**发送文件线程*/
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
			try {//讲数据图像通过文件发送过去
			//	Socket tempSocket = new Socket(ipname, port);
				if(tot_num>=65536)
					return;
				outsocket=tempSocket.getOutputStream();
				
				//String msgHead=java.net.URLEncoder.encode("PHONEVIDEO|"+username+"|","utf-8");
				//byte[] buffer=msgHead.getBytes();
				//outsocket.write(buffer);
				
				byte[] data_buffer=myoutputstream.toByteArray();
				
				//写出数据头2 3两个位置上   该数据包的总长度。
				int number=tot_num;
	      	  	for(int i=3;i>=2;i--)
	      	  	{
	      	  	data_buffer[i] = (byte) (number% 256);
	      	    number>>= 8;
	      	  	}
	      	  	
	      	  	
	           //标示加1,写入头部4.5两个编号的位置中   	  	
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
	
	/**CRC验证模块*/
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
	* byte[]转换成int数
	* 
	* @param data
	*            包括int的byte[]
	* @param offset
	*            偏移量
	* @return int数
	*/
	public static int bytesToInt(byte[] data, int offset) {
	   int num = 0;
	   for (int i = offset; i < offset + 2; i++) {
	    num <<= 8;
	    num |= (data[i] & 0xff);
	   }
	   return num;
	}
	
	
/**    发送TCP请求连接 并获得回复
 *    回复中确定   连接是否成功，并获取组中14,15两个位置所代表的端口。
  *        IP  CRC验证     待完成

	*/
	//该线程用于向接收端请求端口号
	class ReceiveCommandThread extends Thread{
		MainActivity mainActivity; //用来设定端口号的程序
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
					
					byte [] bufferforsend =new byte [24];   //用于发送的byte组
					for(int i=0; i<bufferforsend.length;i++)
					{
						bufferforsend[i]='\0';
					}
					bufferforsend[0]=(byte)Integer.parseInt("00010100",2);//版本号为1 头部数据位3*32
					bufferforsend[1]=(byte)Integer.parseInt("00100011",2);//功能选择
					bufferforsend[3]=(byte)Integer.parseInt("00011000",2);//总的字节长度24
					bufferforsend[6]=(byte)Integer.parseInt("01000000",2);
				    bufferforsend[15]=(byte)Integer.parseInt("00000111",2);
					/**生成一段随机数  放在第4,5两个字节内*/
					ssrc_1= new Random().nextInt();   //随机数标志SSRC
					ssrc=ssrc_1;
					for (end--; end >= begin; end--) {
						bufferforsend[end] = (byte) (ssrc_1% 256);
						ssrc_1 >>= 8;
					}//*生成一段随机数  放在第4,5两个字节内
					
					//对整个数据进行crc校验码生成  并发在第8,9两个字节内
					int crcint=CRC32_change(bufferforsend,24);
					for (int i=9;  i>= 8; i--) {
						bufferforsend[i] = (byte) (crcint% 256);
						crcint >>= 8;
					}
					
					Socket socket = new Socket(mainActivity.getServerUrl(),mainActivity.getCommandPort());	
					OutputStream os=socket.getOutputStream();
					//os.write(new String("getport").getBytes("utf-8"));
					os.write(bufferforsend);  //发出TCP请求。
					InputStream br = socket.getInputStream();
					byte [] buffer =new byte[64];
					for(int i=0; i<buffer.length;i++)
					{
						buffer[i]='\0';
					}
	                /**发送完毕后，等待接受消息*/				
					br.read(buffer);  //发送完请求后，准备接受端口的反馈信息					
					if(buffer != null) //
					{  if((buffer[12]>=0)&&(buffer[13]%2==1)) //如果分配端口成功
					    {
					      t_port=bytesToInt(buffer, 14);		
					      mainActivity.setPort(t_port);
					      ID_number[0]=buffer[20];
					      ID_number[1]=buffer[21];
					      
					      //获取IP地址
					    
					      
					     /**接收完毕之后，开始填写传输数据所需要的数据头*/
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
						//   {Toast.makeText(getApplicationContext(), "连接错误，请关闭后重新连接", Toast.LENGTH_SHORT).show();
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
	 * 结束任务的线程，需要发送结束命令给客户端。
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
		    	 bufferforend[0]=(byte)Integer.parseInt("00010101",2);//版本号
		    	 bufferforend[1]=(byte)Integer.parseInt("00101011",2);//功能选择
		    	 bufferforend[3]=(byte)Integer.parseInt("00011100",2);//长度为28字节
		    	 bufferforend[19]=(byte)Integer.parseInt("00000111",2);
		    	 bufferforend[12]=ID_number[0]; //
		    	 bufferforend[13]=ID_number[1];
		    	 
		    	 //实现crc验证   讲该值放到8,9两个位置上。
		    	 int crcint=CRC32_change(bufferforend,28);
		    	   for (int i=9; i >= 8; i--) {
						bufferforend[i] = (byte) (crcint%256);
						crcint>>= 8;
			     }
		    	 
		    	 
		    	 Socket socket = new Socket(mainActivity.getServerUrl(),mainActivity.getCommandPort());	
					OutputStream os=socket.getOutputStream();
					os.write(bufferforend);   // 发出关闭请求。
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
}/**main函数的结束*/