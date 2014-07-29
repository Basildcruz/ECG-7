package com.guo.ecg.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class NewClientActivity2 extends Activity {
	public static int WIDTH;
	public static int HEIGHT;
	public static int RIGHT;
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
	        //х╚фа    
	        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,      
	        WindowManager.LayoutParams. FLAG_FULLSCREEN);
	        setContentView(R.layout.newlayout);
	        WIDTH = getWindowManager().getDefaultDisplay().getWidth();
	        HEIGHT = getWindowManager().getDefaultDisplay().getHeight();
	 }

}
