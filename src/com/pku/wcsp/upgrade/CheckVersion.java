package com.pku.wcsp.upgrade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.guo.ecg.client.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Looper;
import android.util.Log;

class PackageVersionInfo {	
	public String AppName = "";
	public String ApkName = "";	
	public String VersionName = "";
	public int VersionCode = 0;
}

public class CheckVersion extends Thread {
	
	private Context context;
	
	private int localVersion = 0;// 本地安装版本
	private int serverVersion = 0;// 服务器版本
	private String verurl = "http://162.105.76.252/myapps/ecg/version.txt";	
	private String strResult = "";
	
	public CheckVersion(Context context){
		this.context = context;
	}
	
	public void run(){
		
		Looper.prepare(); 
		
		PackageVersionInfo verInfo = new PackageVersionInfo(); 
		
		try {
			PackageInfo packageInfo = context.getApplicationContext()
					.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			localVersion = packageInfo.versionCode;
			Log.e("updata","localVersion:"+localVersion);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}				

		HttpPost   httpRequest =new HttpPost(verurl);
		List <NameValuePair> params = new ArrayList <NameValuePair>(); 
		params.add(new BasicNameValuePair("str", "I am Post String"));  

		try 
		{ 			
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8)); 
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			Log.e("strResult",strResult);
			if(httpResponse.getStatusLine().getStatusCode() == 200)  
			{ 
				strResult = EntityUtils.toString(httpResponse.getEntity()); 
				Log.e("strResult",strResult);
			} 
			else	  
			{
				Log.e("update",""+httpResponse.getStatusLine().getStatusCode());
			}
		}
		catch (ClientProtocolException e) 
		{  
			e.printStackTrace(); 
		} 
		catch (IOException e) 
		{  
			e.printStackTrace(); 
		} 
		catch (Exception e) 
		{  
			e.printStackTrace();  
		}
		
		try { 
			String verJson = strResult;
			Log.e("updata","strResult:"+strResult);
			JSONArray array = new JSONArray(verJson);
			JSONObject jsonObject = array.getJSONObject(0);
			verInfo.AppName = jsonObject.getString("appname");     
			verInfo.ApkName = jsonObject.getString("apkname");
			verInfo.VersionName = jsonObject.getString("vername");
			verInfo.VersionCode = jsonObject.getInt("vercode");  
			serverVersion = verInfo.VersionCode;
			Log.e("updata","serverVersion:"+serverVersion);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if (localVersion < serverVersion) {

			// 发现新版本，提示用户更新
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle("软件升级")
					.setMessage("发现新版本,建议立即更新使用.")
					.setPositiveButton("更新",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// 开启更新服务UpdateService
									// 这里为了把update更好模块化，可以传一些updateService依赖的值
									// 如布局ID，资源ID，动态获取的标题,这里以app_name为例
									Intent updateIntent = new Intent(
											context,
											UpdateService.class);
									updateIntent.putExtra(
											"app_name",
											context.getResources().getString(
													R.string.app_name));
									context.startService(updateIntent);
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
			alert.create().show();
		}
		Looper.loop();
	}

}
