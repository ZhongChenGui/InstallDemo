package com.learn.installdemo;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 */
public class HttpUtil {
	private static OkHttpClient client = new OkHttpClient();
	public static int ReadTimeOut = 1000*10;
	public static int ConnectTimeOut = 1000*30;
	public static String ipParam = "";
	private String TAG = "OKHttpUtil";
	private OkHttpClient http;
	private String url;
	public HttpUtil(String url){
		http = client.newBuilder().readTimeout(ReadTimeOut, TimeUnit.MILLISECONDS).connectTimeout(ConnectTimeOut, TimeUnit.MILLISECONDS).build();
		setUrl(url);
	}
	public HttpUtil(String url, boolean certificate){
		http = new OkHttpClient().newBuilder()
				.readTimeout(ReadTimeOut, TimeUnit.MILLISECONDS)//读取超时
				.connectTimeout(ConnectTimeOut, TimeUnit.MILLISECONDS)//连接超时
				.sslSocketFactory(SSLSocketManager.getSSLSocketFactory())//配置
				.hostnameVerifier(SSLSocketManager.getHostnameVerifier())//配置
				.build();
		setUrl(url);
	}
	public void setUrl(String path){
		setPath(path);
	}
	private void setPath(String path){
		url = path;
		if(ipParam.length() > 1){
			if(path.contains("?")){
				url = url+"&ip="+ipParam;
			}else{
				url = url+"?ip="+ipParam;
			}
		}
	}

	public String getString(){
		Request request = new Request.Builder().url(url).get().build();
		Call call = http.newCall(request);
		try {
			Response response = call.execute();
			if (response.code() != 200){
				return "-1";
			}else {
				return response.body().string();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public String getString(String uuid){

		try {
			Request request = new Request.Builder().url(url).addHeader("bvtv-uuid", uuid).build();

			Call call = http.newCall(request);

			Response response = call.execute();
			if (response.code() != 200){
				return "-1";
			}else {
				return response.body().string();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	public JSONObject getJson(){
		String rs = getString();
		if(rs == null || rs.equals("-1"))
			return null;
		JSONObject json = null;
		try {
			json = new JSONObject(rs);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	public JSONObject getJson(String uuid){
		String rs = getString(uuid);
		if(rs == null || rs.equals("-1"))
			return null;
		JSONObject json = null;
		try {
			json = new JSONObject(rs);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	public void post(RequestBody requestBody){

		final Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.build();
		Call call = http.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
			}
		});

	}
}
