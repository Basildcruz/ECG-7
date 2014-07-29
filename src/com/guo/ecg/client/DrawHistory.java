package com.guo.ecg.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

public class DrawHistory extends Thread {
	
	private String name;
	private int lenght;
	private int Xoffset = 200;
	private int Yoffset = 50;
	private int WIDTH = 2600;
	private int HEIGHT = 1200;
	
	public DrawHistory(String name, int lenght){
		
		this.name = name;
		this.lenght = lenght;
		
	}
	
	public void run(){
		Bitmap background = drawBackgound(name, lenght);
		drawLeadNum(background);
		drawSign(background);
		drawEcg(background);
		
		savePic(resizeBmp(background),"/mnt/sdcard/ECG-Shot/"+name+".jpg");
	}
	
	private Bitmap drawBackgound(String name, int lenght){
    	Bitmap bitmap = Bitmap.createBitmap(WIDTH+Xoffset, HEIGHT, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.WHITE);
		
		Paint paint = new Paint();
		paint.setColor(Color.argb(255, 255, 0, 0));
		
		for(int i = 0; i < WIDTH+Xoffset; i=i+2){
			for(int j = 0; j < HEIGHT; j=j+10){
				canvas.drawPoint(i, j, paint);
			}
		}
		for(int i = 0; i < WIDTH+Xoffset; i=i+10){
			for(int j = 0; j < HEIGHT; j=j+2){
				canvas.drawPoint(i, j, paint);
			}
		}
		for(int i=0; i<HEIGHT; i= i+50){
			
			canvas.drawLine(0, i, WIDTH+Xoffset, i, paint);
		}
		
		for(int j=0; j<WIDTH+Xoffset; j = j+50){
			canvas.drawLine(j, 0, j, HEIGHT, paint);
		}
		paint.setColor(Color.BLACK);
		paint.setTextSize(20);
		canvas.drawText("时间："+name+".    时长"+lenght+"秒.   病人：张三", 20, 30, paint);
		
		Log.e("pai","save");
		return bitmap;
		
    }
	
	private void drawLeadNum(Bitmap bitmap){
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);// 画笔为绿色
		paint.setStrokeWidth(1);// 设置画笔粗细
		paint.setTextSize(30);
		
		canvas.drawText("I", 30, Yoffset+100, paint);
		canvas.drawText("II", 30, Yoffset+100+150, paint);
		canvas.drawText("III", 30, Yoffset+100+300, paint);
		canvas.drawText("aVR", 30, Yoffset+100+450, paint);
		canvas.drawText("aVL", 30, Yoffset+100+600, paint);
		canvas.drawText("aVF", 30, Yoffset+100+750, paint);
		canvas.drawText("V", 30, Yoffset+100+900, paint);
	}
	private void drawSign(Bitmap bitmap){
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);// 画笔为绿色
		paint.setStrokeWidth(2);// 设置画笔粗细
		
		for(int i=1; i<=7; i++){
			canvas.drawPath(signPath(i), paint);
		}
	}
	
	private void drawEcg(Bitmap bitmap){
    	
    	Canvas canvas = new Canvas(bitmap);
    	Paint paint = new Paint();
    	paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);// 画笔为绿色
		paint.setStrokeWidth(2);// 设置画笔粗细
		
		for(int i=1; i<=7; i++){
			canvas.drawPath(leadPath(i), paint);
		}
    }
	
	private Bitmap resizeBmp(Bitmap bitmap){
		if(bitmap != null){
			Matrix matrix = new Matrix();
			matrix.postScale(0.5f, 0.5f);
			Bitmap resizedBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			return resizedBmp;
		}else{
			return null;
		}
	}
	
	private Path leadPath(int leadNo){
    	DataToDraw todraw = new DataToDraw();
    	int[] lead = null;
    	lead = todraw.get8packet(leadNo);
    	Path path = new Path();
    	int y = Yoffset+100+(leadNo-1)*150;
    	path.moveTo(Xoffset, y);
    	for(int i=0; i<lead.length; i++){
    		path.lineTo(i+Xoffset, (y-2*lead[i]));
    	}
    	return path;
    }
	
	private Path signPath(int leadNo){
		Path path = new Path();
		int y = Yoffset+100+(leadNo-1)*150;
		path.moveTo(100, y);
		path.lineTo(120, y);
		path.lineTo(120, y-70);
		path.lineTo(140, y-70);
		path.lineTo(140, y);
		path.lineTo(160, y);
		return path;
	}
	
	// 保存到sdcard
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

}
