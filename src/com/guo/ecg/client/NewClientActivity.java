package com.guo.ecg.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;

import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import kankan.wheel.widget.NumericWheelAdapter;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.TextWheelAdapter;
import kankan.wheel.widget.WheelView;

import com.baidu.location.LocationClient;
import com.pku.wcsp.upgrade.CheckVersion;

import android.location.LocationManager;
import com.other.view.copy.SpinnerDialog;
import com.other.view.copy.StatusDialog;
import com.other.view.copy.TextDialog;
import com.pku.wcsp.ecg.BMService;
import com.pku.wcsp.ecg.BluetoothManager;
import com.pku.wcsp.ecg.DeviceMessage;
import com.pku.wcsp.ecg.ECGfactory;
import com.pku.wcsp.ecg.Reciever;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NewClientActivity extends Activity {
    /** Called when the activity is first created. */
	private LocationClient mLocClient;
	static Vibrator vibrator;
	PowerManager.WakeLock mWakeLock;
	SharedPreferences sp;
	Bitmap toSaveBitmap;
	final String[] str = new String[]{"abc", "edf", "sffaf", "sdla","djajd","sdkla","sdkjl"};
	int GET = 40;
	public static int WIDTH;
	public static int HEIGHT;
	public static int RIGHT;
	private int oldX,oldY,oldX_pulse,oldY_pulse,oldX_alarm,oldY_alarm;
	public SurfaceView ecg_view;
	public SurfaceView xl_view;
	private RadioGroup group;
	private RadioButton rbt;
	private static TextView heartRateValue;
	private SurfaceHolder ecg_holder;
	private SurfaceHolder xl_holder;
	public TextView tv1;
	public TextView tv2;
	private static TextView status;
	private static int statusValue;
	private static ImageView iv1;
	private ImageView iv2;
	private static ImageView iv3;
	private static ImageView iv4;
	private ImageView iv5;
	private static ImageView inputoverpic;
	private static ImageView leaddroppic;
	private static ImageView heartRateHighPic;
	private static ImageView heartRateLowPic;
	private static ImageView STAlarmPic;
	private static ImageView TAlarmPic;
	private static ImageView manual_alarm;
	private static ImageView fallAlarmPic;
	private static TextView inputover, heartratehigh, stalarm, talarm, heartratelow, leaddrop, manualalarm, fallalarm;
	private static TextView ecgAlarmText, moveAlarmText, positionAlarmText, deviceAlarmText;
	private static ImageView ecgAlarmPic,moveAlarmPic,positionAlarmPic,deviceAlarmPic;
	Timer drawTimer,drawTimer2;
	DrawTask drawTask;
	DrawTask2 drawTask2;
	boolean tag = false;
	boolean tag2 = false;
	public static Context THIS;
	static String ServerIP;
	private static BluetoothAdapter adapter;
	private static BluetoothManager manager;
	private boolean bluetoothTag = false;
//	private static boolean playing = false;
	private boolean historyMode = false;
	private boolean newEdition = true;
	public static int LeadNumber = 12;
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
		if(keyCode == KeyEvent.KEYCODE_BACK ){
			new AlertDialog.Builder(this)
			.setTitle("�˳�")
			.setMessage("ȷ��Ҫ�˳���")
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					onDestroy();
				}
			})
			.setNegativeButton("ȡ��", null)
			.show();
			return true;
		}
		return false;
	}
	
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0, 1, 1, "����ģʽ");
		menu.add(0, 2, 2, "������Ϣ");
    	menu.add(0, 3, 3, "�豸��Ϣ");
    	menu.add(0, 4, 4, "������Ϣ");
    	menu.add(0, 5, 5, "����������");
    	menu.add(0, 6, 6, "����");
    	return super.onCreateOptionsMenu(menu);
    }
	
    public boolean onOptionsItemSelected(MenuItem item){
    	if(item.getItemId() == 1){
    		if(manager.isTransporting()){
    			SendMessage(handler,100);
    		}else{
    			
    			LinearLayout linearLayout = new LinearLayout(THIS);
    			linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    		    linearLayout.setOrientation(LinearLayout.VERTICAL);
    		    linearLayout.setGravity(Gravity.CENTER);
    		    
    			RadioButton normal,alarm,history,test;
    			normal = new RadioButton(THIS);
    		    normal.setId(2);
    		    normal.setText("ʵʱ����");
    		    alarm = new RadioButton(THIS);
    		    alarm.setText("��������");
    		    alarm.setId(3);
    		    history = new RadioButton(THIS);
    		    history.setText("��ʷ����");
    		    history.setId(4);
    		    test = new RadioButton(THIS);
    		    test.setText("�ѻ�����");
    		    test.setId(1);
    		    
    		    RadioGroup radioGroup = new RadioGroup(THIS);
    		    radioGroup.setOrientation(LinearLayout.VERTICAL);
    		    radioGroup.addView(normal,new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		    radioGroup.addView(alarm,new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		    radioGroup.addView(history,new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		    radioGroup.addView(test,new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    			linearLayout.addView(radioGroup);
    			
    			if(!manager.isconnected())
    		    	 test.setChecked(true);
    		     else {
    		    	 switch(manager.getWorkMode()){
    		    	 case 0:
    		    		 test.setChecked(true);
    		    		 break;
    		    	 case 1:
    		    		 test.setChecked(true);
    		    		 break;
    		    	 case 2:
    		    		 normal.setChecked(true);
    		    		 break;
    		    	 case 3:
    		    		 alarm.setChecked(true);
    		    		 break;
    		    	 case 4:
    		    		 history.setChecked(true);
    		    		 break;
    		    	 }
    		     }
    		     
    		     radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

    				public void onCheckedChanged(RadioGroup group, int checkedId) {
    					// TODO Auto-generated method stub
    					Message msg = statusHandler.obtainMessage();
    					msg.what = checkedId;
    					statusHandler.sendMessage(msg);
    				}
    		     });
    			
    			new AlertDialog.Builder(this)
    			.setTitle("ѡ����ģʽ")
    			.setView(linearLayout)
    			.setPositiveButton("ȷ��", null)
    			.show();
    		}
    		
    	}else if(item.getItemId() == 2){
    		if(manager.isTransporting()){
    			SendMessage(handler,100);
    		}else{
    			LinearLayout linearLayout = new LinearLayout(THIS);
    			linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    		    linearLayout.setOrientation(LinearLayout.VERTICAL);
    		    linearLayout.setGravity(Gravity.CENTER);
    		    
    		    TextView tv1,tv2,tv3;
    		    final EditText value1;
				final EditText value2;
				final EditText value3;
    		    
    		    tv1 = new TextView(THIS);
    		    tv1.setText("��������");
    		    linearLayout.addView(tv1);
    		    value1 = new EditText(THIS);
    		    String patientName = sp.getString("patientName", "����");
    		    value1.setText(patientName);
    		    linearLayout.addView(value1,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    		    
    		    tv2 = new TextView(THIS);
    		    tv2.setText("����ID");
    		    linearLayout.addView(tv2);
    		    value2 = new EditText(THIS);
    		    value2.setInputType(InputType.TYPE_CLASS_NUMBER);
    		    String patientID = sp.getString("patientID", "4");
    		    value2.setText(patientID);
    		    linearLayout.addView(value2,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    		    
    		    tv3 = new TextView(THIS);
    		    tv3.setText("���ű���������");
    		    linearLayout.addView(tv3);
    		    value3 = new EditText(THIS);
    		    String patientAge = sp.getString("patientAge", "60");
    		    value3.setText(patientAge);
    		    linearLayout.addView(value3,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    		        		    
    		    new AlertDialog.Builder(this)
    			.setTitle("���ò�����Ϣ")
    			.setView(linearLayout)
    			.setPositiveButton("ȷ��",  new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						SharedPreferences.Editor editor = sp.edit();
						editor.putString("patientName", value1.getText().toString());
						editor.putString("patientID", value2.getText().toString());
						Reciever.PATIENT_ID = Integer.parseInt(value2.getText().toString());
						editor.putString("patientAge", value3.getText().toString());
						editor.commit();
						Toast.makeText(THIS, "��Ϣ�ѱ���", Toast.LENGTH_SHORT).show();
					}
    				
    			})
    			.setNegativeButton("ȡ��", null)
    			.show();
    		}
    	}else if(item.getItemId() == 3){
    		if(manager.isTransporting()){
    			SendMessage(handler,100);
    		}else{
    			Date date = new Date(System.currentTimeMillis());
        		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ;
        		new AlertDialog.Builder(this)
        		.setTitle("�豸��Ϣ")
        		.setMessage("���ƣ�"+"ECG-BLUE1"+"\n"+"���ͣ�"+"�ĵ�"+"\n"+"ʱ�䣺"+df.format(date))
        		.setNegativeButton("ȷ��", null)
        		.setNeutralButton("����", new DialogInterface.OnClickListener() {
    				
    				public void onClick(DialogInterface dialog, int which) {
    					// TODO Auto-generated method stub
    					final LinearLayout set_device_layout = (LinearLayout)getLayoutInflater()
    							.inflate(R.layout.set_device, null);
//    					RadioButton bt = (Button)findViewById(R.id.ecg_device);
    					new AlertDialog.Builder(NewClientActivity.this)
    						.setTitle("�����豸��Ϣ")
    						.setView(set_device_layout)
    						.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
    							
    							public void onClick(DialogInterface dialog, int which) {
    								// TODO Auto-generated method stub
//    								Toast.makeText(NewClientActivity.this, "���óɹ���", Toast.LENGTH_SHORT).show();
    							}
    						})
    						.setNegativeButton("ȡ��", null)
    						.show();
    				}
    			})
    			.show();
    		}
    	}
    	else if(item.getItemId() == 4){
    		if(manager.isTransporting()){
    			SendMessage(handler,100);
    		}else{
//    			SpinnerDialog dialog = new SpinnerDialog(THIS);
//        		dialog.show();
    			LinearLayout linearLayout = new LinearLayout(THIS);
    			linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    		    linearLayout.setOrientation(LinearLayout.VERTICAL);
    		    linearLayout.setGravity(Gravity.CENTER);
    		    
    		    String[] alarms = {"���ʸ�","���ʵ�","ST���쳣","T���쳣"};
    		    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(THIS,android.R.layout.simple_spinner_item, alarms);
    		    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		    TextView tv1,tv2,tv3;
    		    tv1 = new TextView(THIS);
    		    tv1.setText("ѡ�񱨾�");
    		    Spinner spinner = new Spinner(THIS);
    		    spinner.setAdapter(arrayAdapter);
    		    tv2 = new TextView(THIS);
    		    tv2.setText("������ֵ");
    		    tv3 = new TextView(THIS);
    		    tv3.setText("");
    		    
    		    linearLayout.addView(tv1);
    		    linearLayout.addView(spinner);
    		    linearLayout.addView(tv2);
    		    
    		    EditText value = new EditText(THIS);
    		    linearLayout.addView(value,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    		    
    		    LinearLayout linearLayout2 = new LinearLayout(THIS);
    			linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    		    linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
    		    
    		    new AlertDialog.Builder(this)
    			.setTitle("���ñ�����ֵ")
    			.setView(linearLayout)
    			.setPositiveButton("ȷ��", null)
    			.setNegativeButton("ȡ��", null)
    			.show();
    		}
    	}
    	else if(item.getItemId() == 5){
    		if(manager.isTransporting()){
    			SendMessage(handler,100);
    		}else{
    			LinearLayout linearLayout = new LinearLayout(THIS);
    			linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    		    linearLayout.setOrientation(LinearLayout.VERTICAL);
    		    linearLayout.setGravity(Gravity.CENTER);
    		    
    		    TextView tv1,tv2,tv3;
    		    final EditText value1;
				final EditText value2;
				final EditText value3;
    		    
    		    tv1 = new TextView(THIS);
    		    tv1.setText("Զ�̼�ط�����");
    		    linearLayout.addView(tv1);
    		    value1 = new EditText(THIS);
    		    String serverAddress = sp.getString("serverAddress", "162.105.76.212");
    		    value1.setText(serverAddress);
    		    linearLayout.addView(value1,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    		    
    		    tv2 = new TextView(THIS);
    		    tv2.setText("�ʼ�����������");
    		    linearLayout.addView(tv2);
    		    value2 = new EditText(THIS);
    		    String mailAddress = sp.getString("mailAddress", "pku_wcsp@126.com");
    		    value2.setText(mailAddress);
    		    linearLayout.addView(value2,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    		    
    		    tv3 = new TextView(THIS);
    		    tv3.setText("���ű���������");
    		    linearLayout.addView(tv3);
    		    value3 = new EditText(THIS);
    		    String smsAddress = sp.getString("smsAddress", "15901111133");
    		    value3.setText(smsAddress);
    		    linearLayout.addView(value3,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    		        		    
    		    new AlertDialog.Builder(this)
    			.setTitle("���÷�����")
    			.setView(linearLayout)
    			.setPositiveButton("ȷ��",  new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						SharedPreferences.Editor editor = sp.edit();
						editor.putString("serverAddress", value1.getText().toString());
						editor.putString("mailAddress", value2.getText().toString());
						editor.putString("smsAddress", value3.getText().toString());
						editor.commit();
						Toast.makeText(THIS, "��Ϣ�ѱ���", Toast.LENGTH_SHORT).show();
					}
    				
    			})
    			.setNegativeButton("ȡ��", null)
    			.show();
    		}
    	}
    	else if(item.getItemId() == 6){
    		if(manager.isTransporting()){
    			SendMessage(handler,100);
    		}else{
    			final LinearLayout aboutLayout = (LinearLayout)getLayoutInflater()
						.inflate(R.layout.about, null);
    		    new AlertDialog.Builder(this)
    		    .setTitle("����")
    		    .setView(aboutLayout)
    			.setPositiveButton("ȷ��", null)
    			.setNegativeButton("����",  new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						final LinearLayout help_layout = (LinearLayout)getLayoutInflater()
								.inflate(R.layout.help, null);
						new AlertDialog.Builder(NewClientActivity.this)
						.setTitle("����")
						.setView(help_layout)
						.setNegativeButton("ȷ��", null)
						.show();
					}
    				
    			})
    			.show();
    		}
    	}
    	return super.onOptionsItemSelected(item);
    }
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);    
        //ȫ��    
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,      
        WindowManager.LayoutParams. FLAG_FULLSCREEN);
        WIDTH = getWindowManager().getDefaultDisplay().getWidth();
        HEIGHT = getWindowManager().getDefaultDisplay().getHeight();
        Log.e("WIDTH",""+WIDTH);
        Log.e("HEIGHT",""+HEIGHT);
        if(HEIGHT == 800){
        	setContentView(R.layout.main3);
        	RIGHT = WIDTH*4/5;
            RIGHT = 1008;
        }
        else{
        	setContentView(R.layout.main);
        	RIGHT = WIDTH*4/5;
            RIGHT = 888;
        }
        Log.e("right",""+RIGHT);
        
        CheckVersion  checkVersion = new CheckVersion(this);
		checkVersion.start();
		Log.e("updade","update start");
        
        mLocClient = ((Location)this.getApplication()).mLocationClient;
        
        sp = getSharedPreferences("my_data", Context.MODE_PRIVATE);
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,"XYTEST");
        mWakeLock.acquire();
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        
        startService(new Intent(this,BMService.class));//������̨Service
        manager = new BluetoothManager();
        
        iv1 = (ImageView)findViewById(R.id.imageView1);
        iv2 = (ImageView)findViewById(R.id.imageView2);
        iv3 = (ImageView)findViewById(R.id.imageView3);
        iv4 = (ImageView)findViewById(R.id.imageView4);
        iv5 = (ImageView)findViewById(R.id.imageView5);
        status = (TextView)findViewById(R.id.status);
        inputoverpic = (ImageView)findViewById(R.id.inputoverpic);
        manual_alarm = (ImageView)findViewById(R.id.otheralarmpic1);
        leaddroppic = (ImageView)findViewById(R.id.leaddroppic);
        heartRateHighPic = (ImageView)findViewById(R.id.heartratehighpic);
    	heartRateLowPic = (ImageView)findViewById(R.id.heartratelowpic);
    	STAlarmPic = (ImageView)findViewById(R.id.st_alarmpic);
    	TAlarmPic = (ImageView)findViewById(R.id.t_alarmpic);
    	fallAlarmPic = (ImageView)findViewById(R.id.falldownpic);
    	
    	heartratehigh = (TextView)findViewById(R.id.heartratehigh);
    	stalarm = (TextView)findViewById(R.id.st_alarm);
    	talarm = (TextView)findViewById(R.id.t_alarm);
    	heartratelow = (TextView)findViewById(R.id.heartratelow);
    	leaddrop = (TextView)findViewById(R.id.leaddrop);
    	manualalarm = (TextView)findViewById(R.id.otheralarm1);
    	fallalarm = (TextView)findViewById(R.id.otheralarm2);
    	inputover = (TextView)findViewById(R.id.inputover);
    	
    	ecgAlarmPic = (ImageView)findViewById(R.id.ecgalarmpic);
    	moveAlarmPic = (ImageView)findViewById(R.id.movealarmpic);
    	positionAlarmPic = (ImageView)findViewById(R.id.positionalarmpic);
    	deviceAlarmPic = (ImageView)findViewById(R.id.devicealarmpic);
    	
    	ecgAlarmText = (TextView)findViewById(R.id.ecgalarm);
    	moveAlarmText = (TextView)findViewById(R.id.movealarm);
    	positionAlarmText = (TextView)findViewById(R.id.positionalarm);
    	deviceAlarmText = (TextView)findViewById(R.id.devicealarm);
    	
        heartRateValue = (TextView)findViewById(R.id.xl_text);
        adapter = BluetoothAdapter.getDefaultAdapter();
        THIS = this;
        ServerIP = sp.getString("serverAddress", "162.105.76.212");
        Reciever.PATIENT_ID = Integer.parseInt(sp.getString("patientID", "4"));
		
        Log.e("Height",""+HEIGHT);
        ecg_view = (SurfaceView)findViewById(R.id.ecg_show);
        xl_view = (SurfaceView)findViewById(R.id.xl_show);
        initWheel(R.id.passw_1);
