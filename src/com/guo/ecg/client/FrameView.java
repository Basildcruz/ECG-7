package com.guo.ecg.client;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class FrameView extends View {
	
	private int Height, Width;
	private Context mContext = null;
	private Drawable ecg_logo = null;
	private Drawable ecg_word = null;
	private Drawable pw_logo = null;
	private Drawable pw_word = null;
	private Drawable bo_logo = null;
	private Drawable bo_word = null;
	private Drawable bp_logo = null;
	private Drawable bp_word = null;
	private Drawable xl_word = null;
	private Drawable mb_word = null;

	public FrameView(Context context) {
		super(context);
		mContext = context;
		// TODO Auto-generated constructor stub
	}
	public FrameView(Context context, AttributeSet attrs){
		super(context, attrs);
		mContext = context;
	}
	public FrameView(Context context, AttributeSet attrs, int inflateParams){
		super(context, attrs, inflateParams);
		mContext = context;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		Height = NewClientActivity.HEIGHT;
		Width = NewClientActivity.WIDTH;
		ecg_logo = mContext.getResources().getDrawable(R.drawable.ecg_logo);
		ecg_word = mContext.getResources().getDrawable(R.drawable.ecg_word);
		pw_logo = mContext.getResources().getDrawable(R.drawable.pw_logo);
		pw_word = mContext.getResources().getDrawable(R.drawable.pw_word);
		bo_logo = mContext.getResources().getDrawable(R.drawable.bo_logo);
		bo_word = mContext.getResources().getDrawable(R.drawable.bo_word);
		bp_logo = mContext.getResources().getDrawable(R.drawable.bp_logo);
		bp_word = mContext.getResources().getDrawable(R.drawable.bp_word);
		xl_word = mContext.getResources().getDrawable(R.drawable.xl_word);
		mb_word = mContext.getResources().getDrawable(R.drawable.mb_word);
		int H = Height/2;
		int W = Width/10;
		ecg_logo.setBounds(W/7, H/18, W*6/7, H*3/10);
		ecg_word.setBounds(W*3/10, H*7/20, W*7/10, H*17/18);
		pw_logo.setBounds(W/5, H/18+H, W*4/5, H*3/10+H);
		pw_word.setBounds(W*3/10, H*7/20+H, W*7/10, H*17/18+H);
		H = Height/4;
		W = Width/4;
		int offset = Width*13/20;
		bo_logo.setBounds(W/10 + offset, H/8, W/3+offset, H*3/5);
		bo_word.setBounds(W/10 + offset, H*7/10, W/3+offset, H*9/10);
		bp_logo.setBounds(W/10 + offset, H/8+H, W/3+offset, H*3/5+H);
		bp_word.setBounds(W/10 + offset, H*7/10+H, W/3+offset, H*9/10+H);
		ecg_logo.draw(canvas);
		ecg_word.draw(canvas);
		pw_logo.draw(canvas);
		pw_word.draw(canvas);
		bo_logo.draw(canvas);
		bo_word.draw(canvas);
		bp_logo.draw(canvas);
		bp_word.draw(canvas);
		ecg_logo.setBounds(W/10 + offset, H/6+2*H, W/3+offset, H*3/5+2*H);
		xl_word.setBounds(W/10 + offset, H*7/10+H*2, W/3+offset, H*9/10+H*2);
		pw_logo.setBounds(W/8 + offset, H/6+3*H, W*15/48+offset, H*3/5+3*H);
		mb_word.setBounds(W/10 + offset, H*7/10+H*3, W/3+offset, H*9/10+H*3);
		ecg_logo.draw(canvas);
		xl_word.draw(canvas);
		pw_logo.draw(canvas);
		mb_word.draw(canvas);
		super.onDraw(canvas);

		
		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		for(int i = 0; i < Width*11/20; i=i+2){
			for(int j = 0; j < Height/2; j=j+10){
				canvas.drawPoint(Width/10 + i, j, paint);
			}
		}
		for(int i = 0; i < Width*11/20; i=i+10){
			for(int j = 0; j < Height/2; j=j+2){
				canvas.drawPoint(Width/10 + i, j, paint);
			}
		}

		paint.setColor(Color.argb(255, 0, 150, 255));
//		paint.setColor(0xFFBCD4E6);
		for(int i = 0; i < Width*11/20; i=i+2){
			for(int j = 1; j < Height/2; j=j+10){
				canvas.drawPoint(Width/10 + i, Height/2+j, paint);
			}
		}
		for(int i = 0; i < Width*11/20; i=i+10){
			for(int j = 1; j < Height/2; j=j+2){
				canvas.drawPoint(Width/10 + i, Height/2+j, paint);
			}
		}
//		画实线方格
//		for(int i = 10; i < H/2; i = i+10){
//			canvas.drawLine(H/10, H/2 + i, H*13/20, H/2 +i, paint);
//			for(int j = 10; j < H*11/20; j = j+10){	
//				canvas.drawLine(H/10 + j, H/2, H/10 + j, H, paint);
//			}
//		}
		
		paint.setColor(Color.WHITE);
		canvas.drawLine(0,Height/2,Width*9/10,Height/2, paint);
		canvas.drawLine(Width/10, 0, Width/10, Height, paint);
		canvas.drawLine(Width*13/20, 0, Width*13/20, Height, paint);
		canvas.drawLine(Width*9/10, 0, Width*9/10, Height, paint);
		canvas.drawLine(Width*13/20, Height/4, Width*9/10, Height/4, paint);
		canvas.drawLine(Width*13/20, Height*3/4, Width*9/10, Height*3/4, paint);
	}
	

}
