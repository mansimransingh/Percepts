package com.mansimransingh.percepts;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class AsyncPost extends AsyncTask<String, String, String> {
	private Context c;
	private String imageString = "";
	private StatusLine statusLine = null;
	private HttpResponse response = null;
    private String[] mData = null;// post data
    private byte[] result = null;
    private Intent i = null;
    private String str = "";
   // private static String response;
    /**
     * constructor
     */
    CallBackListener mListener;

    public void setListener(CallBackListener listener){
      mListener = listener;
    }
    
    public AsyncPost(Context con) {
    	c = con.getApplicationContext();
    }

    /**
     * background
     */
    @Override
    protected String doInBackground(String... params) {
    	 mData = params;
        
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(mData[0]);// in this case, params[0] is URL
        try {
            // set up post data
        	Log.d("DEBUG","Trying to send image data via post");
            ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            
              //$_POST['name'],  $_POST['imgData'] , $width = $_POST['width'], $metaData = $_POST['metaData']
            	String randomNameString = randomString();
            	Log.e("IMAGE NAME", randomNameString);
                nameValuePair.add(new BasicNameValuePair("name", randomNameString));
                nameValuePair.add(new BasicNameValuePair("imgData", mData[1]));
                nameValuePair.add(new BasicNameValuePair("width", "600"));
                nameValuePair.add(new BasicNameValuePair("metaData","Test meta data from new android"));
           
            post.setEntity(new UrlEncodedFormEntity(nameValuePair, "ASCII"));
            response = client.execute(post);
            Log.d("DEBUG","Got a response");
            statusLine = response.getStatusLine();
            Log.d("DEBUG","INT Status code "+statusLine.getStatusCode());
            if(statusLine.getStatusCode() == 200){
            	Log.d("DEBUG", "Happy days");
                result = EntityUtils.toByteArray(response.getEntity());
                str = new String(result, "UTF-8");
                Log.d("RESPONSE",str);
                //setResponse(str);
                imageString = mData[1];
                onPostExecute();
             }
            
        }
        catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        	Log.e("Exception","Unsupported encoding exception in async post");
        }
        catch (Exception e) {
        	Log.e("Exception","Exception in async post"+e.toString());
        	
        }
        
        return str;
    }
    public static String randomString() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(80);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
   
    protected void onPostExecute() {
    	Log.d("EXECUTED","POST EXECUTION METHOD");
    	if(statusLine != null){
    		Log.d("DEBUG","Status line is not null");
    		if(statusLine.getStatusCode() == 200){
    			mListener.callback();

    		}
    	} else {
    		Log.e("ERROR","Status line is NULL");
    	}
    	 
    }


	protected void onProgressUpdate(Integer... values) {
    	Log.d("PROGRESS","VAL: "+values);
    }
	
	protected void onPreExecute(){
		Log.e("PREEXECUTE","PREEXECUTION METHOD");
	}
    /*
    public static void setResponse(String r){
    	response = r;
    }
    
	public String getResponse() {
		// TODO Auto-generated method stub
		return response;
	}*/

	public String getResponse() {
		// TODO Auto-generated method stub
		
		return str;
	}
}