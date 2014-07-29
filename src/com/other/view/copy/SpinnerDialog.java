package com.other.view.copy;

import com.guo.ecg.client.NewClientActivity;
import com.guo.ecg.client.SendMail;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SpinnerDialog extends Dialog {
	private LinearLayout linearLayout;
	private TextView tv1,tv2,tv3;
	private Spinner spinner;
	private EditText value;
	private Button btnOk;
	private Button btnCancel;
	private LinearLayout bottomChildLinearLayout;
	private String title = "设置报警阈值";
	private ArrayAdapter<String> arrayAdapter;
	private String[] alarms = {"心率高","心率低","ST波异常","T波异常"};
	
	public SpinnerDialog(Context context){
		super(context);
		
		linearLayout = new LinearLayout(context);
	    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	    linearLayout.setOrientation(LinearLayout.VERTICAL);
	    linearLayout.setGravity(Gravity.CENTER);
	    
	    arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, alarms);
	    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    tv1 = new TextView(getContext());
	    tv1.setText("选择报警");
	    spinner = new Spinner(getContext());
	    spinner.setAdapter(arrayAdapter);
	    tv2 = new TextView(getContext());
	    tv2.setText("输入阈值");
	    tv3 = new TextView(getContext());
	    tv3.setText("");
	    
	    linearLayout.addView(tv1);
	    linearLayout.addView(spinner);
	    linearLayout.addView(tv2);
	    
	    value = new EditText(getContext());
	    linearLayout.addView(value,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

	    
	    btnOk = new Button(getContext());
        btnOk.setText("确定");
        btnOk.setWidth(NewClientActivity.WIDTH/5);
        
        btnCancel = new Button(getContext());
        btnCancel.setText("取消");
        btnCancel.setWidth(NewClientActivity.WIDTH/5);
        
        
       
        
        bottomChildLinearLayout = new LinearLayout(getContext());
        bottomChildLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        bottomChildLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomChildLinearLayout.setGravity(Gravity.CENTER);
        
        bottomChildLinearLayout.addView(btnOk);
        bottomChildLinearLayout.addView(btnCancel);
        
        linearLayout.addView(bottomChildLinearLayout);
        
        btnOk.setOnClickListener(new View.OnClickListener()
        {

        	public void onClick(View v)
        	{
        		// TODO Auto-generated method stub
        		dismissDialog();
        	}
        });
        
        btnCancel.setOnClickListener(new View.OnClickListener()
        {
        	public void onClick(View v)
        	{
        		// TODO Auto-generated method stub
        		dismissDialog();
        	}
        });
	    
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