//        initWheel2(R.id.passw_2);
//        group = (RadioGroup)findViewById(R.id.radioGroup);
        
//        listView = (ListView)findViewById(R.id.listView1);
//        tv1 = (TextView)findViewById(R.id.textView1);
//        tv2 = (TextView)findViewById(R.id.textView2);
        ecg_view.setZOrderOnTop(true);
        xl_view.setZOrderOnTop(true);
        ecg_view.setDrawingCacheEnabled(true);
        xl_view.setDrawingCacheEnabled(true);
        
        ecg_view.getHolder().setFormat(PixelFormat.TRANSPARENT);
        xl_view.getHolder().setFormat(PixelFormat.TRANSPARENT);
        
        oldX = 0;
        oldX_pulse = 0;
        oldX_alarm = 0;
//        rbt = (RadioButton)findViewById(R.id.radio_lead1);
//        rbt.setChecked(true);
        creatSDDir("ECG-Shot");
        
        new AlertDialog.Builder(this)
		.setTitle("��ʼ")
		.setMessage("Ҫ�����������豸��")
		.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				startBluetooth();
				bluetoothTag = true;
			}
		})
		.setNegativeButton("ȡ��", null)
		.show();
        
        ecg_view.setOnClickListener(new ImageView.OnClickListener(){

			public void onClick(View v) {
				
			}
        	
        });
        xl_view.setOnClickListener(new SurfaceView.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(newEdition){
					Message msg = editionChangeHandler.obtainMessage();
					msg.what = 1;
					editionChangeHandler.sendMessage(msg);
					newEdition = false;
				}else{
					Message msg = editionChangeHandler.obtainMessage();
					msg.what = 2;
					editionChangeHandler.sendMessage(msg);
					newEdition = true;
				}
			}
        	
        });
        
        
        
