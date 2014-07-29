package com.other.view.copy;

import com.guo.ecg.client.NewClientActivity;
import com.guo.ecg.client.SendMail;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TextDialog extends Dialog {

	 private LinearLayout linearLayout;
	 private TextView tv1;
	 private EditText address;
	 private TextView tv2;
	 private EditText content;
	 private Button btnOk;
	 private Button btnCancel;
	 private LinearLayout bottomChildLinearLayout;
	 
	 /**
	  * 文字对话框标题
	  */
	 private String title = "截图发送";
	 
	    public interface OnTextInputListener {
	        void textInput(String text, int textSize);
	    }

	    public TextDialog(Context context, final String filename) 
	    {
	        super(context);
	        
	        linearLayout = new LinearLayout(context);
	        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	        linearLayout.setOrientation(LinearLayout.VERTICAL);
	        linearLayout.setGravity(Gravity.CENTER);
	        
	        tv1 = new TextView(getContext());
	        tv1.setText("收件人(目前只限1个)：");
	        address = new EditText(getContext());
	        address.setMinLines(1);
	        tv2 = new TextView(getContext());
	        tv2.setText("文字说明：");
	        content = new EditText(getContext());
	        content.setMinLines(3);
	        linearLayout.addView(tv1,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	        linearLayout.addView(address,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	        linearLayout.addView(tv2,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	        linearLayout.addView(content,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	        
	        
	        
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
	        		//验证text是否为空
	        		final String reciever = address.getText().toString()/*.replace("/n", "")*/;
	        		final String text = content.getText().toString();
	        		if(reciever == null || text.trim().equals(""))
	        		{
	        			Toast.makeText(getContext(), "收件人不能为空", Toast.LENGTH_SHORT).show();
	        			return;
	        		}else{
	        			Thread thread = new Thread(new Runnable(){

							public void run() {
								// TODO Auto-generated method stub
								SendMail mail = new SendMail(reciever,text,(filename+".jpg"));
								mail.run();
							}
	        			});
	        			thread.start();
						Toast.makeText(getContext(), "已发送", Toast.LENGTH_SHORT).show();
	        		}
	    
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
	    		dismiss();
	    	}

		}

