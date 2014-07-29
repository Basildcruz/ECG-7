package com.other.view.copy;

import com.guo.ecg.client.NewClientActivity;
import com.pku.wcsp.ecg.BluetoothManager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class StatusDialog extends Dialog {
	private LinearLayout linearLayout;
	private RadioGroup radioGroup;
	private RadioButton normal,alarm,history,test;
	private String title = "选择工作模式";
	
	public StatusDialog (Context context, BluetoothManager manager){
		 super(context);
		 
		 linearLayout = new LinearLayout(context);
	     linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	     linearLayout.setOrientation(LinearLayout.VERTICAL);
	     linearLayout.setGravity(Gravity.CENTER);
	        
	     normal = new RadioButton(getContext());
	     normal.setId(2);
	     normal.setText("实时传输");
	     alarm = new RadioButton(getContext());
	     alarm.setText("报警传输");
	     alarm.setId(3);
	     history = new RadioButton(getContext());
	     history.setText("历史传输");
	     history.setId(4);
	     test = new RadioButton(getContext());
	     test.setText("测试模式");
	     test.setId(1);
	        
	     radioGroup = new RadioGroup(getContext());
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
				Message msg = NewClientActivity.statusHandler.obtainMessage();
				msg.what = checkedId;
				NewClientActivity.statusHandler.sendMessage(msg);
				dismissDialog();
			}
	    	 
	     });
	        
//	     btnOk = new Button(getContext());
//	     btnOk.setText("确定");
//	     btnOk.setWidth(NewClientActivity.WIDTH/5);
//	        
//	     btnCancel = new Button(getContext());
//	     btnCancel.setText("取消");
//	     btnCancel.setWidth(NewClientActivity.WIDTH/5);
//	        
//	     bottomChildLinearLayout = new LinearLayout(getContext());
//	     bottomChildLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
//	     bottomChildLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
//	     bottomChildLinearLayout.setGravity(Gravity.CENTER);
//	        
//	     bottomChildLinearLayout.addView(btnOk);
//	     bottomChildLinearLayout.addView(btnCancel);
//	        
//	     linearLayout.addView(bottomChildLinearLayout);
//	     
//	     btnOk.setOnClickListener(new View.OnClickListener()
//	        {
//
//	        	public void onClick(View v)
//	        	{
//	        		// TODO Auto-generated method stub
//	        		
//	        		dismissDialog();
//	        	}
//	        });
//	        
//	        btnCancel.setOnClickListener(new View.OnClickListener()
//	        {
//	        	public void onClick(View v)
//	        	{
//	        		// TODO Auto-generated method stub
//	        		dismissDialog();
//	        	}
//	        });
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(linearLayout);
		setTitle(title);
	}

	public void dismissDialog()
	{
		this.dismiss();
	}
}