/*        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				
				int LEAD = group.getCheckedRadioButtonId();
				switch(LEAD){
				case R.id.radio_lead1:
					BufferingThread.LEAD = 1;
					Log.v("LEAD","ѡ����1����");
					break;
				case R.id.radio_lead2:
					BufferingThread.LEAD = 2;
					Log.v("LEAD","ѡ����2����");
					break;
				case R.id.radio_lead3:
					BufferingThread.LEAD = 3;
					Log.v("LEAD","ѡ����3����");
					break;
				case R.id.radio_lead4:
					BufferingThread.LEAD = 4;
					Log.v("LEAD","ѡ����4����");
					break;
				case R.id.radio_lead5:
					BufferingThread.LEAD = 5;
					Log.v("LEAD","ѡ����5����");
					break;
				case R.id.radio_lead6:
					BufferingThread.LEAD = 6;
					Log.v("LEAD","ѡ����6����");
					break;
				case R.id.radio_lead7:
					BufferingThread.LEAD = 7;
					Log.v("LEAD","ѡ����7����");
					break;
				}
			}
		});
*/  
        iv1.setOnClickListener(new ImageView.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				LinearLayout linearLayout = new LinearLayout(THIS);
    			linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    		    linearLayout.setOrientation(LinearLayout.VERTICAL);
    		    linearLayout.setGravity(Gravity.CENTER);
    		    
    			RadioButton lead7,lead12;
    			lead7 = new RadioButton(THIS);
    			lead7.setId(7);
    			lead7.setText("�ߵ���");
    			lead12 = new RadioButton(THIS);
    			lead12.setText("ʮ������");
    			lead12.setId(12);
    		    
    		    
    		    RadioGroup radioGroup = new RadioGroup(THIS);
    		    radioGroup.setOrientation(LinearLayout.VERTICAL);
    		    radioGroup.addView(lead7,new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		    radioGroup.addView(lead12,new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    			linearLayout.addView(radioGroup);
    			
    			if(LeadNumber == 7)
   		    	 	lead7.setChecked(true);
    			else if(LeadNumber == 12)
    				lead12.setChecked(true);
    			else
    				lead12.setChecked(true);
    			
    			radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

    				public void onCheckedChanged(RadioGroup group, int checkedId) {
    					// TODO Auto-generated method stub
    					LeadNumber = checkedId;
    					Log.e("lead",""+checkedId);
    				}
    		     });
    			
    			
		        
		        new AlertDialog.Builder(THIS)
    			.setTitle("�����ĵ��豸����")
    			.setView(linearLayout)
    			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
		        		
					}
    				
    			})
    			.setNegativeButton("ȡ��", null)
    			.show();
				
				
			}
        	
        });
        
        iv2.setOnClickListener(new ImageView.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(manager.isTransporting()){
					Toast.makeText(getApplicationContext(), "����ֹͣ����", Toast.LENGTH_SHORT).show();
				}else{
					Intent intent = new Intent();
					intent.setClass(NewClientActivity.this, com.wh.androidcamera.MainActivity.class);
					startActivity(intent);
				}
			}
        });
        
        
        iv3.setOnClickListener(new ImageView.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(manager.isTransporting()){
					SendMessage(handler,100);
				}else{
					if(manager.isconnected()){
						new AlertDialog.Builder(THIS)
						.setTitle("�Ͽ�����")
						.setMessage("ȷ��Ҫ�Ͽ�����������")
						.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								if(manager.disconnect() == true){
									iv3.setImageResource(R.drawable.bluedis);
									Toast.makeText(THIS, "�Ͽ���������", Toast.LENGTH_SHORT).show();
									status.setText("δ����");
								}else{
									Toast.makeText(THIS, "�Ͽ�����ʧ��", Toast.LENGTH_SHORT).show();
								}
							}
						})
						.setNegativeButton("ȡ��", null)
						.show();
					}else{
						if(manager.isTransporting())
							manager.stopECGTransport();
						new AlertDialog.Builder(THIS)
						.setTitle("��ʼ")
						.setMessage("Ҫ�����������豸��")
						.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								startBluetooth();
							}
						})
						.setNegativeButton("ȡ��", null)
						.show();
					}
				}
			}
        });
        
        iv4.setOnClickListener(new ImageView.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(manager.isconnected()){
//					if(manager.getWorkMode() != 4){
						if(manager.isTransporting()){
							iv4.setImageResource(R.drawable.play);
							manager.stopECGTransport();
							BufferingThread.BufferingTag = false;
							drawTimer.cancel();
							drawTimer2.cancel();
						}else{
							iv4.setImageResource(R.drawable.stop);
							manager.startECGTransport();
							byte[] buffer = new byte[5000];
							
							BufferingThread bufferringthread = new BufferingThread(manager, buffer, LeadNumber);
							bufferringthread.start();
							drawTimer = new Timer();
							drawTask = new DrawTask();
							drawTimer.schedule(drawTask, 0, 70);
							drawTimer2 = new Timer();
							drawTask2 = new DrawTask2();
							drawTimer2.schedule(drawTask2, 0, 650);
						}
//					}else{
//						Log.e("workmode",""+manager.getWorkMode());
//						Toast.makeText(getApplicationContext(), "��ѡ����ʷ�ļ�", Toast.LENGTH_SHORT).show();
//					}
					
				}else{
					status.setText("�ѻ�����");
					manager.setTestMode();
					if(ECGfactory.isBuilding){
						iv4.setImageResource(R.drawable.play);
						manager.stopECGTransport();
						drawTimer.cancel();
						drawTimer2.cancel();
					}else{
						iv4.setImageResource(R.drawable.stop);
						if(manager.startECGTransport()){
							Log.e("��ʼ","����ģʽ");
						}
						byte[] buffer = new byte[5000];
						BufferingThread thread = new BufferingThread(manager, buffer, LeadNumber);
						thread.start();
						drawTimer = new Timer();
						drawTask = new DrawTask();
						drawTimer.schedule(drawTask, 0, 70);
						drawTimer2 = new Timer();
						drawTask2 = new DrawTask2();
						drawTimer2.schedule(drawTask2, 0, 650);
					}
				}
				
			}
        });
        
        iv5.setOnClickListener(new ImageView.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(manager.isTransporting()){
					Toast.makeText(getApplicationContext(), "����ֹͣ����", Toast.LENGTH_SHORT).show();
				}else{
					/*LinkedList<String> filelist = manager.getECGFilesList();
					if(filelist != null){
						int count = filelist.size();
						final String[] str = new String[count];
						Iterator<String> iterator = filelist.iterator();
						int i=0;
						while(iterator.hasNext()){
		    				String filename = iterator.next();
		    				str[i] = filename;
		    				i++;
		    				Log.v("�յ��ļ�",filename);
		    			}
//						final String[] str = new String[]{"abc", "edf", "sffaf", "sdla","djajd","sdkla","sdkjl"};
						new AlertDialog.Builder(NewClientActivity.this)
						.setTitle("ѡ����ʷ�ļ�")
						.setItems(str, new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(), "ѡ����"+str[which], Toast.LENGTH_SHORT).show();
								historyMode = true;
								try {
									if(manager.getWorkMode() != 4)
										manager.setHistoryMode();
									manager.getECGhistory(str[which]);
									status.setText("��ʷģʽ");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						})
						.setNegativeButton("ȡ��", null)
						.show();
					}else{
						Toast.makeText(getApplicationContext(), "û����ʷ�ļ�", Toast.LENGTH_SHORT).show();
					}*/
//					manager.getECGleadNumber();
					manager.getTime();
					manager.setTime();
				}
			}
        });
    }
    
    void disconnect(){
    	final Handler mhandler = new Handler(){
    		ProgressDialog dialog;
    		public void handleMessage(Message msg){
    			switch(msg.what){
    			case 1:
    				dialog = new ProgressDialog(THIS);
    				dialog.setTitle("�Ͽ�����");
    				dialog.setMessage("���ڶϿ�����...");
    				dialog.show();
    				break;
    			case 2:
    				dialog.cancel();
    				BufferingThread.BufferingTag = false;
					drawTimer.cancel();
					drawTimer2.cancel();
					iv4.setImageResource(R.drawable.play);
					if(manager.disconnect() == true){
						iv3.setImageResource(R.drawable.bluedis);
						Toast.makeText(THIS, "�Ͽ���������", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(THIS, "�Ͽ�����ʧ��", Toast.LENGTH_SHORT).show();
					}
    			}
    		}
    	};
    	
    	Thread thread = new Thread(new Runnable(){
			public void run() {
				// TODO Auto-generated method stub
				SendMessage(mhandler, 1);
		    	while(true){
					if(!manager.isTransporting()){
						SendMessage(mhandler, 2);
						break;
					}
				}
			}
		});
		thread.start();
    }
    
    void startBluetooth(){
		final Handler mhandler = new Handler(){
			ProgressDialog dialog;
			public void handleMessage(Message msg){
				switch(msg.what){
				case 1:
					dialog = new ProgressDialog(THIS);
					dialog.setTitle("��������");
					dialog.setMessage("���ڴ�����...");
					dialog.show();
					break;
				case 2:
					dialog.cancel();
					SendMessage(handler, 0);
					break;
				}
			}
		};
		Thread thread = new Thread(new Runnable(){
			public void run() {
				// TODO Auto-generated method stub
				if(!adapter.isEnabled())
					adapter.enable();
				SendMessage(mhandler, 1);
				while(true){
					if(adapter.isEnabled()){
						SendMessage(mhandler, 2);
						break;
					}
				}
			}
		});
		thread.start();
	}
      
    static public Handler handler = new Handler(){
    	ProgressDialog dialog;
    	public void handleMessage(Message msg){
    		switch(msg.what){
    		case 0:
    			dialog = new ProgressDialog(THIS);
    			dialog.setTitle("��������");
    			dialog.setMessage("���������豸...");
    			dialog.show();
    			adapter.startDiscovery();
    			manager.cancleTestMode();
    			break;
    		case 1:
    			if(dialog != null){
    				dialog.cancel();
    			}
    			dialog = new ProgressDialog(THIS);
    			dialog.setTitle("��������");
    			dialog.setMessage("���ڽ�������...");
    			dialog.show();
    			try {
					manager.connect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			break;
    		case 2:
    			if(dialog != null)
    				dialog.cancel();
    			try {
    				DeviceMessage device = manager.getDevice();
					switch(device.mode){
					case 1:
						status.setText("�ѻ�����");
						break;
					case 2:
						status.setText("ʵʱģʽ");
						break;
					case 3:
						status.setText("����ģʽ");
						break;
					case 4:
						status.setText("��ʷģʽ");
						break;
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			Toast.makeText(THIS, "��������", Toast.LENGTH_LONG).show();
    			iv3.setImageResource(R.drawable.bluetooth);
    			
    			manager.connectToServer(ServerIP, 2013);
    			Log.e("SvrConnector"," ���������߳�");
    			break;
    		case 3:
    			if(dialog != null)
    				dialog.cancel();
    			Toast.makeText(THIS, "��������ʧ��", Toast.LENGTH_SHORT).show();
    			break;
    		case 4:
    			Toast.makeText(THIS, "�Ͽ�����", Toast.LENGTH_SHORT).show();
    			break;
    		case 5:
    			int hr = msg.arg1;
    			heartRateValue.setText(""+hr);
    			break;
    		case 6:
    			
    			break;
    		case 7:
    			iv1.setImageResource(R.drawable.server_logo);
    			Toast.makeText(THIS, "Զ�̷�����������", Toast.LENGTH_SHORT).show();
    			break;
    		case 8:
    			iv1.setImageResource(R.drawable.server_logo_h);
    			Toast.makeText(THIS, "Զ�̷���������ʧ��", Toast.LENGTH_SHORT).show();
    			break;
    		case 9:
    			iv1.setImageResource(R.drawable.server_logo_h);
    			Toast.makeText(THIS, "Զ�̷������Ͽ�", Toast.LENGTH_SHORT).show();
    			break;
    		case 10:
    			
    			break;
    		case 95:
    			iv4.setImageResource(R.drawable.play);
				BufferingThread.BufferingTag = false;
    			break;
    		case 96:
    			Toast.makeText(THIS, "�豸������", Toast.LENGTH_SHORT).show();
    			break;
    		case 97:
    			Toast.makeText(THIS, "�豸δ����", Toast.LENGTH_SHORT).show();
    			break;
    		case 98:
    			Toast.makeText(THIS, "�����ʼ��ɹ�", Toast.LENGTH_LONG).show();
    			long [] pattern = {100,400,100,400};
    			vibrator.vibrate(pattern,2);
    			new Handler().postDelayed(new Runnable(){

					public void run() {
						// TODO Auto-generated method stub
						vibrator.cancel();
					}
    				
    			}, 3000);
    			break;
    		case 99:
    			Toast.makeText(THIS, "�����ʼ�ʧ�ܣ���������", Toast.LENGTH_LONG).show();
    			long [] pattern2 = {100,400,100,400};
    			vibrator.vibrate(pattern2,2);
    			new Handler().postDelayed(new Runnable(){

					public void run() {
						// TODO Auto-generated method stub
						vibrator.cancel();
					}
    				
    			}, 3000);
    			break;
    		case 100:
    			Toast.makeText(THIS, "����ֹͣ����", Toast.LENGTH_SHORT).show();
    			break;
    		}
    	}
    };
    
    public static Handler alarmHandler = new Handler(){
    	public void handleMessage(Message msg){
    		switch(msg.what){
    		case 0:
    			break;
    		case 1:
    			heartRateLowPic.setImageResource(R.drawable.redcycle3);
    			break;
    		case 2:
    			heartRateHighPic.setImageResource(R.drawable.redcycle3);
    			break;
    		case 3:
    			TAlarmPic.setImageResource(R.drawable.redcycle3);
    			break;
    		case 4:
    			STAlarmPic.setImageResource(R.drawable.redcycle3);
    			break;
    		case 5:
    			leaddroppic.setImageResource(R.drawable.redcycle3);
    			break;
    		case 6:
    			inputoverpic.setImageResource(R.drawable.redcycle3);
    			break;
    		case 9:
    			manual_alarm.setImageResource(R.drawable.redcycle3);
    			break;
    		case 10:
    			heartRateLowPic.setImageResource(R.drawable.greencycle);
    			heartRateHighPic.setImageResource(R.drawable.greencycle);
    			TAlarmPic.setImageResource(R.drawable.greencycle);
    			STAlarmPic.setImageResource(R.drawable.greencycle);
    			leaddroppic.setImageResource(R.drawable.greencycle);
    			inputoverpic.setImageResource(R.drawable.greencycle);
    			manual_alarm.setImageResource(R.drawable.greencycle);
    			break;
    		case 11:
    			ecgAlarmPic.setImageResource(R.drawable.redcycle3);
    			break;
    		case 12:
    			moveAlarmPic.setImageResource(R.drawable.redcycle3);
    			break;
    		case 13:
    			positionAlarmPic.setImageResource(R.drawable.redcycle3);
    			break;
    		case 14:
    			deviceAlarmPic.setImageResource(R.drawable.redcycle3);
    			break;
    		case 15:
    			ecgAlarmPic.setImageResource(R.drawable.greencycle);
    			moveAlarmPic.setImageResource(R.drawable.greencycle);
    			positionAlarmPic.setImageResource(R.drawable.greencycle);
    			deviceAlarmPic.setImageResource(R.drawable.greencycle);
    			break;
    		}
    	}
    };
    
    private Handler editionChangeHandler = new Handler(){
    	public void handleMessage(Message msg){
    		switch(msg.what){
    		case 1:
    			ecgAlarmText.setVisibility(View.INVISIBLE);
    			moveAlarmText.setVisibility(View.INVISIBLE);
    			positionAlarmText.setVisibility(View.INVISIBLE);
    			deviceAlarmText.setVisibility(View.INVISIBLE);
    			ecgAlarmPic.setVisibility(View.INVISIBLE);
    			moveAlarmPic.setVisibility(View.INVISIBLE);
    			positionAlarmPic.setVisibility(View.INVISIBLE);
    			deviceAlarmPic.setVisibility(View.INVISIBLE);
    			
    			inputoverpic.setVisibility(View.VISIBLE);
    			leaddroppic.setVisibility(View.VISIBLE);
    			heartRateHighPic.setVisibility(View.VISIBLE);
    			heartRateLowPic.setVisibility(View.VISIBLE);
    			STAlarmPic.setVisibility(View.VISIBLE);
    			TAlarmPic.setVisibility(View.VISIBLE);
    			manual_alarm.setVisibility(View.VISIBLE);
    			fallAlarmPic.setVisibility(View.VISIBLE);
    			inputover.setVisibility(View.VISIBLE);
    			heartratehigh.setVisibility(View.VISIBLE);
    			stalarm.setVisibility(View.VISIBLE);
    			talarm.setVisibility(View.VISIBLE);
    			heartratelow.setVisibility(View.VISIBLE);
    			leaddrop.setVisibility(View.VISIBLE);
    			manualalarm.setVisibility(View.VISIBLE);
    			fallalarm.setVisibility(View.VISIBLE);
    			break;
    		case 2:
    			inputoverpic.setVisibility(View.INVISIBLE);
    			leaddroppic.setVisibility(View.INVISIBLE);
    			heartRateHighPic.setVisibility(View.INVISIBLE);
    			heartRateLowPic.setVisibility(View.INVISIBLE);
    			STAlarmPic.setVisibility(View.INVISIBLE);
    			TAlarmPic.setVisibility(View.INVISIBLE);
    			manual_alarm.setVisibility(View.INVISIBLE);
    			fallAlarmPic.setVisibility(View.INVISIBLE);
    			inputover.setVisibility(View.INVISIBLE);
    			heartratehigh.setVisibility(View.INVISIBLE);
    			stalarm.setVisibility(View.INVISIBLE);
    			talarm.setVisibility(View.INVISIBLE);
    			heartratelow.setVisibility(View.INVISIBLE);
    			leaddrop.setVisibility(View.INVISIBLE);
    			manualalarm.setVisibility(View.INVISIBLE);
    			fallalarm.setVisibility(View.INVISIBLE);
    			
    			ecgAlarmText.setVisibility(View.VISIBLE);
    			moveAlarmText.setVisibility(View.VISIBLE);
    			positionAlarmText.setVisibility(View.VISIBLE);
    			deviceAlarmText.setVisibility(View.VISIBLE);
    			ecgAlarmPic.setVisibility(View.VISIBLE);
    			moveAlarmPic.setVisibility(View.VISIBLE);
    			positionAlarmPic.setVisibility(View.VISIBLE);
    			deviceAlarmPic.setVisibility(View.VISIBLE);
    			
    			break;
    		}
    	}
    };
    
    public static Handler statusHandler = new Handler(){
    	public void handleMessage(Message msg){
    		switch(msg.what){
    		case 1:
    			if(manager.setTestMode())
    				status.setText("�ѻ�����");
    			break;
    		case 2:
    			try {
					if(manager.setNormalMode())
						status.setText("ʵʱ����");
					else
						status.setText("����ʧ��");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					status.setText("����ʧ��");
				}
    			break;
    		case 3:
    			try {
					if(manager.setAlarmMode())
						status.setText("��������");
					else
						status.setText("����ʧ��");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					status.setText("����ʧ��");
				}
    			break;
    		case 4:
    			try {
					if(manager.setHistoryMode())
						status.setText("������ʷ");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					status.setText("����ʧ��");
				}
    			break;
    		}
    	}
    };
    
    
    class DrawTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(manager.isTransporting())
				draw(GET);
			
		}
    	
    }
    class DrawTask2 extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(manager.isTransporting())
				draw2();
		}
    	
    }

    void draw(int lenght){
    	byte[] todraw = BufferingThread.buffer.removeBytes(lenght);
//    	byte[] todraw2 = BufferingThread.buffer2.removeBytes(lenght);
//    	byte[] todraw3 = BufferingThread.buffer3.removeBytes(lenght);
//    	byte[] todraw4= BufferingThread.buffer4.removeBytes(lenght);
//    	byte[] todraw5 = BufferingThread.buffer5.removeBytes(lenght);
//    	byte[] todraw6 = BufferingThread.buffer6.removeBytes(lenght);
//    	byte[] todraw7 = BufferingThread.buffer7.removeBytes(lenght);
    	int[] leads = new int[lenght];
//    	int[] leads2 = new int[lenght];
//    	int[] leads3 = new int[lenght];
//    	int[] leads4 = new int[lenght];
//    	int[] leads5 = new int[lenght];
//    	int[] leads6 = new int[lenght];
//    	int[] leads7 = new int[lenght];
    	if(todraw != null){
    		for(int i=0; i<todraw.length; i++){
        		leads[i] = todraw[i];
        	}
//    		for(int i=0; i<todraw2.length; i++){
//        		leads2[i] = todraw2[i];
//        	}
//    		for(int i=0; i<todraw3.length; i++){
//        		leads3[i] = todraw3[i];
//        	}
//    		for(int i=0; i<todraw4.length; i++){
//        		leads4[i] = todraw4[i];
//        	}
//    		for(int i=0; i<todraw5.length; i++){
//        		leads5[i] = todraw5[i];
//        	}
//    		for(int i=0; i<todraw6.length; i++){
//        		leads6[i] = todraw6[i];
//        	}
//    		for(int i=0; i<todraw7.length; i++){
//        		leads7[i] = todraw7[i];
//        	}
//    		
//    		int[][] wholeleads= {leads,leads2,leads3,leads4,leads5,leads6,leads7};
    		
        	if(oldX == 0)
    			originalDraw();
    		else{
    			currentDraw(leads);
//    			currentDraw(values2d[1]);
//    			currentDraw(wholeleads[0]);
    		}
    	}
    }
    
    void draw2(){
    	int heartRate = BufferingThread.heartRateBuffer.removeInt();
    	Message msg = handler.obtainMessage();
    	msg.what = 5;
    	if(heartRate >= 0)
    		msg.arg1 = heartRate;
    	else
    		msg.arg1 = 0-heartRate;
    	handler.sendMessage(msg);
    	int[] heartRateInt = new int[20];
    	for(int i=0; i<20; i++){
    		heartRateInt[i] = heartRate;
    	}
    	if(oldX_pulse == 0){
    		originalDraw2();
    		
    	}else{
    		currentDraw2(heartRateInt);
    	}
    }
    
    void currentDraw(int array[]){
    	if(array != null){
    		int x_left, x_right;
        	x_left = oldX;
        	x_right = oldX+20;
        	ecg_holder = ecg_view.getHolder();
        	if(oldX+80 <= RIGHT){
    			Canvas canvas2 = ecg_holder.lockCanvas(new Rect(x_left,0,x_left+80,HEIGHT/2));
//    			Log.e("���","left"+x_left+"--"+"right"+(x_left+40));
    			canvas2.drawColor(Color.BLACK);// �������
    			canvas2.drawColor(Color.TRANSPARENT,Mode.CLEAR);
    			ecg_holder.unlockCanvasAndPost(canvas2);
    		}
        	Canvas canvas = ecg_holder.lockCanvas(new Rect(x_left,0,x_right,HEIGHT/2));
        	canvas.drawColor(Color.BLACK);// �������
			canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
        	Paint mPaint = new Paint();
    		mPaint.setColor(Color.GREEN);// ����Ϊ��ɫ
    		mPaint.setStrokeWidth(2);// ���û��ʴ�ϸ
    		mPaint.setStyle(Paint.Style.STROKE);
    		canvas.drawPath(ecgPath(array), mPaint);
//    		Log.v("��","left"+x_left+"right"+x_right);
    		ecg_holder.unlockCanvasAndPost(canvas);
    	}
	}
    void currentDraw(int array[][]){
    	if(array != null){
    		int x_left, x_right;
        	x_left = oldX;
        	x_right = oldX+20;
        	ecg_holder = ecg_view.getHolder();
        	if(oldX+80 <= RIGHT){
    			Canvas canvas2 = ecg_holder.lockCanvas(new Rect(x_left,0,x_left+80,HEIGHT));
//    			Log.e("���","left"+x_left+"--"+"right"+(x_left+40));
    			canvas2.drawColor(Color.BLACK);// �������
    			canvas2.drawColor(Color.TRANSPARENT,Mode.CLEAR);
    			ecg_holder.unlockCanvasAndPost(canvas2);
    		}
        	Canvas canvas = ecg_holder.lockCanvas(new Rect(x_left,0,x_right,HEIGHT));
        	canvas.drawColor(Color.BLACK);// �������
			canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
        	Paint mPaint = new Paint();
    		mPaint.setColor(Color.GREEN);// ����Ϊ��ɫ
    		mPaint.setStrokeWidth(2);// ���û��ʴ�ϸ
    		mPaint.setStyle(Paint.Style.STROKE);
    		canvas.drawPath(ecgPath(array), mPaint);
//    		Log.v("��","left"+x_left+"right"+x_right);
    		ecg_holder.unlockCanvasAndPost(canvas);
    	}
	}
    
    
    void currentDraw2(int array[]){
    	int x_left_pulse, x_right_pulse;
    	x_left_pulse = oldX_pulse;
    	x_right_pulse = oldX_pulse+20;
    	xl_holder = xl_view.getHolder();
    	if(x_left_pulse+80 <= RIGHT){
    		Canvas canvas2 = xl_holder.lockCanvas(new Rect(x_left_pulse,0,x_left_pulse+80,HEIGHT));
    		canvas2.drawColor(Color.BLACK);// �������
			canvas2.drawColor(Color.TRANSPARENT,Mode.CLEAR);
			xl_holder.unlockCanvasAndPost(canvas2);
    	}
    	Canvas canvas = xl_holder.lockCanvas(new Rect(x_left_pulse,0,x_right_pulse,HEIGHT));
    	canvas.drawColor(Color.BLACK);// �������
		canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
    	Paint mPaint = new Paint();
		mPaint.setColor(Color.RED);// ����Ϊ��ɫ
		mPaint.setStrokeWidth(2);// ���û��ʴ�ϸ
		mPaint.setStyle(Paint.Style.STROKE);
		Path path = pulsePath(array);
		canvas.drawPath(path, mPaint);
		Bitmap bitmap = shot();
		toSaveBitmap  = bitmap;
		Canvas mCanvas = new Canvas(bitmap);
		mCanvas.drawPath(path, mPaint);
		xl_holder.unlockCanvasAndPost(canvas);
		xl_view.buildDrawingCache();
    }
    

    void originalDraw(){
/*
    	ecg_holder = ecg_view.getHolder();
    	Canvas canvas = ecg_holder.lockCanvas();
    	Paint mPaint = new Paint();
		mPaint.setColor(Color.GREEN);// ����Ϊ��ɫ
		mPaint.setStrokeWidth(2);// ���û��ʴ�ϸ
		canvas.drawLine(WIDTH/10, 0, WIDTH/10, HEIGHT/4, mPaint);
		ecg_holder.unlockCanvasAndPost(canvas);
*/
		oldX = WIDTH/10;
        oldY = HEIGHT/4;
    }
    
    void originalDraw2(){
		oldX_pulse = WIDTH/10;
        oldY_pulse = 190;
    }
    
    Path ecgPath(int array[]){
    	if(array != null){
    		Path path = new Path();
        	path.moveTo(oldX, oldY);
        	for(int i=0; i<array.length; i=i+GET/20){
        		path.lineTo(oldX+1, HEIGHT/4-2*array[i]);
        		oldX = oldX+1;
        		oldY=HEIGHT/4-2*array[i];
        		if(oldX == RIGHT){
        			oldX = WIDTH/10;
        			Log.e("oldX","����"+oldX);
        		}
        	}
        	return path;
    	}else{
    		return null;
    	}
    }
    Path ecgPath(int array[][]){
    	if(array != null){
    		Path path = new Path();
        	for(int j=0; j<7; j++){
        		path.moveTo(oldX, oldY);
        		for(int i=0; i<array.length; i=i+GET/20){
            		path.lineTo(oldX+1, 50+j*100-2*array[j][i]);
            		oldX = oldX+1;
            		oldY=50+j*100-2*array[j][i];
            	}
        		oldX = oldX-20; oldY = 50+(j+1)*100;
        	}
        	if(oldX == RIGHT){
    			oldX = WIDTH/10;
    			Log.e("oldX","����"+oldX);
    		}
        	return path;
    	}else{
    		return null;
    	}
    }
    Path pulsePath(int array[]){
    	int Y_pulse;
    	int X_pulse;
    	Path path = new Path();
    	path.moveTo(oldX_pulse, oldY_pulse);
    	for(int i=0; i<array.length; i++){
    		if(array[i] >= 50 && array[i] <= 80){
    			Y_pulse = (90-(array[i]-60)*4);
        		X_pulse = oldX_pulse+1;
    		}else if(array[i] < 50 && array[i] >=20){
    			Y_pulse = (90-(array[i]-60));
        		X_pulse = oldX_pulse+1;
    		}else if(array[i] < 20){
    			Y_pulse = 190;
        		X_pulse = oldX_pulse+1;
    		}else{
    			Y_pulse = 10;
        		X_pulse = oldX_pulse+1;
    		}
    		path.lineTo(X_pulse, Y_pulse);
    		oldX_pulse = X_pulse;
    		oldY_pulse= Y_pulse;
    		if(oldX_pulse == RIGHT)
    			oldX_pulse = WIDTH/10;
//    		if(array[i] == 0){
//    			path.lineTo(oldX_pulse+1, 130);
//        		oldX_pulse = oldX_pulse+1;
//        		oldY_pulse = 130;
//    		}else{
//    			path.lineTo(oldX_pulse+1, (90-(array[i]-60)*4));
//        		oldX_pulse = oldX_pulse+1;
//        		oldY_pulse=(90-(array[i]-60)*4);
//    		}
//    		if(oldX_pulse == RIGHT)
//    			oldX_pulse = WIDTH/10;
    	}
    	return path;
    }
    
    public String getLocalIPAddress() 
	{  
	    try {  
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {  
	            NetworkInterface intf = en.nextElement();  
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {  
	                InetAddress inetAddress = enumIpAddr.nextElement();  
	                if (!inetAddress.isLoopbackAddress()&&inetAddress.getAddress().length!=16)	//Ҫ����IPV4�ĵ�ַ 
	                {	                    
	                	return inetAddress.getHostAddress().toString();  
	                }
	            }  
	        }  
	    } catch (SocketException ex) {  
	        System.out.println(ex.toString());  
	    }
	    return null;  
	}
    
    
    static void SendMessage(Handler handler, int i){
		Message msg = handler.obtainMessage();
		msg.what = i;
		handler.sendMessage(msg);
	}
    
    @Override
	public void onStop(){
		stopService(new Intent(this,BMService.class));
		Log.e("onStop", "ֹͣ����");
		super.onStop();
		mWakeLock.release();
	}
    
    @Override
	public void onDestroy(){
		stopService(new Intent(this,BMService.class));
		Log.e("onDestroy", "ֹͣ����");
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
		mWakeLock.release();
	}
    /////////////////////////////////////////////////////
 // Wheel scrolled flag
    private boolean wheelScrolled = false;
    
    // Wheel scrolled listener
    OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
        public void onScrollingStarted(WheelView wheel) {
            wheelScrolled = true;
        }
        public void onScrollingFinished(WheelView wheel) {
            wheelScrolled = false;
            updateStatus();
        }
    };
    
    // Wheel changed listener
    private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            if (!wheelScrolled) {
                updateStatus();
            }
        }
    };
    
    /**
     * Updates entered PIN status
     */
    private void updateStatus() {
//        TextView text = (TextView) findViewById(R.id.pwd_status);
//            text.setText(getAllCode());
    	if(LeadNumber == 7){
    		switch(1+getWheel(R.id.passw_1).getCurrentItem()){
    		case 8:
    			BufferingThread.LEAD =1;
    			break;
    		case 9:
    			BufferingThread.LEAD =2;
    			break;
    		case 10:
    			BufferingThread.LEAD =3;
    			break;
    		case 11:
    			BufferingThread.LEAD =4;
    			break;
    		case 12:
    			BufferingThread.LEAD =5;
    			break;
    		}
    	}else{
    		BufferingThread.LEAD = 1+getWheel(R.id.passw_1).getCurrentItem();
    	}
    }

    /**
     * Initializes wheel
     * @param id the wheel widget Id
     */
    private void initWheel(int id) {
        WheelView wheel = getWheel(id);
//        wheel.setAdapter(new NumericWheelAdapter(1, 7));
        wheel.setAdapter(new NumericWheelAdapter(1, 12));
        wheel.setCurrentItem(0);
        
        wheel.addChangingListener(changedListener);
        wheel.addScrollingListener(scrolledListener);
        wheel.setCyclic(true);
        wheel.setInterpolator(new AnticipateOvershootInterpolator());
    }
    
    private void initWheel2(int id){
    	WheelView wheel = getWheel(id);
    	wheel.setAdapter(new TextWheelAdapter(0, 3));
    	wheel.setCurrentItem(0);
    	wheel.setCyclic(true);
    	wheel.setInterpolator(new AnticipateOvershootInterpolator());
    }
    
    /**
     * Returns wheel by Id
     * @param id the wheel Id
     * @return the wheel with passed Id
     */
    private WheelView getWheel(int id) {
    	return (WheelView) findViewById(id);
    }
    
    
    /**
     * Tests wheel value
     * @param id the wheel Id
     * @param value the value to test
     * @return true if wheel value is equal to passed value
     */
    private boolean testWheelValue(int id, int value) {
    	
    	return getWheel(id).getCurrentItem() == value;
    }
    
    /**
     * Mixes wheel
     * @param id the wheel id
     */
    private void mixWheel(int id) {
        WheelView wheel = getWheel(id);
        wheel.scroll(-25 + (int)(Math.random() * 50), 2000);
    }
    private String getAllCode(){
    	StringBuilder sb = new StringBuilder();
    	return sb.append(getWheel(R.id.passw_1).getCurrentItem()+"")
//    	                          .append(getWheel(R.id.passw_2).getCurrentItem()+"")
//    	                          .append(getWheel(R.id.passw_3).getCurrentItem()+"")
//    	                          .append(getWheel(R.id.passw_4).getCurrentItem()+"")
//    	                          .append(getWheel(R.id.passw_5).getCurrentItem()+"")
//    	                          .append(getWheel(R.id.passw_6).getCurrentItem()+"")
    							   .toString();
    }
    
    ///////////////////////////////////////////////
    /**  
     * ��������  
     * @return  
     */
    private Bitmap shot() {   
        View view = getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Display display = this.getWindowManager().getDefaultDisplay();   
        view.layout(0, 0, display.getWidth(), display.getHeight());   
        view.setDrawingCacheEnabled(true);//����ǰ���ڱ��滺����Ϣ������getDrawingCache()�����Ż᷵��һ��Bitmap   
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache());
//        Rect frame = new Rect();
//        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//        int statusBarHeight = frame.top;
//        Bitmap b = Bitmap.createBitmap(bmp, 0, statusBarHeight, WIDTH, HEIGHT);
//        view.destroyDrawingCache();
        return bmp;   
    }
    
    // ���浽sdcard
    private void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /** 
    * ��SD���ϴ���Ŀ¼ 
    *  
	* @param dirName 
	*/  
	public void creatSDDir(String dirName) {  
		File dir = new File("/mnt/sdcard/" + dirName);  
		if(!dir.exists()){
			dir.mkdir();
			Log.e("sdcard","����Ŀ¼"+dirName);
		}
	}
}